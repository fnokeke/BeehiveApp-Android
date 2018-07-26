package io.smalldata.beehiveapp.notification;

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
        handleBundle(intent.getExtras());
    }

    private void handleBundle(Bundle bundle) {
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

        boolean wasDismissed = bundle.getBoolean("was_dismissed");
        if (wasDismissed) {
            Toast.makeText(mContext, method + " was dismissed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Launching " + method, Toast.LENGTH_SHORT).show();
            if (method.equals(Constants.TYPE_PUSH_NOTIFICATION)) {
                mContext.startActivity(new Intent(mContext, AppInfo.class));
                String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
                IntentLauncher.launchApp(mContext, appIdToLaunch);
            } else {
                Intent intentAppInfo = new Intent(mContext, AppInfo.class);
                intentAppInfo.putExtra(Constants.RS_TYPE, method);
                intentAppInfo.putExtra(Constants.ALARM_PROTOCOL_NOTIF_DETAILS, bundle.getString(Constants.ALARM_PROTOCOL_NOTIF_DETAILS));
                mContext.startActivity(intentAppInfo);
            }
        }

//            switch (method) {
//                case Constants.TYPE_PUSH_NOTIFICATION:
//                    String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
//                    IntentLauncher.launchApp(mContext, appIdToLaunch);
//                    break;
//                case Constants.TYPE_PAM:
//                    AlarmHelper.showInstantNotif(mContext, "NotifEvent 4 PAM", DateHelper.getFormattedTimestamp(), "", 2021);
//                    mContext.startActivity(new Intent(mContext, AppInfo.class));
//                    RSActivityManager.get().queueActivity(mContext, "RSpam", true);
//                    break;
//                case Constants.TYPE_PUSH_SURVEY:
//                    AlarmHelper.showInstantNotif(mContext, "NotifEvent 4 Survey", DateHelper.getFormattedTimestamp(), "", 2020);
//                    mContext.startActivity(new Intent(mContext, AppInfo.class));
//                    RSActivityManager.get().queueActivity(mContext, "survey", true);
//                    break;
//            }

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

