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

            JSONArray mJsonArray= result.optJSONArray("events");

            String mJsonStr = getPrettyEvents(mJsonArray);
            Log.e("mJsonStr: ", mJsonStr);
            Display.showSuccess(todayTV, mJsonStr);
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
    };
}
