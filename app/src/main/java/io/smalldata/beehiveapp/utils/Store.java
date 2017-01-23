package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fnokeke on 1/23/17.
 */

public class Store {

    private static Store mInstance;
    private final String PREF_NAME = "store";
    private Context context;
    private SharedPreferences.Editor editor;

    public Store(Context cxt) {
        this.context = cxt;
        this.editor = getPrefs(context).edit();
    }

    public static synchronized Store getInstance(Context cxt) {
        if (mInstance == null) {
            mInstance = new Store(cxt);
        }
        return mInstance;
    }

    private SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setString(String key, String input) {
        editor.putString(key, input).apply();
    }

    public String getString(String key) {
        return getPrefs(context).getString(key, "");
    }

    public void setInt(String key, Integer input) {
        editor.putInt(key, input).apply();
    }

    public Integer getInt(String key) {
        return getPrefs(context).getInt(key, 0);
    }

    public void setBoolean(String key, Boolean input) {
        editor.putBoolean(key, input).apply();
    }

    public Boolean getBoolean(String key) {
        return getPrefs(context).getBoolean(key, false);
    }

}
