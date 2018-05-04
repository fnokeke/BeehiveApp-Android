package org.researchsuite.rsrp.Core.RSRPFrontEndServiceProvider;

import android.support.annotation.Nullable;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.UUID;

import org.researchsuite.rsrp.Core.RSRPFrontEndServiceProvider.spi.RSRPFrontEnd;
import org.researchsuite.rsrp.Core.RSRPIntermediateResult;
import org.researchsuite.rsrp.Core.RSRPResultTransform;
import org.researchsuite.rsrp.Core.RSRPResultTransformInputMapping;


/**
 * Created by jameskizer on 2/2/17.
 */
public class RSRPFrontEndService {

    private static RSRPFrontEndService service;
    private ServiceLoader<RSRPFrontEnd> loader;

    private RSRPFrontEndService() {
        this.loader = ServiceLoader.load(RSRPFrontEnd.class);
    }

    public static synchronized RSRPFrontEndService getInstance() {
        if (service == null) {
            service = new RSRPFrontEndService();
        }
        return service;
    }

    public List<RSRPIntermediateResult> processResult(TaskResult taskResult, UUID taskRunUUID, List<RSRPResultTransform> resultTransforms) {

        List<RSRPIntermediateResult> intermediateResults = new ArrayList<>();

        for (RSRPResultTransform transform : resultTransforms) {

            Map<String, Object> parameters = new HashMap<String, Object>();
            List<RSRPResultTransformInputMapping> inputMappings = transform.getInputMapping();


            for (RSRPResultTransformInputMapping inputMapping : inputMappings) {

                if (inputMapping == null) {
                    continue;
                }

                if (inputMapping.getStepIdentifier() != null ) {
                    StepResult result = taskResult.getStepResult(inputMapping.getStepIdentifier());
                    if (result != null) {
                        parameters.put(inputMapping.getParameter(), result);
                    }
                }
                else if (inputMapping.getConstant() != null) {
                    parameters.put(inputMapping.getParameter(), inputMapping.getConstant());
                }
            }

            RSRPIntermediateResult intermediateResult = this.transform(
                    transform.getTransform(),
                    taskResult.getIdentifier(),
                    taskRunUUID,
                    parameters);

            if (intermediateResult != null) {
                intermediateResults.add(intermediateResult);
            }

        }

        return intermediateResults;

    }

    @Nullable
    private RSRPIntermediateResult transform(String type,
                                             String taskIdentifier,
                                             UUID taskRunUUID,
                                             Map<String,Object> parameters) {

        try {
            Iterator<RSRPFrontEnd> frontEnds = this.loader.iterator();

            boolean hasNext = frontEnds.hasNext();

            while (parameters != null && frontEnds.hasNext()) {
                RSRPFrontEnd frontEnd = frontEnds.next();
                if (frontEnd.supportsType(type)) {
                    RSRPIntermediateResult intermediateResult = frontEnd.transform(taskIdentifier, taskRunUUID, parameters);
                    if (intermediateResult != null) {
                        return intermediateResult;
                    }
                }
            }
        } catch (ServiceConfigurationError serviceError) {
            serviceError.printStackTrace();
            return null;
        }

        return null;

    }
}
