/*
 * Copyright © 2019 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.loginserver;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.UPnPService;
import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.loginserver.config.Configuration;
import com.l2jserver.loginserver.mail.MailSystem;
import com.l2jserver.loginserver.network.L2LoginClient;
import com.l2jserver.loginserver.network.L2LoginPacketHandler;
import com.l2jserver.loginserver.status.Status;
import com.l2jserver.mmocore.SelectorConfig;
import com.l2jserver.mmocore.SelectorThread;

/**
 * Login Server.
 * @author KenM
 * @author Zoey76
 * @version 2.6.1.0
 */
public final class LoginServer {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoginServer.class);
	
	private static final String BANNED_IPS = "/config/banned_ip.cfg";
	
	public static final int PROTOCOL_REV = 0x0106;
	
	private static LoginServer _instance;
	
	private GameServerListener _gameServerListener;
	
	private SelectorThread<L2LoginClient> _selectorThread;
	
	private Status _statusServer;
	
	private Thread _restartLoginServer;
	
	public static void main(String[] args) {
		new LoginServer();
	}
	
	public static LoginServer getInstance() {
		return _instance;
	}
	
	private LoginServer() {
		_instance = this;
		
		// Prepare Database
		ConnectionFactory.builder() //
			.withDriver(Configuration.getInstance().server().getDatabaseDriver()) //
			.withUrl(Configuration.getInstance().server().getDatabaseURL()) //
			.withUser(Configuration.getInstance().server().getDatabaseUser()) //
			.withPassword(Configuration.getInstance().server().getDatabasePassword()) //
			.withConnectionPool(Configuration.getInstance().server().getDatabaseConnectionPool()) //
			.withMaxIdleTime(Configuration.getInstance().server().getDatabaseMaximumIdleTime()) //
			.withMaxPoolSize(Configuration.getInstance().server().getDatabaseMaximumPoolSize()) //
			.build();
		
		LoginController.getInstance();
		
		GameServerTable.getInstance();
		
		loadBanFile();
		
		if (Configuration.getInstance().email().isEnabled()) {
			MailSystem.getInstance();
		}
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Configuration.getInstance().mmo().getMaxReadPerPass();
		sc.MAX_SEND_PER_PASS = Configuration.getInstance().mmo().getMaxSendPerPass();
		sc.SLEEP_TIME = Configuration.getInstance().mmo().getSleepTime();
		sc.HELPER_BUFFER_COUNT = Configuration.getInstance().mmo().getHelperBufferCount();
		
		final L2LoginPacketHandler loginPacketHandler = new L2LoginPacketHandler();
		final SelectorHelper selectorHelper = new SelectorHelper();
		try {
			_selectorThread = new SelectorThread<>(sc, selectorHelper, loginPacketHandler, selectorHelper, selectorHelper);
		} catch (Exception ex) {
			LOG.error("Failed to open Selector!", ex);
			System.exit(1);
		}
		
		try {
			_gameServerListener = new GameServerListener();
			_gameServerListener.start();
			LOG.info("Listening for game servers on {}:{}.", Configuration.getInstance().server().getGameServerHost(), Configuration.getInstance().server().getGameServerPort());
		} catch (Exception ex) {
			LOG.error("Failed to start the Game Server Listener!", ex);
			System.exit(1);
		}
		
		if (Configuration.getInstance().telnet().isEnabled()) {
			try {
				_statusServer = new Status();
				_statusServer.start();
			} catch (Exception ex) {
				LOG.warn("Failed to start the Telnet Server!", ex);
			}
		} else {
			LOG.info("Telnet server is currently disabled.");
		}
		
		InetAddress bindAddress = null;
		if (!Configuration.getInstance().server().getHost().equals("*")) {
			try {
				bindAddress = InetAddress.getByName(Configuration.getInstance().server().getGameServerHost());
			} catch (Exception ex) {
				LOG.warn("The Login Server bind address is invalid, using all avaliable IPs!", ex);
			}
		}
		try {
			_selectorThread.openServerSocket(bindAddress, Configuration.getInstance().server().getPort());
			_selectorThread.start();
			LOG.info("Login Server is now listening on {}:{}.", Configuration.getInstance().server().getHost(), Configuration.getInstance().server().getPort());
		} catch (Exception ex) {
			LOG.error("Failed to open server socket!", ex);
			System.exit(1);
		}
		
		if (Configuration.getInstance().server().isUPnPEnabled()) {
			UPnPService.getInstance().load(Configuration.getInstance().server().getPort(), "L2J Login Server");
		}
	}
	
	public Status getStatusServer() {
		return _statusServer;
	}
	
	public GameServerListener getGameServerListener() {
		return _gameServerListener;
	}
	
	private void loadBanFile() {
		try (var fis = getClass().getResourceAsStream(BANNED_IPS);
			var is = new InputStreamReader(fis);
			var lnr = new LineNumberReader(is)) {
			lnr.lines() //
				.map(String::trim) //
				.filter(l -> !l.isEmpty() && (l.charAt(0) != '#')) //
				.forEach(line -> {
					String[] parts = line.split("#", 2); // address[ duration][ # comments]
					line = parts[0];
					parts = line.split("\\s+"); // durations might be aligned via multiple spaces
					String address = parts[0];
					long duration = 0;
					
					if (parts.length > 1) {
						try {
							duration = Long.parseLong(parts[1]);
						} catch (Exception ex) {
							LOG.warn("Incorrect ban duration {} on line {} on file {}!", parts[1], lnr.getLineNumber(), BANNED_IPS, ex);
							return;
						}
					}
					
					try {
						LoginController.getInstance().addBanForAddress(address, duration);
					} catch (Exception ex) {
						LOG.warn("Invalid address {} on line {} on file {}!", address, lnr.getLineNumber(), BANNED_IPS, ex);
					}
				});
		} catch (Exception ex) {
			LOG.warn("Error while reading the bans file {}!", BANNED_IPS, ex);
		}
		LOG.info("Loaded {} banned IPs.", LoginController.getInstance().getBannedIps().size());
		
		if (Configuration.getInstance().server().isLoginRestatEnabled()) {
			_restartLoginServer = new LoginServerRestart();
			_restartLoginServer.setDaemon(true);
			_restartLoginServer.start();
			LOG.info("Scheduled restart after {} hours.", Configuration.getInstance().server().getLoginRestartTime());
		}
	}
	
	class LoginServerRestart extends Thread {
		public LoginServerRestart() {
			setName("LoginServerRestart");
		}
		
		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					Thread.sleep(Configuration.getInstance().server().getLoginRestartTime() * 3600000);
				} catch (InterruptedException e) {
					return;
				}
				shutdown(true);
			}
		}
	}
	
	public void shutdown(boolean restart) {
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}
