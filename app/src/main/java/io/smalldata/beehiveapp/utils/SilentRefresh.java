package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.main.RefreshService;


/**
 * Help connect user to Beehive Researcher Study
 * Created by fnokeke on 5/18/17.
 */

public class SilentRefresh {
    private static final String TAG = "SilentRefresh";
    private Context mContext;
    private Experiment experiment;

    public SilentRefresh(Context context) {
        mContext = context;
        experiment = new Experiment(context);
    }

    public void syncExperiment() {
        JSONObject fullUserDetails = Experiment.getFullUserDetails(mContext);
        Helper.showInstantNotif(mContext, "Beehive Remote Sync", Helper.getTimestamp(), "", 7778);
        CallAPI.connectStudy(mContext, fullUserDetails, silentConnectHandler);
    }

    private VolleyJsonCallback silentConnectHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject jsonResult) {

            JSONObject jsonExperimentInfo = jsonResult.optJSONObject("experiment");
            if (jsonExperimentInfo.length() == 0) {
                experiment.enableSettings(false);
                return;
            }
            experiment.enableSettings(true);
            experiment.saveConfigs(jsonExperimentInfo);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            experiment.enableSettings(false);
            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, false);
            Log.e(TAG, "onConnectFailure: " + error.getMessage());
            error.printStackTrace();
        }
    };

}
