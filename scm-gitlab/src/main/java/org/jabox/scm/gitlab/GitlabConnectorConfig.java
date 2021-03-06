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
package org.jabox.scm.gitlab;

import org.jabox.model.DeployerConfig;
import org.jabox.model.Server;
import org.jabox.scm.git.IGITConnectorConfig;

public class GitlabConnectorConfig extends DeployerConfig implements
        IGITConnectorConfig {
    private static final long serialVersionUID = -830757629457448866L;

    private static final String GIT_GITLAB_COM = "git://github.com/";

    private static final String HTTPS_GITLAB_COM = "https://github.com/";

    private static final String DOT_GIT = ".git";

    public GitlabConnectorConfig() {
        pluginId = GitlabConnector.ID;
    }

    public String username;

    public String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Server getServer() {
        return server;
    }

    public String getScmUrl() {
        String scmURL = GIT_GITLAB_COM + getUsername() + "/";
        return scmURL;
    }

    public String getProjectScmUrl(final String projectName) {
        return getScmUrl() + projectName + DOT_GIT;
    }

    public String getScmMavenPrefix() {
        return "scm:git:";
    }
}
