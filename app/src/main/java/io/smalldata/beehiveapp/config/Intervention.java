package io.smalldata.beehiveapp.config;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Helper.getTodaysDateStr;
import static io.smalldata.beehiveapp.utils.Store.INTV_END;
import static io.smalldata.beehiveapp.utils.Store.INTV_EVERY;
import static io.smalldata.beehiveapp.utils.Store.INTV_NOTIF;
import static io.smalldata.beehiveapp.utils.Store.INTV_REPEAT;
import static io.smalldata.beehiveapp.utils.Store.INTV_START;
import static io.smalldata.beehiveapp.utils.Store.INTV_TREATMENT_IMAGE;
import static io.smalldata.beehiveapp.utils.Store.INTV_TREATMENT_TEXT;
import static io.smalldata.beehiveapp.utils.Store.INTV_TYPE;
import static io.smalldata.beehiveapp.utils.Store.INTV_USER_WINDOW_MINS;
import static io.smalldata.beehiveapp.utils.Store.INTV_WHEN;

/**
 * All daily interventions assigned through Beehive Platform will be handled here.
 * Created by fnokeke on 2/15/17.
 */

public class Intervention extends BaseConfig {
    private static final String LAST_CHECKED_DATE = "lastCheckedDate";
    private  Context mContext;

    public Intervention(Context context) {
        mContext = context;
    }

    @Override
    public void saveSettings(JSONArray interventions) {
        if (interventions == null) {
            interventions = new JSONArray();
        }
        storeAllInterventions(interventions);
    }

    private void storeAllInterventions(JSONArray interventions) {
        Store.setString(mContext, "interventions", interventions.toString());
        prepareTodayIntervention(mContext);
    }

    private void prepareTodayIntervention(Context context) {
//        if (todayAlreadyChecked(context)) return;

        JSONArray interventions = getAllInterventions(context);
        JSONObject intv = new JSONObject();
        for (Integer i = 0; i < interventions.length(); i++) {

            try {
                intv = new JSONObject(interventions.optString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isForToday(intv)) {
                storeTodayAsCheckedDate(context);

                Store.setString(context, INTV_START, intv.optString("start"));
                Store.setString(context, INTV_END, intv.optString("end"));
                Store.setString(context, INTV_EVERY, intv.optString("every"));
                Store.setString(context, INTV_REPEAT, intv.optString("repeat"));
                Store.setString(context, INTV_TREATMENT_TEXT, intv.optString("treatment_text"));
                Store.setString(context, INTV_TREATMENT_IMAGE, intv.optString("treatment_image"));
                Store.setString(context, INTV_WHEN, intv.optString("when"));
                Store.setString(context, INTV_TYPE, intv.optString("intv_type"));
                Store.setString(context, INTV_NOTIF, intv.optString("notif"));

                if (Store.getBoolean(context, Store.NOTIF_WINDOW_FEATURE)) {
                    String user_window_mins = intv.optString("user_window_mins");
                    user_window_mins = user_window_mins.equals("") ? "120" : user_window_mins;
                    Store.setInt(context, INTV_USER_WINDOW_MINS, Integer.parseInt(user_window_mins));
                }

                new DailyReminder(context).triggerSetReminder();

                String msg = String.format("Today treatment. text: %s / image: %s",
                        Store.getString(context, Store.INTV_TREATMENT_TEXT),
                        Store.getString(context, Store.INTV_TREATMENT_IMAGE));
                Log.e("BeehiveTreatment", msg);
                break;
            }
        }

        if (Store.getString(context, "iTreatmentImage").equals("")) {
            Log.e("BeehiveTreatment", "No treatment image for today");
        }

        if (Store.getString(context, "iTreatmentText").equals("")) {
            Log.e("BeehiveTreatment", "No treatment text for today");
        }
    }

    private boolean todayAlreadyChecked(Context context) {
        String lastCheckedDate = Store.getString(context, LAST_CHECKED_DATE);
        String today = getTodaysDateStr();
        return today.equals(lastCheckedDate);
    }

    private void storeTodayAsCheckedDate(Context context) {
        Store.setString(context, LAST_CHECKED_DATE, Helper.getTodaysDateStr());
    }

    private static JSONArray getAllInterventions(Context context) {
        String interventionsStr = Store.getString(context, "interventions");
        JSONArray interventions = new JSONArray();
        try {
            interventions = new JSONArray(interventionsStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return interventions;
    }

    private static Boolean isForToday(JSONObject jo) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        Date startDate = Helper.getDatetimeGMT(jo.optString("start"), dateFormat);
        Date endDate = Helper.getDatetimeGMT(jo.optString("end"), dateFormat);
        long rightNow = java.util.Calendar.getInstance().getTimeInMillis();

        return rightNow >= startDate.getTime() && rightNow <= endDate.getTime();
    }

    public static JSONObject getNotifDetails(Context context) {
        JSONObject info = new JSONObject();
        try {
            info = new JSONObject(Store.getString(context, Store.INTV_NOTIF));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    public JSONObject getTodayIntervention(Context context) {
        JSONObject todayIntervention = new JSONObject();
        Helper.setJSONValue(todayIntervention, "start", Store.getString(context, Store.INTV_START));
        Helper.setJSONValue(todayIntervention, "end", Store.getString(context, Store.INTV_END));
        Helper.setJSONValue(todayIntervention, "every", Store.getString(context, Store.INTV_EVERY));
        Helper.setJSONValue(todayIntervention, "when", Store.getString(context, Store.INTV_WHEN));
        Helper.setJSONValue(todayIntervention, "repeat", Store.getString(context, Store.INTV_REPEAT));
        Helper.setJSONValue(todayIntervention, "treatment_text", Store.getString(context, Store.INTV_TREATMENT_TEXT));
        Helper.setJSONValue(todayIntervention, "treatment_image", Store.getString(context, Store.INTV_TREATMENT_IMAGE));
        return todayIntervention;
    }

//    public static File getTodayImagePath(Context context) {
//        ImageSaver imageSaver = new ImageSaver(context);
//        return imageSaver.getImagePath("beehiveImages", "beehiveTodayImage.png");
//    }

//    private void downloadImage(String image_url) {
//        if (image_url.equals("")) return;
//
//        Picasso.with(mContext)
//                .load(image_url)
//                .into(new Target() {
//                          @Override
//                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                              ImageSaver imageSaver = new ImageSaver(mContext);
//                              imageSaver.
//                                      setFileName("beehiveTodayImage.png").
//                                      setDirectoryName("beehiveImages").
//                                      save(bitmap);
//
//                              Log.i("BeehiveBitmapSave", "imageSaved");
//                          }
//
//                          @Override
//                          public void onBitmapFailed(Drawable errorDrawable) {
//                              Log.d("PicassoBitmap", "bitmap failed");
//                          }
//
//                          @Override
//                          public void onPrepareLoad(Drawable placeHolderDrawable) {}
//                      }
//                );
//    }

}

// TODO: 2/21/17 use NYC timezone