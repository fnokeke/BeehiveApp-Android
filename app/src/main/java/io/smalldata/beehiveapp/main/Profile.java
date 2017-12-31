package io.smalldata.beehiveapp.main;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.smalldata.beehiveapp.api.SingletonRequest;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Created by fnokeke on 12/17/17.
 */

public class Profile {

    private Context mContext;
    private static final String FIRSTNAME = "firstname";
    private static final String STUDY_CONFIG_KEY = "studyConfig";
    private static final String USERNAME = "username";

    public Profile(Context context) {
        mContext = context;
    }

    public void saveFirstname(String firstname) {
        Store.setString(mContext, FIRSTNAME, firstname);
    }

    public String getFirstname() {
        return Store.getString(mContext, FIRSTNAME);
    }

    public void saveStudyConfig(JSONObject jsonResult) {
        Store.setString(mContext, STUDY_CONFIG_KEY, jsonResult.toString());
    }

    private JSONObject getStudyConfig() {
        return JsonHelper.strToJsonObject(Store.getString(mContext, STUDY_CONFIG_KEY));
    }

    public void saveUsername(String username) {
        Store.setString(mContext, USERNAME, username);
    }

    public String getUsername() {
        return Store.getString(mContext, USERNAME);
    }

}
