package org.jabox.its.bugzilla;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.jabox.apis.its.ITSConnectorConfig;
import org.jabox.model.DeployerConfig;

@Entity
@DiscriminatorValue(BugzillaRepository.ID)
public class BugzillaRepositoryConfig extends DeployerConfig implements
		ITSConnectorConfig {
	private static final long serialVersionUID = -4830931405902901630L;

	public BugzillaRepositoryConfig() {
		pluginId = BugzillaRepository.ID;
	}
}