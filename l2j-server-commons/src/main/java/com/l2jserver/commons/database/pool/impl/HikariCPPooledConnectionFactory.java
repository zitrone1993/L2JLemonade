/*
 * Copyright © 2004-2019 L2J Server
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
package com.l2jserver.commons.database.pool.impl;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

/**
 * HikariCP Pooled Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class HikariCPPooledConnectionFactory implements PooledConnectionFactory {
	
	private static final int MAX_LIFETIME = 15;
	
	private final HikariDataSource _dataSource;
	
	public HikariCPPooledConnectionFactory(String driver, String url, String user, String password, int maxPoolSize, int maxIdleTime) {
		_dataSource = new HikariDataSource();
		_dataSource.setDriverClassName(driver);
		_dataSource.setJdbcUrl(url);
		_dataSource.setUsername(user);
		_dataSource.setPassword(password);
		_dataSource.setMaximumPoolSize(maxPoolSize);
		_dataSource.setIdleTimeout(SECONDS.toMillis(maxIdleTime));
		_dataSource.setMaxLifetime(MINUTES.toMillis(MAX_LIFETIME));
	}
	
	@Override
	public void close() {
		try {
			_dataSource.close();
		} catch (Exception e) {
			LOG.warn("There has been a problem closing the data source!", e);
		}
	}
	
	@Override
	public DataSource getDataSource() {
		return _dataSource;
	}
}
