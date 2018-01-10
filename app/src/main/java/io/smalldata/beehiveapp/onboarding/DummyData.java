package io.smalldata.beehiveapp.onboarding;

import android.content.Context;

import org.json.JSONObject;

import io.smalldata.beehiveapp.utils.JsonHelper;

/**
 * Created by fnokeke on 12/31/17.
 * Use fake data for testing offline
 */

class DummyData {

    static void applyFakeConfig(Context context) {
        JSONObject config = getFakeData();
        new Profile(context).saveStudyConfig(config);
    }

    private static JSONObject getFakeData() {
        return JsonHelper.strToJsonObject(getFakeString());
    }

    private static String getFakeString() {
        return "{\n" +
                "    \"login_type\": [\n" +
                "        \"google_login\"\n" +
                "    ],\n" +
                "    \"experiment\": {\n" +
                "        \"code\": \"mobile\",\n" +
                "        \"description\": \"Manage your daily stress level through meditation so you live an improved life.\",\n" +
                "        \"end_date\": \"2018-12-31\",\n" +
                "        \"title\": \"Stress management\",\n" +
                "        \"label\": \"Billie-study\",\n" +
                "        \"owner\": \"fnokeke@gmail.com\",\n" +
                "        \"created_date\": \"2017-12-17 15:06:19.983888\",\n" +
                "        \"app_usage\": false,\n" +
                "        \"start_date\": \"2016-12-17\",\n" +
                "        \"screen_events\": false\n" +
                "    },\n" +
                "    \"google_login_type\": \"google_no_ohmage\",\n" +
                "    \"protocols\": [\n" +
                "        {\n" +
                "            \"end_date\": \"2018-12-25\",\n" +
                "            \"notif_content\": \"Spend two minutes to meditate\",\n" +
                "            \"notif_appid\": \"com.google.android.calendar\",\n" +
                "            \"notif_type\": \"user_window\",\n" +
                "            \"probable_half_notify\": \"False\",\n" +
                "            \"created_at\": \"2017-12-17 15:06:19.989863\",\n" +
                "            \"exp_code\": \"mobile\",\n" +
                "            \"notif_time\": \"1_hour\",\n" +
                "            \"frequency\": \"daily\",\n" +
                "            \"start_date\": \"2017-12-20\",\n" +
                "            \"notif_title\": \"Meditation Time\",\n" +
                "            \"label\": \"headspace-prompt\",\n" +
                "            \"id\": 14\n" +
                "        },\n" +
                "        {\n" +
                "            \"end_date\": \"2018-12-25\",\n" +
                "            \"notif_content\": \"How was your day?\",\n" +
                "            \"notif_appid\": \"com.google.android.keep\",\n" +
                "            \"notif_type\": \"sleep_wake\",\n" +
                "            \"probable_half_notify\": \"False\",\n" +
                "            \"created_at\": \"2017-12-17 15:06:19.989863\",\n" +
                "            \"exp_code\": \"mobile\",\n" +
                "            \"notif_time\": \"1_hour;before_sleep\",\n" +
                "            \"frequency\": \"daily\",\n" +
                "            \"start_date\": \"2017-12-20\",\n" +
                "            \"notif_title\": \"Bedtime Check-in\",\n" +
                "            \"label\": \"pam-ema\",\n" +
                "            \"id\": 14\n" +
                "        },\n" +
                "        {\n" +
                "            \"end_date\": \"2017-12-25\",\n" +
                "            \"notif_content\": \"use-headspace-now\",\n" +
                "            \"notif_appid\": \"com.headspace.app\",\n" +
                "            \"notif_type\": \"user_window\",\n" +
                "            \"probable_half_notify\": \"True\",\n" +
                "            \"created_at\": \"2017-12-17 15:06:19.998121\",\n" +
                "            \"exp_code\": \"mobile\",\n" +
                "            \"notif_time\": \"3_hour\",\n" +
                "            \"frequency\": \"daily\",\n" +
                "            \"start_date\": \"2017-12-18\",\n" +
                "            \"notif_title\": \"time4headspace\",\n" +
                "            \"label\": \"headspace-half-time\",\n" +
                "            \"id\": 15\n" +
                "        },\n" +
                "        {\n" +
                "            \"end_date\": \"2017-12-25\",\n" +
                "            \"notif_content\": \"None\",\n" +
                "            \"notif_appid\": \"None\",\n" +
                "            \"notif_type\": \"none\",\n" +
                "            \"probable_half_notify\": \"False\",\n" +
                "            \"created_at\": \"2017-12-17 15:34:31.402768\",\n" +
                "            \"exp_code\": \"mobile\",\n" +
                "            \"notif_time\": \"None\",\n" +
                "            \"frequency\": \"daily\",\n" +
                "            \"start_date\": \"2017-12-18\",\n" +
                "            \"notif_title\": \"None\",\n" +
                "            \"label\": \"baseline-no-intv\",\n" +
                "            \"id\": 19\n" +
                "        }\n" +
                "    ]\n" +
                "} ";
    }

}

