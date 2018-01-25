package io.smalldata.beehiveapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.Locale;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.EMA;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.server.FileHelper;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Handle all notification dismissed event
 * Created by fnokeke on 6/5/17.
 */

public class NotifClickORDismissReceiver extends BroadcastReceiver {
    private static final String TAG = "NotifClickORDismiss";
    private final Locale locale = Locale.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        JSONObject params = Experiment.getUserInfo(context);
        JsonHelper.setJSONValue(params, "title", bundle.getString(Constants.ALARM_NOTIF_TITLE));
        JsonHelper.setJSONValue(params, "content", bundle.getString(Constants.ALARM_NOTIF_CONTENT));
        JsonHelper.setJSONValue(params, "app_id", bundle.getString(Constants.ALARM_APP_ID));
        JsonHelper.setJSONValue(params, "was_dismissed", bundle.getBoolean(Constants.ALARM_NOTIF_WAS_DISMISSED));
        JsonHelper.setJSONValue(params, "time_appeared", bundle.getLong(Constants.ALARM_MILLIS_SET));
        JsonHelper.setJSONValue(params, "time_clicked", System.currentTimeMillis());
        JsonHelper.setJSONValue(params, "ringer_mode", DeviceInfo.getRingerMode(context));
        CallAPI.addNotifClickedStats(context, params, getSubmitNotifClickHandler());

        String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
        boolean wasDismissed = bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED);
        if (params.optString(Constants.NOTIF_TYPE).equals(Constants.TYPE_EMA)) {
            Intent intEma = new Intent(context, EMA.class);
            intEma.putExtra("ema_title", params.optString("title"));
            intEma.putExtra("ema_content", params.optString("content"));
            context.startActivity(new Intent(intEma));
            Toast.makeText(context, "Launching EMA.", Toast.LENGTH_SHORT).show();
        } else if (!wasDismissed) {
            IntentLauncher.launchApp(context, appIdToLaunch);
            Toast.makeText(context, "Launching app: " + appIdToLaunch, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Dismissed app: " + appIdToLaunch, Toast.LENGTH_SHORT).show();
        }





        long alarmTimeMillis = bundle.getLong(Constants.ALARM_MILLIS_SET);
        String phoneRingerMode = DeviceInfo.getRingerMode(context);

        Profile profile = new Profile(context);
        String username = profile.getUsername();
        String studyCode = profile.getStudyCode();

        String title = bundle.getString(Constants.ALARM_NOTIF_TITLE);
        String content = bundle.getString(Constants.ALARM_NOTIF_CONTENT);
        String appId = bundle.getString(Constants.ALARM_APP_ID);
        long timeOfClickOrDismiss = System.currentTimeMillis();

        String data = String.format(locale, "%s, %s, %d, %s, %s, %s, %s, %s, %d;\n",
                username,
                studyCode,
                alarmTimeMillis,
                phoneRingerMode,
                title,
                content,
                appId,
                wasDismissed,
                timeOfClickOrDismiss);

        FileHelper.appendToFile(context, Constants.NOTIFICATION_FILENAME, data);

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
