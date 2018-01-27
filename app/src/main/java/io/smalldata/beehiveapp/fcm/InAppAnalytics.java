package io.smalldata.beehiveapp.fcm;

import android.content.Context;

import java.util.Locale;

import io.smalldata.beehiveapp.onboarding.Constants;

/**
 * Created by fnokeke on 1/25/18.
 * Monitor all In-App analytics
 */

public class InAppAnalytics {

   public static void add(Context context, String data)  {
      String row = String.format(Locale.getDefault(), "%d, %s;\n", System.currentTimeMillis(), data);
      LocalStorage.appendToFile(context, Constants.ANALYTICS_LOG_CSV, row);
   }

    public static void add(Context context, String key, String value)  {
        String row = String.format(Locale.getDefault(), "%d, %s-%s;\n", System.currentTimeMillis(), key, value);
        LocalStorage.appendToFile(context, Constants.ANALYTICS_LOG_CSV, row);
    }
}
