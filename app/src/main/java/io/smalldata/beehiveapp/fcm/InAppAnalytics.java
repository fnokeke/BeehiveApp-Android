package io.smalldata.beehiveapp.fcm;

import android.content.Context;

import io.smalldata.beehiveapp.onboarding.Constants;

/**
 * Created by fnokeke on 1/25/18.
 * Monitor all In-App analytics
 */

public class InAppAnalytics {

   public static void add(Context context, String data)  {
      String row = String.format("%s;\n", data);
      LocalStorage.appendToFile(context, Constants.NOTIF_LOGS_CSV, row);
   }

    public static void add(Context context, String key, String value)  {
        String row = String.format("%s-%s;\n", key, value);
        LocalStorage.appendToFile(context, Constants.NOTIF_LOGS_CSV, row);
    }
}
