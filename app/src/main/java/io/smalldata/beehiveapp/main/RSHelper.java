package io.smalldata.beehiveapp.main;

import android.content.Context;

import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.studyManagement.RSActivity;
import io.smalldata.beehiveapp.studyManagement.RSActivityManager;

/**
 * render ResearchStack activities
 * Created by fnokeke on 5/30/18.
 */

public class RSHelper extends RSActivity {

    public static void showTask(Context context, String method) {
        switch (method) {
            case Constants.TYPE_PAM:
                RSActivityManager.get().queueActivity(context, "RSpam", true);
                break;
            case Constants.TYPE_PUSH_SURVEY:
                RSActivityManager.get().queueActivity(context, "survey", true);
                break;
        }
    }

}
