package org.researchsuite.rsrp.Core;

import java.util.Map;
import java.util.UUID;

import org.researchsuite.rsrp.Core.RSRPFrontEndServiceProvider.spi.RSRPFrontEnd;

/**
 * Created by jameskizer on 2/4/17.
 */
public class RSRPFakeFrontEnd implements RSRPFrontEnd {


    @Override
    public RSRPIntermediateResult transform(
            String taskIdentifier,
            UUID taskRunUUID,
            Map<String, Object> parameters) {
        return null;
    }

    @Override
    public boolean supportsType(String type) {
        return false;
    }
}
