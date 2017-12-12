package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.main.AutoUpdateAlarm;
import io.smalldata.beehiveapp.main.MainActivity;


/**
 * Help connect user to Beehive Researcher Study
 * Created by fnokeke on 5/18/17.
 */

public class ConnectBeehiveHelper {
    private final static String TAG = "ConnectBeehiveHelper";
    private Context mContext;
    private TextView tvFeedback;
    private Experiment experiment;

    public ConnectBeehiveHelper(Context context, TextView feedback) {
        mContext = context;
        tvFeedback = feedback;
        experiment = new Experiment(context);
    }

    public void connectToBeehive(JSONObject userInfo) {
        JSONObject fromPhoneDetails = DeviceInfo.getPhoneDetails(mContext);
        JsonHelper.copy(fromPhoneDetails, userInfo);
        Display.showBusy(mContext, "Transferring your bio...");
        CallAPI.connectStudy(mContext, userInfo, connectStudyResponseHandler);
    }

    public void fetchStudyUsingCode(String code) {
        if (!Network.isDeviceOnline(mContext)) {
            Display.showError(tvFeedback, "No network connection.");
            return;
        }

        JSONObject data = new JSONObject();
        JsonHelper.setJSONValue(data, "code", code);
        Display.showBusy(mContext, "Verifying code...");
        CallAPI.fetchStudy(mContext, data, fetchStudyResponseHandler);
    }

    private VolleyJsonCallback fetchStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject jsonResult) {
            Log.i(TAG, "fetchStudySuccess: " + jsonResult.toString());
            Display.dismissBusy();
            Display.showSuccess(tvFeedback, "Successfully connected!");
            mContext.startActivity(new Intent(mContext, MainActivity.class));

//            Display.showSuccess(tvFeedback, "Awesome! Valid Code!!");

//            JSONObject jsonExperimentInfo = jsonResult.optJSONObject("experiment");
//            Display.dismissBusy();
//            if (jsonExperimentInfo.length() == 0) {
//                experiment.enableSettings(false);
//                Display.showError(tvFeedback, "Invalid code.");
//            }
//            experiment.enableSettings(true);
//            Display.showSuccess(tvFeedback, "Successfully connected!");
//            experiment.saveConfigs(jsonExperimentInfo);
//
//            JSONObject user = jsonResult.optJSONObject("user");
//            experiment.saveUserInfo(user);
//            FirebaseMessaging.getInstance().subscribeToTopic(user.optString("code"));
//
//            JSONObject response = jsonResult.optJSONObject("response");
//            Store.setString(mContext, "formInputResponse", response.toString());
//            Store.setString(mContext, "formInputUser", user.toString());
//
//            Intent intent = new Intent("ui-form-update");
//            intent.putExtra("formInputResponse", response.toString());
//            intent.putExtra("formInputUser", user.toString());
//            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//
//            Display.dismissBusy();
//            new AutoUpdateAlarm().setAlarmForPeriodicUpdate(mContext);
//
//            showSettingsTip();
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.i(TAG, "fetchStudyFailure: " + error.toString());
            Display.dismissBusy();
            if (error.toString().contains("NoConnectionError")) {
                Display.showError(tvFeedback, "You don't have network connection.");
            } else {
                Display.showError(tvFeedback, "Uh oh...wrong code.");
            }

//            experiment.enableSettings(false);
//            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, false);
//            Log.e("onConnectFailure: ", error.toString());
//
//            Display.showError(tvFeedback, "Cannot submit your bio.");
//            String msg = String.format(Constants.LOCALE, "Error, submitting info. %s", error.toString());
//            Display.showError(tvFeedback, msg);
//            Display.dismissBusy();
//            error.printStackTrace();
        }
    };

    private VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject jsonResult) {
            Log.i(TAG, "onConnectStudySuccess: " + jsonResult.toString());

            JSONObject jsonExperimentInfo = jsonResult.optJSONObject("experiment");
            Display.dismissBusy();
            if (jsonExperimentInfo.length() == 0) {
                experiment.enableSettings(false);
                Display.showError(tvFeedback, "Invalid code.");
                return;
            }
            experiment.enableSettings(true);
            Display.showSuccess(tvFeedback, "Successfully connected!");
            experiment.saveConfigs(jsonExperimentInfo);

            JSONObject user = jsonResult.optJSONObject("user");
            experiment.saveUserInfo(user);
            FirebaseMessaging.getInstance().subscribeToTopic(user.optString("code"));

            JSONObject response = jsonResult.optJSONObject("response");
            Store.setString(mContext, "formInputResponse", response.toString());
            Store.setString(mContext, "formInputUser", user.toString());

            Intent intent = new Intent("ui-form-update");
            intent.putExtra("formInputResponse", response.toString());
            intent.putExtra("formInputUser", user.toString());
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

            Display.dismissBusy();
            new AutoUpdateAlarm().setAlarmForPeriodicUpdate(mContext);

            showSettingsTip();
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            experiment.enableSettings(false);
            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, false);
            Log.e("onConnectFailure: ", error.toString());

            Display.showError(tvFeedback, "Cannot submit your bio.");
            String msg = String.format(Constants.LOCALE, "Error, submitting info. %s", error.toString());
            Display.showError(tvFeedback, msg);
            Display.dismissBusy();
            error.printStackTrace();
        }
    };

    private void showSettingsTip() {
        String title = "Select Preference";
        String content = "Go to Beehive App >> Settings";
        AlarmHelper.showInstantNotif(mContext, title, content, "", 7777);
    }


}
