package io.smalldata.beehiveapp.studyManagement;

import com.google.gson.JsonElement;

import org.researchsuite.rsrp.Core.RSRPResultTransform;

import java.util.List;

/**
 * Created by jameskizer on 1/19/17.
 */
public class CTFScheduleItem {

    public String type;
    public String identifier;
    public String title;
    public String guid;
    public JsonElement activity;
    public List<RSRPResultTransform> resultTransforms;

}
