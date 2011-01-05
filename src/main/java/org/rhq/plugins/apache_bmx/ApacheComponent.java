
package org.rhq.plugins.apache_bmx;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.rhq.core.domain.configuration.Configuration;
import org.rhq.core.domain.measurement.DataType;
import org.rhq.core.domain.measurement.MeasurementDataTrait;
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

    private static final String DEFAULT_BMX_HANDLER_URL = "http://localhost:80/bmx";
    private final Log log = LogFactory.getLog(this.getClass());

    private String bmxUrl;




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
        bmxUrl = conf.getSimpleValue("bmxUrl", DEFAULT_BMX_HANDLER_URL);

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

        URL url = new URL(bmxUrl);
        URLConnection conn = url.openConnection();
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
        Map<String,String> values = parseInput(in);
        in.close();


         for (MeasurementScheduleRequest req : metrics) {
             String name = req.getName();
             if (values.containsKey(name)) {
                 if (req.getDataType()== DataType.TRAIT) {
                     String val = values.get(name);
                     MeasurementDataTrait mdt = new MeasurementDataTrait(req,val);
                     report.addData(mdt);
                 } else {
                    Double val = Double.valueOf(values.get(name));
                    MeasurementDataNumeric mdn = new MeasurementDataNumeric(req,val);
                    report.addData(mdn);
                 }
             }
         }
    }

    private Map<String, String> parseInput(BufferedInputStream in) throws Exception {
        Map<String,String> ret = new HashMap<String, String>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        Mode mode = Mode.NONE;

        while ((line = reader.readLine())!=null) {
            // TODO implement some real state machine / parser here

            if (mode == Mode.NONE && !line.contains("mod_bmx_status"))
                continue;

            // we found the global section
            mode = Mode.GLOBAL;
            while (!(line = reader.readLine()).equals("")) {
                int pos = line.indexOf(":");
                String key = line.substring(0,pos);
                String val = line.substring(pos+2);
                ret.put("global:" + key , val);
            }
            mode = Mode.NONE;
        }

        return ret;
    }


    private static enum Mode {
        NONE,
        GLOBAL,
        VHOST
    }
}
