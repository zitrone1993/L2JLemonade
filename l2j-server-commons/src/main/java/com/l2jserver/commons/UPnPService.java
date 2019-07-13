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
package com.l2jserver.commons;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * UPnP Service.
 * @author UnAfraid
 * @author Zoey76
 * @version 2.6.1.0
 */
public class UPnPService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UPnPService.class);
	
	private static final String PROTOCOL = "TCP";
	
	private final GatewayDiscover _gatewayDiscover = new GatewayDiscover();
	
	private GatewayDevice _activeGW;
	
	private final Set<Integer> ports = new HashSet<>();
	
	protected UPnPService() {
		// Do nothing.
	}
	
	public void load(int port, String description) {
		try {
			LOG.info("Looking for UPnP Gateway Devices...");
			
			final var gateways = _gatewayDiscover.discover();
			if (gateways.isEmpty()) {
				LOG.info("No UPnP gateways has been found.");
				return;
			}
			
			_activeGW = _gatewayDiscover.getValidGateway();
			if (_activeGW == null) {
				LOG.info("No active UPnP gateway found.");
				return;
			}
			
			LOG.info("Using UPnP gateway: {}", _activeGW.getFriendlyName());
			
			LOG.info("Using local address: {} External address: {}", _activeGW.getLocalAddress().getHostAddress(), _activeGW.getExternalIPAddress());
			
			addPortMapping(port, description);
		} catch (Exception ex) {
			LOG.warn("There has been an error loading {}[{}]!", description, port, ex);
		}
	}
	
	public void removeAllPorts() throws Exception {
		if (_activeGW != null) {
			ports.forEach(p -> deletePortMapping(p));
		}
	}
	
	private void addPortMapping(int port, String description) throws IOException, SAXException {
		final var portMapping = new PortMappingEntry();
		final var localAddress = _activeGW.getLocalAddress();
		
		// Attempt to re-map
		if (_activeGW.getSpecificPortMappingEntry(port, PROTOCOL, portMapping)) {
			_activeGW.deletePortMapping(port, PROTOCOL);
		}
		
		if (_activeGW.addPortMapping(port, port, localAddress.getHostAddress(), PROTOCOL, description)) {
			LOG.info("Mapping successful on {}:{}", localAddress.getHostAddress(), port);
			ports.add(port);
		} else {
			LOG.info("Mapping failed on [{}:{}] - Already mapped?", localAddress.getHostAddress(), port);
		}
	}
	
	private void deletePortMapping(int port) {
		try {
			if (_activeGW.deletePortMapping(port, PROTOCOL)) {
				LOG.info("Mapping was deleted from [{}:{}]", _activeGW.getLocalAddress().getHostAddress(), port);
			}
		} catch (Exception ex) {
			LOG.warn("There has been an error removing port {} from UPnP!", port, ex);
		}
	}
	
	public static UPnPService getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final UPnPService INSTANCE = new UPnPService();
	}
}
