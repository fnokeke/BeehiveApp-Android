package io.smalldata.beehiveapp.studyManagement;


import android.text.TextUtils;

import org.researchstack.backbone.ResourcePathManager;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSResourcePathManager extends ResourcePathManager {

    public static final String BASE_PATH_JSON = "json";

    @Override
    public String generatePath(int type, String name) {
        String dir;
        switch (type) {
            default:
                dir = null;

                break;
            case Resource.TYPE_JSON:
                dir = BASE_PATH_JSON;
                break;
        }

        StringBuilder path = new StringBuilder();
        if (!TextUtils.isEmpty(dir)) {
            path.append(dir).append("/");
        }

        return path.append(name).append(".").append(getFileExtension(type)).toString();
    }
}

