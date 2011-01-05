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

import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.configuration.Property;
import org.rhq.core.domain.configuration.PropertySimple;
import org.rhq.core.pluginapi.inventory.DiscoveredResourceDetails;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.pluginapi.inventory.ProcessScanResult;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryComponent;
import org.rhq.core.pluginapi.inventory.ResourceDiscoveryContext;


/**
 * Discovery class
 */
@SuppressWarnings("unused")
public class ApacheDiscovery implements ResourceDiscoveryComponent {


    private final Log log = LogFactory.getLog(this.getClass());


    /**
     * Run the discovery
     */
    public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext discoveryContext) throws Exception {

        Set<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>();

        /**
         * Discover apache with mod_bmx by pinging ist bmx-handler
         *
         * A discovered resource must have a unique key, that must
         * stay the same when the resource is discovered the next
         * time
         */
        Configuration config = discoveryContext.getDefaultPluginConfiguration();
        String host = config.getSimpleValue("host","localhost");
        String portS = config.getSimpleValue("port","80");
        int port = Integer.valueOf(portS);
        String handler = config.getSimpleValue("bmxHandler","/bmx");

        URL url = new URL("http",host,port,handler);
        URLConnection conn = url.openConnection();
        conn.getExpiration();

        String urlString = url.toString();

        config.put(new PropertySimple("bmxUrl",urlString));
        config.put(new PropertySimple("vhost","_GLOBAL_"));

        DiscoveredResourceDetails detail =  new DiscoveredResourceDetails(
            discoveryContext.getResourceType(), // ResourceType
            urlString, // Resource key
            "Apache server at " + urlString, // Resource Name
            null, // Version - TODO: get from mod_bmx
            "Apache httpd with mod_bmx", // Description
            config, // COnfiguration
            null // process scans
        );


        // Add to return values
        discoveredResources.add(detail);
        log.info("Discovered new ... httpd at " + urlString);

        return discoveredResources;

    }
}
