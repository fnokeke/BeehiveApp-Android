package io.smalldata.beehiveapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Locale;

import io.smalldata.beehiveapp.fcm.LocalStorage;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.AlarmHelper;
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
        if (bundle == null) return;
        saveNotifToLocalStorage(bundle);

        String method = bundle.getString(Constants.ALARM_PROTOCOL_METHOD);
        boolean wasDismissed = bundle.getBoolean("was_dismissed");
        if (method != null && !wasDismissed) {
            if (method.equals(Constants.TYPE_PUSH_NOTIFICATION)) {
                String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
                IntentLauncher.launchApp(mContext, appIdToLaunch);
            } else {
                Intent rsIntent = new Intent(mContext, AppInfo.class);
                rsIntent.putExtra(Constants.RS_TYPE, method);
                mContext.startActivity(rsIntent);
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

        LocalStorage.appendToFile(mContext, Constants.NOTIF_LOGS_CSV, data);
    }

}

