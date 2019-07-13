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

/**
 * Database configuration implementation.
 * @author Zoey76
 */
public class DatabaseConfigurationImpl implements DatabaseConfiguration {
	
	private static final String PREFIX = ".db.";
	
	private String section;
	
	private Configuration config;
	
	public DatabaseConfigurationImpl(String section, Configuration config) {
		this.config = config;
		this.section = section;
	}
	
	@Override
	public String name() {
		return config.getString(section + PREFIX + "name");
	}
	
	@Override
	public String host() {
		return config.getString(section + PREFIX + "host");
	}
	
	@Override
	public int port() {
		return config.getInt(section + PREFIX + "port");
	}
	
	@Override
	public String user() {
		return config.getString(section + PREFIX + "user");
	}
	
	@Override
	public String password() {
		return config.getString(section + PREFIX + "password");
	}
	
	@Override
	public DatabaseConfiguration withName(String name) {
		config.setProperty(section + PREFIX + "name", name);
		return this;
	}
	
	@Override
	public DatabaseConfiguration withHost(String host) {
		config.setProperty(section + PREFIX + "host", host);
		return this;
	}
	
	@Override
	public DatabaseConfiguration withPort(int port) {
		config.setProperty(section + PREFIX + "port", port);
		return this;
	}
	
	@Override
	public DatabaseConfiguration withUser(String user) {
		config.setProperty(section + PREFIX + "user", user);
		return this;
	}
	
	@Override
	public DatabaseConfiguration withPassword(String password) {
		config.setProperty(section + PREFIX + "password", password);
		return this;
	}
}
