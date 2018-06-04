package io.smalldata.beehiveapp.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONObject;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.notification.NewAlarmHelper;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.IntentLauncher;

/**
 * Check which datastreams are activated
 * Created by fnokeke on 6/4/18.
 */

class CheckActiveStream {
    private final String MONITORINIG_APP = "io.smalldata.goodvibe";

    private Context mContext;
    private Profile mProfile;

    CheckActiveStream(Context context) {
        this.mContext = context;
        mProfile = new Profile(mContext);
    }

    void prompt() {
        JSONObject experiment = mProfile.getStudyConfig().optJSONObject("experiment");
        if (experiment.optBoolean("screen_events") || experiment.optBoolean("app_usage")) {
            if (!Helper.isPackageInstalled(mContext, MONITORINIG_APP)) {
                Toast.makeText(mContext, "Goodvibe app needed...", Toast.LENGTH_SHORT).show();
                String appLink = "https://slm.smalldata.io/static/downloads/goodvibe-2.5.apk";
                NewAlarmHelper.showInstantNotif(
                        mContext,
                        "This study requires Goodvibe app",
                        "Tap here to install.",
                        appLink,
                        2011);
            }
        }
    }


}
