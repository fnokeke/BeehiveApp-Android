package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

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

public class ConnectHelper {
    private final static String TAG = "ConnectHelper";
    private Context mContext;
    private TextView tvFeedback;

    public ConnectHelper(Context context, TextView feedback) {
        mContext = context;
        tvFeedback = feedback;
    }

    public void connectToBeehive(JSONObject userInfo) {
        JSONObject fromPhoneDetails = DeviceInfo.getPhoneDetails(mContext);
        Helper.copy(fromPhoneDetails, userInfo);
        Display.showBusy(mContext, "Transferring your bio...");
        CallAPI.connectStudy(mContext, userInfo, connectStudyResponseHandler);
    }

    private VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.i(TAG, "onConnectStudySuccess: " + result.toString());
            Store.wipeAll(mContext);
            Store.setBoolean(mContext, Store.SETTINGS_ENABLED, true);
            SettingsFragment.wipeAll(mContext);

            Experiment experiment = new Experiment(mContext);
            JSONObject experimentInfo = result.optJSONObject("experiment");
            experiment.saveConfigs(experimentInfo);

            JSONObject user = result.optJSONObject("user");
            experiment.saveUserInfo(user);

//            JSONObject response = result.optJSONObject("response");
//            updateFormInput(response, user);

            Display.dismissBusy();
            RefreshService.startRefreshInIntervals(mContext);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Store.setBoolean(mContext, Store.SETTINGS_ENABLED, false);
            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, false);
            Log.e("onConnectFailure: ", error.toString());

            Display.showError(tvFeedback, "Cannot submit your bio.");
            String msg = String.format(Constants.LOCALE, "Error submitting your bio. Please contact researcher. " +
                    "Error details: %s", error.toString());
            Display.showError(tvFeedback, msg);
            Display.dismissBusy();
            error.printStackTrace();
        }
    };

}
