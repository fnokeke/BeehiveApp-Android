package org.researchsuite.rsrp.Core;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jameskizer on 2/2/17.
 */
public class RSRPIntermediateResult {

    private String type;
    private UUID uuid;
    private String taskIdentifier;
    private UUID taskRunUUID;
    private Date startDate;
    private Date endDate;

    private Map<String, Serializable> userInfo;
    private Map<String, Object> parameters;

    public RSRPIntermediateResult(String type, UUID uuid, String taskIdentifier, UUID taskRunUUID) {
        this.type = type;
        this.uuid = uuid;
        this.taskIdentifier = taskIdentifier;
        this.taskRunUUID = taskRunUUID;
    }

    public String getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTaskIdentifier() {
        return taskIdentifier;
    }

    public UUID getTaskRunUUID() {
        return taskRunUUID;
    }

    public Map<String, Serializable> getUserInfo() {
        return userInfo;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setUserInfo(Map<String, Serializable> userInfo) {
        this.userInfo = userInfo;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
