package io.smalldata.beehiveapp.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

/**
 * Created by jameskizer on 4/20/17.
 */

public class RSFirstRun {
    private static final String PREFERENCES_FILE  = "firstRun";
    private static final String KEY_FIRST_RUN = "firstRun";

    protected final SharedPreferences sharedPreferences;

    private Gson gson;

    public RSFirstRun(Context applicationContext) {

        this.gson = new Gson();
        sharedPreferences = applicationContext
                .getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

    }

    protected <T> T getValue(String key, Class<? extends T> klass) {
        String json = sharedPreferences.getString(key, null);
        return this.gson.fromJson(json, klass);
    }

    protected <T> void setValue(String key, T value, Class<? super T> klass) {
        String json = this.gson.toJson(value, klass);
        sharedPreferences.edit().putString(key, json).apply();
    }

    @Nullable
    public Boolean getFirstRun() {
        return this.getValue(KEY_FIRST_RUN, Boolean.class);
    }

    public void setFirstRun(Boolean firstRun) {
        this.setValue(KEY_FIRST_RUN, firstRun, Boolean.class);
    }

}