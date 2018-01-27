package io.smalldata.beehiveapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Locale;

import io.smalldata.beehiveapp.fcm.LocalStorage;
import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.onboarding.EMA;
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
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        if (isEMAType(bundle)) {
            launchEMA(bundle);
        } else {
            launchNonDismissedApp(bundle);
            saveNotifToLocalStorage(bundle);
        }
    }

    private boolean isEMAType(Bundle bundle) {
        String notifType = bundle.getString(Constants.NOTIF_TYPE);
        return notifType != null && notifType.equals(Constants.TYPE_EMA);
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
        long timeOfClickOrDismiss = System.currentTimeMillis();
        boolean wasDismissed = bundle.getBoolean(AlarmHelper.ALARM_NOTIF_WAS_DISMISSED);

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

        LocalStorage.appendToFile(mContext, Constants.NOTIF_LOGS_CSV, data);
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

    private void launchEMA(Bundle bundle) {
        Intent intEma = new Intent(mContext, EMA.class);
        intEma.putExtra("ema_title", bundle.getString("title"));
        intEma.putExtra("ema_content", bundle.getString("content"));
        mContext.startActivity(new Intent(intEma));
        Toast.makeText(mContext, "Launching EMA.", Toast.LENGTH_SHORT).show();
    }

}

