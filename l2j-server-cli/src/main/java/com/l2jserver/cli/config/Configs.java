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
package com.l2jserver.cli.config;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import com.l2jserver.cli.config.impl.GameServerConfigurationImpl;
import com.l2jserver.cli.config.impl.LoginServerConfigurationImpl;

/**
 * Configs.
 * @author Zoey76
 */
public enum Configs {
	INSTANCE;
	
	private Configuration config;
	
	private GameServerConfiguration gameServerConfig;
	
	private LoginServerConfiguration loginServerConfiguration;
	
	Configs() {
		final var configs = new Configurations();
		try {
			config = configs.properties(new File("config.properties"));
			
			gameServerConfig = new GameServerConfigurationImpl(config);
			
			loginServerConfiguration = new LoginServerConfigurationImpl(config);
		} catch (Exception ex) {
			System.err.println("There has been an error loading configs!");
			ex.printStackTrace();
		}
	}
	
	public static GameServerConfiguration gameServer() {
		return INSTANCE.gameServerConfig;
	}
	
	public static LoginServerConfiguration loginServer() {
		return INSTANCE.loginServerConfiguration;
	}
}
