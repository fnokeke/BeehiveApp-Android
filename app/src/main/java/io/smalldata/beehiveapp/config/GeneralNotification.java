package io.smalldata.beehiveapp.config;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import io.smalldata.beehiveapp.utils.Store;

/**
 * General Notifications configured on Beehive Platform will be handled here.
 * Created by fnokeke on 2/21/17.
 */

public class GeneralNotification extends BaseConfig {
    private Context mContext;
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String APP_ID = "app_id";

     public GeneralNotification(Context context) {
        mContext = context;
    }

    public void saveSettings(JSONArray notifConfig) {
        if (notifConfig == null || notifConfig.length() == 0) return;
        JSONObject lastItem = notifConfig.optJSONObject(notifConfig.length() - 1);
        Store.setString(mContext, TITLE, lastItem.optString(TITLE));
        Store.setString(mContext, CONTENT, lastItem.optString(CONTENT));
        Store.setString(mContext, APP_ID, lastItem.optString(APP_ID));
    }

    public String getTitle() {
        return Store.getString(mContext, TITLE);
    }

    public String getContent() {
        return Store.getString(mContext, CONTENT);
    }

    public String getAppId() {
        return Store.getString(mContext, APP_ID);
    }
}
