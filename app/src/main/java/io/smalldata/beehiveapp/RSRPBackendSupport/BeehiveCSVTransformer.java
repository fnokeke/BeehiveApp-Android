package io.smalldata.beehiveapp.RSRPBackendSupport;

import android.support.annotation.Nullable;

import org.researchstack.backbone.result.StepResult;
import org.researchsuite.rsrp.Core.RSRPFrontEndServiceProvider.spi.RSRPFrontEnd;
import org.researchsuite.rsrp.Core.RSRPIntermediateResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by christinatsangouri on 4/27/18.
 */

public class BeehiveCSVTransformer implements RSRPFrontEnd {

    @Nullable
    public <T> T extractResult(Map<String, Object> parameters, String identifier) {

        Object param = parameters.get(identifier);
        if (param != null && (param instanceof StepResult)) {
            StepResult stepResult = (StepResult)param;
            if (stepResult.getResult() != null) {
                return (T)stepResult.getResult();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public RSRPIntermediateResult transform(String taskIdentifier, UUID taskRunUUID, Map<String, Object> parameters) {

        ArrayList<Object> results = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        keys.add("timestamp");

        for (String key : parameters.keySet()) {
            if (!Objects.equals(key, "schemaID")){
                results.add(extractResult(parameters,key));
                keys.add(key);
            }

        }



        Object[] demographyResults = results.toArray();
        String[] headerValues = keys.toArray(new String[0]);



        BeehiveCSVEncodable result = new BeehiveCSVEncodable(
                UUID.randomUUID(),
                taskIdentifier,
                taskRunUUID,
                demographyResults,
                headerValues
        );

        StepResult firstStepResult = (StepResult) (parameters.get("demography1") != null ? parameters.get("demography1") : parameters.get("demography1"));

        if (firstStepResult != null) {
            result.setStartDate(firstStepResult.getStartDate());
        }
        else {
            result.setStartDate(new Date());
        }

        if (firstStepResult != null) {
            result.setEndDate(firstStepResult.getStartDate());
        }
        else {
            result.setEndDate(new Date());
        }

        return result;
    }

    @Override
    public boolean supportsType(String type) {
        if (type.equals(io.smalldata.beehiveapp.RSRPBackendSupport.BeehiveCSVEncodable.TYPE)) return true;
        else return false;
    }
}
