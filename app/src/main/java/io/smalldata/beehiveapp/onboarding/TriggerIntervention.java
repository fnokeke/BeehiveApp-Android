package io.smalldata.beehiveapp.onboarding;

import android.content.Context;

import io.smalldata.beehiveapp.utils.DateHelper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Created by fnokeke on 1/2/18.
 * Handle intervention
 */

class TriggerIntervention {
    private Context mContext;
    private Profile mProfile;

    TriggerIntervention(Context context) {
        mContext = context;
        mProfile = new Profile(mContext);
    }

    void startIntvForToday() {
        if (isNewDay() && todayIntvExists()) {
            mProfile.applyIntvForToday();
        }
    }

    private boolean isNewDay() {
        String lastSavedDate = Store.getString(mContext, Constants.KEY_LAST_SAVED_DATE);
        String todayDate = DateHelper.getTodayDateStr();
        return !lastSavedDate.equals(todayDate);
    }

    private boolean todayIntvExists() {
        return mProfile.hasIntvForToday();
    }

}
