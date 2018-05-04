package org.researchsuite.rsrp.Core;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jameskizer on 2/2/17.
 */
public class RSRPResultTransform implements Serializable {

    private String transform;
    private List<RSRPResultTransformInputMapping> inputMapping;

    public RSRPResultTransform() {

    }

    public String getTransform() {
        return transform;
    }

    public List<RSRPResultTransformInputMapping> getInputMapping() {
        return inputMapping;
    }
}
