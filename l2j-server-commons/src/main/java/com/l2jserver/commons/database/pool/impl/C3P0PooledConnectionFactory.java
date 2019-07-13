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

import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * C3P0 Pooled Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class C3P0PooledConnectionFactory implements PooledConnectionFactory {
	
	private static final int MIN_CONNECTIONS = 2;
	
	private final ComboPooledDataSource _dataSource;
	
	public C3P0PooledConnectionFactory(String driver, String url, String user, String password, int maxPoolSize, int maxIdleTime) {
		if (maxPoolSize < 2) {
			LOG.warn("A minimum of {} database connections are required.", MIN_CONNECTIONS);
			maxPoolSize = 2;
		}
		
		_dataSource = new ComboPooledDataSource();
		_dataSource.setAutoCommitOnClose(true);
		_dataSource.setInitialPoolSize(10);
		_dataSource.setMinPoolSize(10);
		_dataSource.setMaxPoolSize(Math.max(10, maxPoolSize));
		_dataSource.setAcquireRetryAttempts(0); // try to obtain connections indefinitely (0 = never quit)
		_dataSource.setAcquireRetryDelay(500); // 500 milliseconds wait before try to acquire connection again
		_dataSource.setCheckoutTimeout(0); // 0 = wait indefinitely for new connection if pool is exhausted
		_dataSource.setAcquireIncrement(5); // if pool is exhausted, get 5 more connections at a time
		// cause there is a "long" delay on acquire connection
		// so taking more than one connection at once will make connection pooling
		// more effective.
		
		_dataSource.setAutomaticTestTable("connection_test_table");
		_dataSource.setTestConnectionOnCheckin(false);
		_dataSource.setIdleConnectionTestPeriod(3600);
		_dataSource.setMaxIdleTime(maxIdleTime);
		_dataSource.setMaxStatementsPerConnection(100);
		_dataSource.setBreakAfterAcquireFailure(false);
		
		try {
			_dataSource.setDriverClass(driver);
		} catch (Exception ex) {
			LOG.error("There has been a problem setting the driver class!", ex);
		}
		
		_dataSource.setJdbcUrl(url);
		_dataSource.setUser(user);
		_dataSource.setPassword(password);
		
		try {
			_dataSource.getConnection().close();
		} catch (SQLException ex) {
			LOG.warn("There has been a problem closing the test connection!", ex);
		}
		
		LOG.debug("Database connection working.");
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
