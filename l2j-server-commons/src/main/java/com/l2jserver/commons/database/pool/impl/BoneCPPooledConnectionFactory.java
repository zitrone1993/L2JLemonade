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

import static java.util.concurrent.TimeUnit.SECONDS;

import javax.sql.DataSource;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * BoneCP Pooled Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class BoneCPPooledConnectionFactory implements PooledConnectionFactory {
	
	private static final int PARTITION_COUNT = 5;
	
	private final BoneCPDataSource _dataSource;
	
	public BoneCPPooledConnectionFactory(String driver, String url, String user, String password, int maxConnections, int maxIdleTime) {
		_dataSource = new BoneCPDataSource();
		_dataSource.setDriverClass(driver);
		_dataSource.setJdbcUrl(url);
		_dataSource.setUsername(user);
		_dataSource.setPassword(password);
		_dataSource.setPartitionCount(PARTITION_COUNT);
		_dataSource.setMaxConnectionsPerPartition(maxConnections);
		_dataSource.setIdleConnectionTestPeriod(maxIdleTime, SECONDS);
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
