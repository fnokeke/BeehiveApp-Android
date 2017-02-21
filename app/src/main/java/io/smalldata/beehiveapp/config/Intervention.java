package io.smalldata.beehiveapp.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.util.Date;

import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.ImageSaver;
import io.smalldata.beehiveapp.utils.Store;

/**
 * All daily interventions assigned through Beehive Platform will be handled here.
 * Created by fnokeke on 2/15/17.
 */

public class Intervention extends BaseConfig {
    private  Context mContext;

    public Intervention(Context context) {
        mContext = context;
    }

    @Override
    public void saveSettings(JSONArray interventions) {
        if (interventions == null) {
            interventions = new JSONArray();
        }
        setAllInterventions(interventions);
    }

    private static Boolean isForToday(JSONObject jo) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        Date startDate = Helper.getDatetime(jo.optString("start"), dateFormat);
        Date endDate = Helper.getDatetime(jo.optString("end"), dateFormat);
        long rightNow = java.util.Calendar.getInstance().getTimeInMillis();

        return rightNow >= startDate.getTime() && rightNow <= endDate.getTime();
    }

    private void setAllInterventions(JSONArray interventions) {
        Store.setString(mContext, "interventions", interventions.toString());
        prepareTodayIntervention(mContext);
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

    public JSONObject getTodayIntervention(Context context) {
        prepareTodayIntervention(context);

        JSONObject todayIntervention = new JSONObject();
        Helper.setJSONValue(todayIntervention, "start", Store.getString(context, "iStart"));
        Helper.setJSONValue(todayIntervention, "end", Store.getString(context, "iEnd"));
        Helper.setJSONValue(todayIntervention, "every", Store.getString(context, "iEvery"));
        Helper.setJSONValue(todayIntervention, "when", Store.getString(context, "iWhen"));
        Helper.setJSONValue(todayIntervention, "repeat", Store.getString(context, "iRepeat"));
        Helper.setJSONValue(todayIntervention, "treatment_text", Store.getString(context, "iTreatmentText"));
        Helper.setJSONValue(todayIntervention, "treatment_image", Store.getString(context, "iTreatmentImage"));

        return todayIntervention;
    }

    private void prepareTodayIntervention(Context context) {
        JSONArray interventions = getAllInterventions(context);
        JSONObject intv = new JSONObject();
        for (Integer i = 0; i < interventions.length(); i++) {

            try {
                intv = new JSONObject(interventions.optString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isForToday(intv)) {
                Store.setString(context, "iStart", intv.optString("start"));
                Store.setString(context, "iEnd", intv.optString("end"));
                Store.setString(context, "iEvery", intv.optString("every"));
                Store.setString(context, "iWhen", intv.optString("when"));
                Store.setString(context, "iRepeat", intv.optString("repeat"));
                Store.setString(context, "iTreatmentText", intv.optString("treatment_text"));
                Store.setString(context, "iTreatmentImage", intv.optString("treatment_image"));

                String msg = String.format("Today treatment. text: %s / image: %s",
                        Store.getString(context, "iTreatmentText"),
                        Store.getString(context, "iTreatmentImage"));
                Log.e("BeehiveTreatment", msg);
                downloadImage(Store.getString(context, "iTreatmentImage"));
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

    public static File getTodayImagePath(Context context) {
        ImageSaver imageSaver = new ImageSaver(context);
        return imageSaver.getImagePath("beehiveImages", "beehiveTodayImage.png");
    }

    private void downloadImage(String image_url) {
        if (image_url.equals("")) return;

        Picasso.with(mContext)
                .load(image_url)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              ImageSaver imageSaver = new ImageSaver(mContext);
                              imageSaver.
                                      setFileName("beehiveTodayImage.png").
                                      setDirectoryName("beehiveImages").
                                      save(bitmap);

                              Log.i("BeehiveBitmapSave", "imageSaved");
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                              Log.d("PicassoBitmap", "bitmap failed");
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {}
                      }
                );
    }

//    public static String getStartDate(Context context) {
//        return Store.getString(context, "iStart");
//    }
//
//    public static String getEndDate(Context context) {
//        return Store.getString(context, "iEnd");
//    }


//                .load("http://10.144.46.211:5000/_uploads/photos/salamba-sarvangasana.jpg")
}

// TODO: 2/21/17 use NYC timezone