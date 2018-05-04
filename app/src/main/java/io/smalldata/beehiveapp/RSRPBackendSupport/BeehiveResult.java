package io.smalldata.beehiveapp.RSRPBackendSupport;

import org.researchsuite.rsrp.Core.RSRPIntermediateResult;

import java.util.UUID;

/**
 * Created by christinatsangouri on 4/27/18.
 */

public class BeehiveResult extends RSRPIntermediateResult {

    public static String TYPE = "DemographicsResult";

    private Object[] demographyResults;
    private String[] header;

    public BeehiveResult(UUID uuid, String taskIdentifier, UUID taskRunUUID, Object demographyResults[], String headerValues[]) {
        super(TYPE, uuid, taskIdentifier, taskRunUUID);

        this.demographyResults = demographyResults;
        this.header = headerValues;

    }


    public Object[] getDemographyResults() {
        return demographyResults;
    }

    public String[] getHeaderValues(){
        return header;
    }


}