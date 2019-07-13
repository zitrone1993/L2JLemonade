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
package com.l2jserver.commons.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.pool.impl.ApacheDBCPPooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.BoneCPPooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.C3P0PooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.HikariCPPooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.PooledConnectionFactory;
import com.l2jserver.commons.database.pool.impl.ViburDBCPPooledConnectionFactory;

/**
 * Connection Factory implementation.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class ConnectionFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);
	
	private final PooledConnectionFactory pooledConnectionFactory;
	
	private String connectionPool;
	
	private String url;
	
	private String user;
	
	private String password;
	
	private String driver;
	
	private int maxPoolSize;
	
	private int maxIdleTime;
	
	private ConnectionFactory(Builder builder) {
		this.connectionPool = builder.connectionPool;
		this.url = builder.url;
		this.user = builder.user;
		this.password = builder.password;
		this.driver = builder.driver;
		this.maxPoolSize = builder.maxPoolSize;
		this.maxIdleTime = builder.maxIdleTime;
		
		switch (connectionPool) {
			default:
			case "HikariCP": {
				pooledConnectionFactory = new HikariCPPooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
			case "C3P0": {
				pooledConnectionFactory = new C3P0PooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
			case "BoneCP": {
				pooledConnectionFactory = new BoneCPPooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
			case "ApacheDBCP": {
				pooledConnectionFactory = new ApacheDBCPPooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
			case "ViburDBCP": {
				pooledConnectionFactory = new ViburDBCPPooledConnectionFactory(driver, url, user, password, maxPoolSize, maxIdleTime);
				break;
			}
		}
		LOG.info("Using {} connection pool.", pooledConnectionFactory.getClass().getSimpleName().replace("PooledConnectionFactory", ""));
	}
	
	public static PooledConnectionFactory getInstance() {
		return Builder.INSTANCE.pooledConnectionFactory;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		protected static volatile ConnectionFactory INSTANCE;
		
		private String connectionPool;
		private String url;
		private String user;
		private String password;
		private String driver;
		private int maxPoolSize;
		private int maxIdleTime;
		
		private Builder() {
		}
		
		public Builder withConnectionPool(String connectionPool) {
			this.connectionPool = connectionPool;
			return this;
		}
		
		public Builder withUrl(String url) {
			this.url = url;
			return this;
		}
		
		public Builder withUser(String user) {
			this.user = user;
			return this;
		}
		
		public Builder withPassword(String password) {
			this.password = password;
			return this;
		}
		
		public Builder withDriver(String driver) {
			this.driver = driver;
			return this;
		}
		
		public Builder withMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
			return this;
		}
		
		public Builder withMaxIdleTime(int maxIdleTime) {
			this.maxIdleTime = maxIdleTime;
			return this;
		}
		
		public void build() {
			if (INSTANCE == null) {
				synchronized (this) {
					if (INSTANCE == null) {
						INSTANCE = new ConnectionFactory(this);
					} else {
						LOG.warn("Trying to build another Connection Factory!");
					}
				}
			}
		}
	}
}
