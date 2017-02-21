package io.smalldata.beehiveapp.config;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Google Calendar configurations set on Beehive Platform will be handled here.
 * Created by fnokeke on 2/15/17.
 */

public class GoogleCalendar extends BaseConfig {
    private Context mContext;
    private final static Locale locale = Constants.LOCALE;
    private final static String STATS_CAL = "statsCal";
    private final static String EVENT_NUM_LIMIT = "event_num_limit";
    private final static String EVENT_TIME_LIMIT = "event_time_limit";

    public GoogleCalendar(Context context) {
        mContext = context;
    }

    public void saveSettings(JSONArray calendarConfig) {
        if (calendarConfig == null || calendarConfig.length() == 0) return;
        JSONObject lastItem = calendarConfig.optJSONObject(calendarConfig.length() - 1);
        Store.setString(mContext, EVENT_NUM_LIMIT, lastItem.optString(EVENT_NUM_LIMIT));
        Store.setString(mContext, EVENT_TIME_LIMIT, lastItem.optString(EVENT_TIME_LIMIT));
    }

    public String getStoredStats() {
        return Store.getString(mContext, STATS_CAL);
    }

    public String getTimeLimit() {
        return Store.getString(mContext, EVENT_TIME_LIMIT);

    }
    public String getNumLimit() {
        return Store.getString(mContext, EVENT_NUM_LIMIT);
    }

    public void refreshAndStoreStats() {
        String email = Store.getString(mContext, "email");
        if (email.equals("")) { return; }

        JSONObject params = new JSONObject();
        Helper.setJSONValue(params, "email", email);
        Helper.setJSONValue(params, "date", Helper.getTodayDateStr());

        CallAPI.getAllCalEvents(mContext, params, showCalResponseHandler);
    }

    private VolleyJsonCallback showCalResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            String resultStr = result.toString();
            Log.d("onConnectSuccess: ", resultStr);
            if (result.optInt("events") == -1) {
                Store.setString(mContext, "errorCal", "There are no available calendar events.");
                return;
            }

            JSONArray mJsonArray = result.optJSONArray("events");

            if (mJsonArray != null) {
                String mJsonStr = getPrettyEvents(mJsonArray);
                long busyTimeMs = computeBusyTimeMs(mJsonArray);
                Float busyHours = (float) busyTimeMs / 3600000;
                Integer noOfTodayEvents = countTodayEvents(mJsonArray);

                String statsCal = "No of events today: " + noOfTodayEvents.toString() + "\n\n" +
                        "Total Busy Hours: " + String.format(locale, "%.02f", busyHours) + "\n\n" +
                        "Today Events:\n" + mJsonStr +
                        "Potential Notification Time:\n" + getFreeTime(mJsonArray) ;
                Store.setString(mContext, STATS_CAL, statsCal);
            }
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            handleVolleyError(error);
        }
    };

    private Integer countTodayEvents(JSONArray ja) {
        JSONObject jo;
        Integer totalEventCount = 0;

        for (Integer i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            // use only events with specific begin/end time (not just begin/end date)
            if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                totalEventCount += 1;
            }
        }
        return totalEventCount;
    }

    private long computeBusyTimeMs(JSONArray ja) {
        JSONObject jo;
        String start, end;
        Date startDT, endDT;
        long totalMs = 0;

        for (Integer i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            // use only events with specific begin/end time (not just begin/end date)
            if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                start = jo.optJSONObject("start").optString("dateTime");
                startDT = Helper.getDatetime(start);

                end = jo.optJSONObject("end").optString("dateTime");
                endDT = Helper.getDatetime(end);

                totalMs += endDT.getTime() - startDT.getTime();
            }
        }

        return totalMs;
    }

    private void handleVolleyError(VolleyError error) {
        error.printStackTrace();
        if (error instanceof TimeoutError) {
            Log.d("**TimeoutError**", "Check that your server is up and running");
            Store.setString(mContext, "errorRT", "Error fetching Rescuetime data. No network connection detected.");
        } else if (error instanceof ServerError) {
            Log.d("**ServerError**", "Typically happens when your API url endpoint is incorrect.");
            Store.setString(mContext, "errorRT", "Experiment is unavailable. Please contact researcher.");
        } else {
            String msg = "Something went wrong while fetching your info. Please contact your researcher. \n\nError details: " + error.toString();
            Store.setString(mContext, "errorRT", msg);
        }
    }

    private String getPrettyEvents(JSONArray ja) {

        String results = "";
        String summary, start, end;
        JSONObject jo, joTmp;

        for (int i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            summary = jo.optString("summary");

            joTmp = jo.optJSONObject("start");
            start = joTmp.optString("dateTime").equals("") ? joTmp.optString("date") : joTmp.optString("dateTime");

            joTmp = jo.optJSONObject("end");
            end = joTmp.optString("dateTime").equals("") ? joTmp.optString("date") : joTmp.optString("dateTime");

            results += summary + "\n" + start + " to " + end + "\n\n";
        }

        return results;
    }

    private String getFreeTime(JSONArray ja) {

        SparseArray<String> freeHoursOfDay = new SparseArray<>();
        for (Integer i = 0; i < 24; i++) {
            freeHoursOfDay.put(i, String.format(locale, "%s:00 hrs", i.toString()));
        }

        JSONObject jo;
        String start, end;
        Date startDT, endDT;

        for (Integer i = 0; i < ja.length(); i++) {
            jo = ja.optJSONObject(i);

            // use only events with specific begin/end time (not just begin/end date)
            if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                start = jo.optJSONObject("start").optString("dateTime");
                startDT = Helper.getDatetime(start);

                end = jo.optJSONObject("end").optString("dateTime");
                endDT = Helper.getDatetime(end);

                removeBusyTime(freeHoursOfDay, startDT, endDT);
            }
        }

        return prettyHours(freeHoursOfDay);
    }

    private static String prettyHours(SparseArray freeHoursOfDay) {
        String results = "";
        for(int i = 0; i < freeHoursOfDay.size(); i++) {
            results += freeHoursOfDay.valueAt(i).toString() + " ";
        }
        return results;
    }

    private static void removeBusyTime(SparseArray freeHrs, Date startDT, Date endDT) {
        int startHr = getHours(startDT);
        int endHr = getHours(endDT);
        if (getMinutes(endDT) > 0) {
            endHr += 1;
        }

        for (int i = startHr; i < endHr; i++) {
            freeHrs.remove(i);
        }
    }

    private static int getHours(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private static int getMinutes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

}
