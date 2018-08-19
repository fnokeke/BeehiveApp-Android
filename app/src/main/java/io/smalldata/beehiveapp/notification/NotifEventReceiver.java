package io.smalldata.beehiveapp.notification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Locale;

import io.smalldata.beehiveapp.fcm.LocalStorage;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.studyManagement.RSActivityManager;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.IntentLauncher;

/**
 * Handle all notification dismissed event
 * Created by fnokeke on 6/5/17.
 */

public class NotifEventReceiver extends BroadcastReceiver {
    private final Locale locale = Locale.getDefault();
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        handleBundle(intent);
    }

    private void handleBundle(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            AlarmHelper.showInstantNotif(mContext, "Error: handleBundle is null.", DateHelper.getFormattedTimestamp(), "", 2025);
            return;
        }
        saveNotifToLocalStorage(bundle);

        String method = bundle.getString(Constants.ALARM_PROTOCOL_METHOD);
        if (method == null) {
            AlarmHelper.showInstantNotif(mContext, "Error: method == null.", DateHelper.getFormattedTimestamp(), "", 2025);
            return;
        }

        if (mContext == null)
            throw new AssertionError("NotifEventReceiver Error: mContext cannot be null");

        boolean wasDismissed = bundle.getBoolean("was_dismissed");
        if (wasDismissed && Constants.IS_DEBUG_MODE) {
            Toast.makeText(mContext, method + " was dismissed.", Toast.LENGTH_SHORT).show();
        } else {
            if (method.equals(Constants.TYPE_PUSH_NOTIFICATION)) {
                mContext.startActivity(new Intent(mContext, AppInfo.class));
                String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
                IntentLauncher.launchApp(mContext, appIdToLaunch);
                Toast.makeText(mContext, "Launching app...", Toast.LENGTH_SHORT).show();
            } else {
                Intent intentAppInfo = new Intent(mContext, AppInfo.class);
                intentAppInfo.putExtra(Constants.RS_TYPE, method);
                intentAppInfo.putExtra(Constants.ALARM_PROTOCOL_NOTIF_DETAILS, bundle.getString(Constants.ALARM_PROTOCOL_NOTIF_DETAILS));
                intentAppInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intentAppInfo);
                String suffix = Constants.RS_TYPE.equals("pam") ? "PAM" : "Survey";
                suffix = "Opening %s..." + suffix;
                Toast.makeText(mContext, suffix, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveNotifToLocalStorage(Bundle bundle) {
        long alarmTimeMillis = bundle.getLong(Constants.ALARM_MILLIS_SET);
        String phoneRingerMode = DeviceInfo.getRingerMode(mContext);

        Profile profile = new Profile(mContext);
        String username = profile.getUsername();
        String studyCode = profile.getStudyCode();

        String title = bundle.getString(Constants.ALARM_NOTIF_TITLE);
        String content = bundle.getString(Constants.ALARM_NOTIF_CONTENT);
        String appId = bundle.getString(Constants.ALARM_APP_ID);
        String method = bundle.getString(Constants.ALARM_PROTOCOL_METHOD);
        long timeOfClickOrDismiss = System.currentTimeMillis();
        boolean wasDismissed = bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED);

        String data = String.format(locale, "%s, %s, %d, %s, %s, %s, %s, %s, %s, %d\n",
                username,
                studyCode,
                alarmTimeMillis,
                phoneRingerMode,
                method,
                title,
                content,
                appId,
                wasDismissed,
                timeOfClickOrDismiss);

        LocalStorage.appendToFile(mContext, Constants.NOTIF_EVENT_LOGS_CSV, data);
    }

}

