/*
 * Jabox Open Source Version
 * Copyright (C) 2009-2010 Dimitris Kapanidis                                                                                                                          
 * 
 * This file is part of Jabox
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.jabox.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jabox.apis.cis.CISConnector;
import org.jabox.apis.cis.CISConnectorConfig;
import org.jabox.apis.cqm.CQMConnector;
import org.jabox.apis.cqm.CQMConnectorConfig;
import org.jabox.apis.its.ITSConnector;
import org.jabox.apis.its.ITSConnectorConfig;
import org.jabox.apis.rms.RMSConnector;
import org.jabox.apis.rms.RMSConnectorConfig;
import org.jabox.apis.scm.SCMConnector;
import org.jabox.apis.scm.SCMConnectorConfig;

import com.google.inject.Inject;

public class Manager {

	private final Set<ITSConnector> _itsConnectors;
	private final Set<CISConnector> _cisConnectors;
	private final Set<SCMConnector> _scmConnectors;
	private final Set<RMSConnector> _rmsConnectors;
	private final Set<CQMConnector> _cqmConnectors;

	public Manager() {
		_itsConnectors = null;
		_cisConnectors = null;
		_scmConnectors = null;
		_rmsConnectors = null;
		_cqmConnectors = null;
	}

	@Inject
	public Manager(Set<ITSConnector> its, Set<CISConnector> cis,
			Set<SCMConnector> scm, Set<RMSConnector> rms, Set<CQMConnector> cqm) {
		this._itsConnectors = its;
		this._cisConnectors = cis;
		this._scmConnectors = scm;
		this._rmsConnectors = rms;
		this._cqmConnectors = cqm;
	}

	public void printAll() {
		for (ITSConnector<ITSConnectorConfig> connector : _itsConnectors) {
			System.out.println("ITS: " + connector.getName());
		}
		for (CISConnector connector : _cisConnectors) {
			System.out.println("CIS: " + connector.getName());
		}
	}

	public Connector getConnectorInstance(final ConnectorConfig connectorConfig) {
		List<Connector> connectors = new ArrayList<Connector>();
		connectors.addAll(_itsConnectors);
		connectors.addAll(_scmConnectors);
		connectors.addAll(_rmsConnectors);
		connectors.addAll(_cisConnectors);
		connectors.addAll(_cqmConnectors);

		if (connectorConfig == null) {
			return null;
		}

		for (Connector connectorInstance : connectors) {
			if (connectorConfig.getPluginId().equals(connectorInstance.getId())) {
				return connectorInstance;
			}
		}
		return null;
	}

	public SCMConnector<SCMConnectorConfig> getScmConnectorInstance(
			SCMConnectorConfig scmc) {
		return null;
	}

	public ITSConnector<ITSConnectorConfig> getItsConnectorInstance(
			ITSConnectorConfig config) {
		if (config == null) {
			return null;
		}

		for (ITSConnector connectorInstance : _itsConnectors) {
			if (config.equals(connectorInstance.getName())) {
				return connectorInstance;
			}
		}
		return null;
	}

	public RMSConnector getRmsConnectorInstance(RMSConnectorConfig config) {
		if (config == null) {
			return null;
		}

		for (RMSConnector connectorInstance : _rmsConnectors) {
			if (config.equals(connectorInstance.getName())) {
				return connectorInstance;
			}
		}
		return null;
	}

	public CISConnector getCisConnectorInstance(CISConnectorConfig config) {
		if (config == null) {
			return null;
		}

		for (CISConnector connectorInstance : _cisConnectors) {
			if (config.equals(connectorInstance.getName())) {
				return connectorInstance;
			}
		}
		return null;
	}

	public CQMConnector getCqmConnectorInstance(CQMConnectorConfig config) {
		if (config == null) {
			return null;
		}

		for (CQMConnector connectorInstance : _cqmConnectors) {
			if (config.equals(connectorInstance.getName())) {
				return connectorInstance;
			}
		}
		return null;
	}

}
