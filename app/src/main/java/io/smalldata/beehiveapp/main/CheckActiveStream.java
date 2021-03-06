package io.smalldata.beehiveapp.main;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

import io.smalldata.beehiveapp.notification.NewAlarmHelper;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.JsonHelper;

/**
 * Check which data streams are activate and perform respective tasks
 * Created by fnokeke on 6/4/18.
 */

class CheckActiveStream {

    private Context mContext;
    private Profile mProfile;
    final private String MONITORINIG_APP = "io.smalldata.applogger";

    CheckActiveStream(Context context) {
        this.mContext = context;
        mProfile = new Profile(mContext);
    }

    void confirmMonitorAppSetUp() {
        JSONObject experiment = mProfile.getStudyConfig().optJSONObject("experiment");
        if (experiment.optBoolean("screen_events") || experiment.optBoolean("app_usage")) {
            if (!Helper.isPackageInstalled(mContext, MONITORINIG_APP)) {
                final String appLink = "https://slm.smalldata.io/static/downloads/applogger.apk";
                NewAlarmHelper.showInstantNotif(mContext, "This study requires AppLogger App",
                        "Tap here to install.",
                        appLink, 2011);
                Toast.makeText(mContext, "AppLogger needed...", Toast.LENGTH_SHORT).show();
            } else {
                if (!mProfile.hasAlreadyPrompted()) {
                    mProfile.setPromptedForMonitoringApp();
                    connectToAppLogger();
                }
            }
        }
    }

    public void connectToAppLogger() {
        JSONObject dataToTransfer = new JSONObject();
        JsonHelper.setJSONValue(dataToTransfer, "username", mProfile.getUsername());
        JsonHelper.setJSONValue(dataToTransfer, "code", mProfile.getStudyCode());
        IntentLauncher.launchApp(mContext, MONITORINIG_APP, dataToTransfer);
        Toast.makeText(mContext, "Linking AppLogger...", Toast.LENGTH_SHORT).show();
    }


    void confirmMeditationAppSetUp() {
        final String HEADSPACE_APP = "com.getsomeheadspace.android";
        if (!Helper.isPackageInstalled(mContext, HEADSPACE_APP)) {
            final String APPLINK = "https://play.google.com/store/apps/details?id=com.getsomeheadspace.android&hl=en_US";
            NewAlarmHelper.showInstantNotif(mContext, "This study requires Headspace App",
                    "Tap here to install.",
                    APPLINK, 2012);
            Toast.makeText(mContext, "Please install Headspace for meditation.", Toast.LENGTH_SHORT).show();
        }
    }

}
