package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.smalldata.beehiveapp.BuildConfig;

import static io.smalldata.beehiveapp.utils.Helper.setJSONValue;

/**
 * DeviceInfo provides detailed information about each device where app is installed
 * Created by fnokeke on 2/13/17.
 */

public class DeviceInfo {

    public static JSONObject getPhoneDetails(Context context) {

        long lastInstalledTimeMS = getLastInstalledTimeMilliSeconds(context);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Constants.LOCALE);
        String prettyLastInstalledTime = formatter.format(new Date(lastInstalledTimeMS));
        String phoneModel = String.format(Constants.LOCALE, "%s %s", Build.MANUFACTURER, Build.MODEL);
        phoneModel = phoneModel.length() > 30 ? phoneModel.substring(0, 30) : phoneModel;

        JSONObject phoneDetails = new JSONObject();
        setJSONValue(phoneDetails, "last_installed_ms", String.valueOf(lastInstalledTimeMS));
        setJSONValue(phoneDetails, "pretty_last_installed", prettyLastInstalledTime);
        setJSONValue(phoneDetails, "app_version_name", BuildConfig.VERSION_CODE);
        setJSONValue(phoneDetails, "app_version_code", BuildConfig.VERSION_NAME);
        setJSONValue(phoneDetails, "phone_model", phoneModel);
        setJSONValue(phoneDetails, "android_version", Build.VERSION.RELEASE);
        setJSONValue(phoneDetails, "device_country", getDeviceCountry(context));
        setJSONValue(phoneDetails, "device_id", getDeviceID(context));

        return phoneDetails;
    }


    private static long getLastInstalledTimeMilliSeconds(Context context) {
        PackageManager pm = context.getPackageManager();
        long installedMilliSeconds = -1;
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            String appFile = appInfo.sourceDir;
            installedMilliSeconds = new File(appFile).lastModified();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return installedMilliSeconds;
    }

    private static String getDeviceCountry(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso().toUpperCase();
    }

    private static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
