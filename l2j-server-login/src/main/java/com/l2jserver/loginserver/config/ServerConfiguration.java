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
package com.l2jserver.loginserver.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

/**
 * Server Configuration.
 * @author Zoey76
 * @version 2.6.1.1
 */
@Sources({
	"file:./config/server.properties",
	"classpath:config/server.properties"
})
public interface ServerConfiguration extends Config {
	
	@Key("EnableUPnP")
	boolean isUPnPEnabled();
	
	@Key("Host")
	String getHost();
	
	@Key("Port")
	int getPort();
	
	@Key("GameServerHost")
	String getGameServerHost();
	
	@Key("GameServerPort")
	int getGameServerPort();
	
	@Key("LoginTryBeforeBan")
	int getLoginTryBeforeBan();
	
	@Key("LoginBlockAfterBan")
	int getLoginBlockAfterBan();
	
	@Key("AcceptNewGameServer")
	boolean isAcceptNetGameServer();
	
	@Key("EnableFloodProtection")
	boolean isFloodProtectionEnabled();
	
	@Key("FastConnectionLimit")
	int getFastConnectionLimit();
	
	@Key("NormalConnectionTime")
	int getNormalConnectionTime();
	
	@Key("FastConnectionTime")
	int getFastConnectionTime();
	
	@Key("MaxConnectionPerIP")
	int getMaxConnectionPerIP();
	
	@Key("DatabaseDriver")
	String getDatabaseDriver();
	
	@Key("DatabaseURL")
	String getDatabaseURL();
	
	@Key("DatabaseUser")
	String getDatabaseUser();
	
	@Key("DatabasePassword")
	String getDatabasePassword();
	
	@Key("DatabaseConnectionPool")
	String getDatabaseConnectionPool();
	
	@Key("DatabaseMaximumPoolSize")
	int getDatabaseMaximumPoolSize();
	
	@Key("DatabaseMaximumIdleTime")
	int getDatabaseMaximumIdleTime();
	
	@Key("DatabaseConnectionCloseTime")
	int getDatabaseConnectionCloseTime();
	
	@Key("ShowLicence")
	boolean showLicense();
	
	@Key("AutoCreateAccounts")
	boolean autoCreateAccounts();
	
	@Key("DatapackRoot")
	String getDatapackRoot();
	
	@Key("Debug")
	boolean isDebug();
	
	@Key("LoginRestartSchedule")
	boolean isLoginRestatEnabled();
	
	@Key("LoginRestartTime")
	int getLoginRestartTime();
}