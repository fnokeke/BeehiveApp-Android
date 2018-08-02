package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by fnokeke on 2/14/17.
 */

public class IntentLauncher {

    public static Intent getLaunchIntent(Context context, String appPackageName) {
        return getLaunchIntent(context, appPackageName, null);
    }

    public static Intent getLaunchIntent(Context context, String appPackageName, JSONObject dataToTransfer) {
        if (appPackageName.contains("/")) {
            return getURLIntent(appPackageName);
        }
        PackageManager pm = context.getPackageManager();
        Intent appStartIntent = pm.getLaunchIntentForPackage(appPackageName);
        if (appStartIntent == null) {
            appStartIntent = pm.getLaunchIntentForPackage(context.getPackageName());
        }

        if (dataToTransfer != null && appStartIntent != null) {
            String key, value;
            Iterator<?> keys = dataToTransfer.keys();
            while (keys.hasNext()) {
                key = (String) keys.next();
                value = dataToTransfer.optString(key);
                appStartIntent.putExtra(key, value);
            }
        }

        return appStartIntent;
    }

    private static Intent getURLIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    public static void launchApp(Context context, String appPackageName) {
        launchApp(context, appPackageName, null);
    }

    public static void launchApp(Context context, String appPackageName, JSONObject dataToTransfer) {
        if (appPackageName.contains("/")) {
            Helper.openURL(context, appPackageName);
        } else {
            context.startActivity(getLaunchIntent(context, appPackageName, dataToTransfer));
        }
    }

}
