package org.rhq.plugins.apache_bmx;

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
public class ApacheDiscovery implements ResourceDiscoveryComponent {


    private final Log log = LogFactory.getLog(this.getClass());


    /**
     * Run the discovery
     */
    public Set<DiscoveredResourceDetails> discoverResources(ResourceDiscoveryContext discoveryContext) throws Exception {

        Set<DiscoveredResourceDetails> discoveredResources = new HashSet<DiscoveredResourceDetails>();

        /**
         * TODO : do your discovery here
         * A discovered resource must have a unique key, that must
         * stay the same when the resource is discovered the next
         * time
         */
        DiscoveredResourceDetails detail = null; // new DiscoveredResourceDetails(  );


        // Add to return values
        discoveredResources.add(detail);
        log.info("Discovered new ... TODO "); // TODO change

        return discoveredResources;

    }
}
