package io.smalldata.beehiveapp.RSRPBackendSupport;

import org.researchsuite.rsrp.Core.RSRPIntermediateResult;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by christinatsangouri on 3/7/18.
 */

public class PAMRaw extends RSRPIntermediateResult {
    public static String TYPE = "PAMRaw";

    private Map<String, Serializable> pamChoice;

    public PAMRaw(
            UUID uuid,
            String taskIdentifier,
            UUID taskRunUUID,
            Map<String, Serializable> choice) {

        super(TYPE, uuid, taskIdentifier, taskRunUUID);
        this.pamChoice = choice;

    }

    public Map<String, Serializable> getPamChoice() {
        return pamChoice;
    }
}