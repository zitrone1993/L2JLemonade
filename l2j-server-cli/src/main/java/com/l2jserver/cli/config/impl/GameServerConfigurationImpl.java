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
package com.l2jserver.cli.config.impl;

import org.apache.commons.configuration2.Configuration;

import com.l2jserver.cli.config.DatabaseConfiguration;
import com.l2jserver.cli.config.GameServerConfiguration;

/**
 * Game Server configuration implementation.
 * @author Zoey76
 */
public class GameServerConfigurationImpl implements GameServerConfiguration {
	
	private DatabaseConfiguration databaseConfiguration;
	
	public GameServerConfigurationImpl(Configuration config) {
		databaseConfiguration = new DatabaseConfigurationImpl("gs", config);
	}
	
	@Override
	public DatabaseConfiguration db() {
		return databaseConfiguration;
	}
}
