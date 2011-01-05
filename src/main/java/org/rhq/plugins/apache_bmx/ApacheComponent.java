
package org.rhq.plugins.apache_bmx;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.pluginapi.inventory.InvalidPluginConfigurationException;
import org.rhq.core.domain.measurement.AvailabilityType;
import org.rhq.core.domain.measurement.MeasurementDataNumeric;
import org.rhq.core.domain.measurement.MeasurementReport;
import org.rhq.core.domain.measurement.MeasurementScheduleRequest;
import org.rhq.core.pluginapi.inventory.ResourceComponent;
import org.rhq.core.pluginapi.inventory.ResourceContext;
import org.rhq.core.pluginapi.measurement.MeasurementFacet;


public class ApacheComponent implements ResourceComponent
, MeasurementFacet
{
    private final Log log = LogFactory.getLog(this.getClass());

    private static final int CHANGEME = 1; // TODO remove or change this




    /**
     * Return availability of this resource
     *  @see org.rhq.core.pluginapi.inventory.ResourceComponent#getAvailability()
     */
    public AvailabilityType getAvailability() {
        // TODO supply real implementation
        return AvailabilityType.UP;
    }


    /**
     * Start the resource connection
     * @see org.rhq.core.pluginapi.inventory.ResourceComponent#start(org.rhq.core.pluginapi.inventory.ResourceContext)
     */
    public void start(ResourceContext context) throws InvalidPluginConfigurationException, Exception {

        Configuration conf = context.getPluginConfiguration();
        // TODO add code to start the resource / connection to it


    }


    /**
     * Tear down the rescource connection
     * @see org.rhq.core.pluginapi.inventory.ResourceComponent#stop()
     */
    public void stop() {


    }



    /**
     * Gather measurement data
     *  @see org.rhq.core.pluginapi.measurement.MeasurementFacet#getValues(org.rhq.core.domain.measurement.MeasurementReport, java.util.Set)
     */
    public  void getValues(MeasurementReport report, Set<MeasurementScheduleRequest> metrics) throws Exception {

         for (MeasurementScheduleRequest req : metrics) {
            if (req.getName().equals("dummyMetric")) {
                 MeasurementDataNumeric res = new MeasurementDataNumeric(req, Double.valueOf(CHANGEME));
                 report.addData(res);
            }
            // TODO add more metrics here
         }
    }






}
