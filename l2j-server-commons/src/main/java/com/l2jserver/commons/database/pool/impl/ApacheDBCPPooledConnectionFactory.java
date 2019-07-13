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

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Apache DBCP Pooled Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class ApacheDBCPPooledConnectionFactory implements PooledConnectionFactory {
	
	private final BasicDataSource _dataSource;
	
	public ApacheDBCPPooledConnectionFactory(String driver, String url, String user, String password, int maxPoolSize, int maxIdleTime) {
		_dataSource = new BasicDataSource();
		_dataSource.setDriverClassName(driver);
		_dataSource.setUrl(url);
		_dataSource.setUsername(user);
		_dataSource.setPassword(password);
		_dataSource.setMinIdle(maxIdleTime / 2);
		_dataSource.setMaxIdle(maxIdleTime);
		_dataSource.setMaxOpenPreparedStatements(100);
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
