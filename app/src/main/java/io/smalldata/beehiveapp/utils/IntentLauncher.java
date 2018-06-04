package io.smalldata.beehiveapp.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by fnokeke on 2/14/17.
 */

public class IntentLauncher {

    public static Intent getLaunchIntent(Context context, String appPackageName) {
        if (appPackageName.contains("/")) {
            return getURLIntent(appPackageName);
        }
        PackageManager pm = context.getPackageManager();
        Intent appStartIntent = pm.getLaunchIntentForPackage(appPackageName);
        if (appStartIntent == null) {
            appStartIntent = pm.getLaunchIntentForPackage(context.getPackageName());
        }
        return appStartIntent;
    }

    private static Intent getURLIntent(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    public static void launchApp(Context context, String appPackageName) {
        if (appPackageName.contains("/")) {
            Helper.openURL(context, appPackageName);
        } else {
            context.startActivity(getLaunchIntent(context, appPackageName));
        }
    }
}
