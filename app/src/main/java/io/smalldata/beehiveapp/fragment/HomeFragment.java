package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.config.GoogleCalendar;
import io.smalldata.beehiveapp.config.Rescuetime;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Fabian Okeke
 * HomeFragment: displays dashboard for any ongoing intervention
 * Created 1/20/17
 */

public class HomeFragment extends Fragment {

    private Context mContext;
    private Activity mActivity;
    private TextView needToConnectTV;
    private TextView rescuetimeTV;
    private TextView calendarTV;
    private TextView usernameTV;
    private TextView homeDetailsTV;
    private TextView todayTV;
    private ImageView todayImageView;
    private Intervention intervention;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = getActivity();

        mActivity.setTitle("Home");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setResources();
        updateUsernameIfPresent();
        promptUserIfNotConnected();
        displayTodayIntervention();
    }

    private void setResources() {
        usernameTV = (TextView) mActivity.findViewById(R.id.tv_username);
        rescuetimeTV = (TextView) mActivity.findViewById(R.id.tv_rescuetime);
        calendarTV = (TextView) mActivity.findViewById(R.id.tv_calendar);
        needToConnectTV = (TextView) mActivity.findViewById(R.id.tv_need_to_connect);
        homeDetailsTV = (TextView) mActivity.findViewById(R.id.tv_home_details);
        todayTV = (TextView) mActivity.findViewById(R.id.tv_today_text);
        todayImageView = (ImageView) mActivity.findViewById(R.id.iv_today_image);
        intervention = new Intervention(mContext);
    }

    private void updateUsernameIfPresent() {
        String userMsg = SettingsFragment.getUsername(mContext);
        if (!userMsg.equals("")) {
            userMsg = String.format("Hello %s,", userMsg);
            usernameTV.setText(userMsg);
            usernameTV.setVisibility(View.VISIBLE);
        }
    }

    private void promptUserIfNotConnected() {
        String email = Experiment.getUserInfo(mContext).optString("email");
        if (email.equals("")) {
            needToConnectTV.setVisibility(View.VISIBLE);
            rescuetimeTV.setVisibility(View.GONE);
            calendarTV.setVisibility(View.GONE);
        } else {
            needToConnectTV.setVisibility(View.GONE);
        }
    }

    private void displayTodayIntervention() {
        if (Experiment.getUserInfo(mContext).length() == 0) return;
        String textUpdate;

        homeDetailsTV.setVisibility(View.VISIBLE);
        if (Store.getBoolean(mContext, Store.RESCUETIME_FEATURE) && SettingsFragment.canShowRescuetimeInfo(mContext)) {
            Rescuetime rescuetime = new Rescuetime(mContext);
            textUpdate = rescuetime.getStoredStats();
            textUpdate = textUpdate.equals("") ? "Rescuetime updates will appear here in a few minutes." : textUpdate;
            Display.showPlain(rescuetimeTV, textUpdate);
            homeDetailsTV.setVisibility(View.GONE);
        }

        if (Store.getBoolean(mContext, Store.CALENDAR_FEATURE) && SettingsFragment.canShowCalendarInfo(mContext)) {
            GoogleCalendar googleCalendar = new GoogleCalendar(mContext);
            googleCalendar.refreshAndStoreStats();
            textUpdate = googleCalendar.getStoredStats();
            textUpdate = textUpdate.equals("") ? "Calendar updates will appear here in a few minutes." : textUpdate;
            Display.showPlain(calendarTV, textUpdate);
            homeDetailsTV.setVisibility(View.GONE);
        }

        if (Store.getBoolean(mContext, Store.TEXT_FEATURE) || Store.getBoolean(mContext, Store.IMAGE_FEATURE)) {
            showTodayTextImageIntervention();
        }


    }

    private void showTodayTextImageIntervention() {
        JSONObject textImage = Intervention.getTodayIntervention(mContext);
        String todayText = textImage.optString("treatment_text");
        String todayImage = textImage.optString("treatment_image");
        if (!todayImage.contains("slm.smalldata.io")) {
          todayImage = todayImage.replace("localhost:5000", "10.0.0.166:5000");
        }

        if (todayImage.equals("") && !todayText.equals("")) {
            todayTV.setText(todayText + ": " + todayImage);
            todayTV.setVisibility(View.VISIBLE);
        } else if (todayText.equals("null") && !todayImage.equals("")) {
            Picasso.with(mContext).load(todayImage).into(todayImageView);
        } else if (!todayImage.equals("") && !todayText.equals("")) {
            todayTV.setText(todayText);
            todayTV.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(todayImage).into(todayImageView);
        }

        if (!todayImage.equals("") && !todayText.equals("") && !todayText.equals("null")) {
            usernameTV.setVisibility(View.GONE);
            homeDetailsTV.setVisibility(View.GONE);
            needToConnectTV.setVisibility(View.GONE);
        }
    }

}

