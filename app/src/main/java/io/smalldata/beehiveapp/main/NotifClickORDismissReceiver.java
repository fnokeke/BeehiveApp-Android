package io.smalldata.beehiveapp.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.JsonHelper;

/**
 * Handle all notification dismissed event
 * Created by fnokeke on 6/5/17.
 */

public class NotifClickORDismissReceiver extends BroadcastReceiver {
    private static final String TAG = "NotifClickORDismiss";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        JSONObject params = Experiment.getUserInfo(context);
        JsonHelper.setJSONValue(params, "title", bundle.getString(AlarmHelper.ALARM_NOTIF_TITLE));
        JsonHelper.setJSONValue(params, "content", bundle.getString(AlarmHelper.ALARM_NOTIF_CONTENT));
        JsonHelper.setJSONValue(params, "app_id", bundle.getString(AlarmHelper.ALARM_APP_ID));
        JsonHelper.setJSONValue(params, "was_dismissed", bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED));
        JsonHelper.setJSONValue(params, "time_appeared", bundle.getLong(AlarmHelper.ALARM_MILLIS_SET));
        JsonHelper.setJSONValue(params, "time_clicked", System.currentTimeMillis());
        JsonHelper.setJSONValue(params, "ringer_mode", DeviceInfo.getRingerMode(context));
        CallAPI.addNotifClickedStats(context, params, getSubmitNotifClickHandler());

        String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
        boolean wasDismissed = bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED);
        if (!wasDismissed) {
            IntentLauncher.launchApp(context, appIdToLaunch);
        }
    }

    public VolleyJsonCallback getSubmitNotifClickHandler() {
        return new VolleyJsonCallback() {
            @Override
            public void onConnectSuccess(JSONObject result) {
                Log.i(TAG, "onConnectSuccess: submit_notif_stats success " + result.toString());
            }

            @Override
            public void onConnectFailure(VolleyError error) {
                Log.e(TAG, "submit_notif_stats error " + error.toString());
                error.printStackTrace();
            }
        };
    }

}
