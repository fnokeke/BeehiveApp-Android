package io.smalldata.beehiveapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Locale;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.fcm.LocalStorage;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.EMA;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.studyManagement.RSActivityManager;
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
        String method = bundle.getString(Constants.ALARM_NOTIF_METHOD);
        if (method != null) {
            switch (method) {
                case Constants.TYPE_PAM:
                    RSActivityManager.get().queueActivity(mContext, "pam", true);
                    break;

                case Constants.TYPE_PUSH_SURVEY:
                    RSActivityManager.get().queueActivity(mContext, "demography", true);
                    break;

                case Constants.TYPE_PUSH_NOTIFICATION:
                    launchNonDismissedApp(bundle);
                    break;

                default:
                    throw new UnsupportedOperationException("Protocol type does not exist");
            }
            saveNotifToLocalStorage(bundle);
        }
    }

    private void launchNonDismissedApp(Bundle bundle) {
        String appIdToLaunch = bundle.getString(AlarmHelper.ALARM_APP_ID);
        boolean wasDismissed = bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED);
        if (wasDismissed) {
            Toast.makeText(mContext, "Dismissed app: " + appIdToLaunch, Toast.LENGTH_SHORT).show();
        } else {
            IntentLauncher.launchApp(mContext, appIdToLaunch);
            Toast.makeText(mContext, "Launching app: " + appIdToLaunch, Toast.LENGTH_SHORT).show();
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
        String method = bundle.getString(Constants.ALARM_NOTIF_METHOD);
        long timeOfClickOrDismiss = System.currentTimeMillis();
        boolean wasDismissed = bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED);

        String data = String.format(locale, "%s, %s, %d, %s, %s, %s, %s, %s, %s, %d;\n",
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

