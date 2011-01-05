/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.plugins.apache_bmx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;

/**
 * Discover VHhosts
 * @author Heiko W. Rupp
 */
public class ApacheVhostDiscovery implements ResourceDiscoveryComponent<ApacheComponent> {

    private final Log log = LogFactory.getLog(ApacheVhostDiscovery.class);
    Pattern hostPattern = Pattern.compile(".*Host=(.+),.*");


    public Set<DiscoveredResourceDetails> discoverResources(
        ResourceDiscoveryContext<ApacheComponent> discoveryContext) throws
        InvalidPluginConfigurationException, Exception {

        Set<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>();
        Configuration config = discoveryContext.getDefaultPluginConfiguration();

        String bmxUrl = discoveryContext.getParentResourceComponent().getBmxUrl();
        config.put(new PropertySimple("bmxUrl",bmxUrl));

        URL url = new URL(bmxUrl);
        URLConnection conn = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;

        while ((line = reader.readLine())!=null) {
            if (line.startsWith("Name: mod_bmx_vhost:Type=forever") && !line.contains("_GLOBAL_")) {
                Matcher m = hostPattern.matcher(line);
                m.matches();
                String host = m.group(1);
                config.put(new PropertySimple("vhost",host));

                DiscoveredResourceDetails detail = new DiscoveredResourceDetails(
                    discoveryContext.getResourceType(), // ResourceType
                    host, // key
                    "VHost " +host, // name
                    null, // TODO get from parent/mod_bmx
                    "A virtual host", // description
                    config, // plugin config
                    null // process scan
                );
                discoveredResources.add(detail);
                log.info("Found vhost [" + host + "]");
            }
        }
        reader.close();

        return discoveredResources;
    }
}
