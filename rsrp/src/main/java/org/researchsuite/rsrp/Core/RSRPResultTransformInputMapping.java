package org.researchsuite.rsrp.Core;

import java.io.Serializable;

/**
 * Created by jameskizer on 2/2/17.
 */
public class RSRPResultTransformInputMapping implements Serializable {

    private Object constant;
    private String stepIdentifier;
    private String parameter;

    public RSRPResultTransformInputMapping() {

    }

    public String getParameter() {
        return parameter;
    }

    public Object getConstant() {
        return constant;
    }

    public String getStepIdentifier() {
        return stepIdentifier;
    }
}
