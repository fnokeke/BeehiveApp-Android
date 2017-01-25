package io.smalldata.beehiveapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Created by fnokeke on 1/20/17.
 */

public class HomeFragment extends Fragment {

    TextView todayTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        todayTV = (TextView) getActivity().findViewById(R.id.tv_today_msg);
        showCalEvents();
    }

    private void showCalEvents() {
        JSONObject params = new JSONObject();
        Helper.setJSONValue(params, "email", Store.getInstance(getActivity()).getString("email"));
        Helper.setJSONValue(params, "date", Helper.getTodayDateFmt());
        CallAPI.getAllCalEvents(getActivity(), params, showCalResponseHandler);
    }


    VolleyJsonCallback showCalResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            String resultStr = result.toString();

            Log.d("onConnectSuccess: ", resultStr);

            if (result.optInt("events") == -1) {
                Display.showError(todayTV, "Could not fetch calendar info.");
                return;
            }

            JSONArray mJsonArray = result.optJSONArray("events");

            if (mJsonArray != null) {
                String mJsonStr = getPrettyEvents(mJsonArray);
                String mFreeStr = getFreeTime(mJsonArray);
//                Log.e("mJsonStr: ", mJsonStr + "\n" + mFreeStr);
                Display.showSuccess(todayTV, mJsonStr + "\nFree Hours:\n" + mFreeStr);
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

            HashMap freeHoursOfDay = new HashMap();
            for (Integer i = 0; i < 24; i++) {
                freeHoursOfDay.put(i, String.format("%s:00 hrs", i.toString()));
            }

            JSONObject jo;
            String start, end;
            Date startDT, endDT;

            for (Integer i = 0; i < ja.length(); i++) {
                jo = ja.optJSONObject(i);

                // use only events with specific begin/end time (not just begin/end date)
                if (!(jo.optJSONObject("start").optString("dateTime").equals(""))) {
                    start = jo.optJSONObject("start").optString("dateTime");
                    startDT = getDatetime(start);

                    end = jo.optJSONObject("end").optString("dateTime");
                    endDT = getDatetime(end);

                    removeBusyTime(freeHoursOfDay, startDT, endDT);
                }
            }

            return prettyHours(freeHoursOfDay);
        }

        private Date getDatetime(String datetimeStr) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-05:00", Locale.US);
            Date result = new Date();
            try {
                result = dateFormat.parse(datetimeStr);
            } catch(ParseException pe) {
                pe.printStackTrace();
            }
            return result;
        }

    };

    private String prettyHours(HashMap freeHoursOfDay) {
        String results = "";
        for(Object value : freeHoursOfDay.values())  {
           results += value.toString() + "  " ;
        }
        return results;
    }

    private void removeBusyTime(HashMap freeHrs, Date startDT, Date endDT) {
        int startHr = getHours(startDT);
        int endHr = getHours(endDT);
        if (getMinutes(endDT) > 0) {
            endHr += 1;
        }

        for (int i = startHr; i < endHr; i++) {
            freeHrs.remove(i);
        }
    }

    private int getHours(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    private int getMinutes(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }
}

// TODO: 1/24/17 deal with timeout errors on Volley
