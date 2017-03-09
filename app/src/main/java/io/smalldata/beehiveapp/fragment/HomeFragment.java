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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

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
        promptUserIfNotConnected();
        displayTodayIntervention();
    }

    private void setResources() {
        rescuetimeTV = (TextView) mActivity.findViewById(R.id.tv_rescuetime);
        calendarTV = (TextView) mActivity.findViewById(R.id.tv_calendar);
        needToConnectTV = (TextView) mActivity.findViewById(R.id.tv_need_to_connect);
        homeDetailsTV = (TextView) mActivity.findViewById(R.id.tv_home_details);
        todayTV = (TextView) mActivity.findViewById(R.id.tv_today_text);
        todayImageView = (ImageView) mActivity.findViewById(R.id.iv_today_image);
        intervention = new Intervention(mContext);
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

        JSONObject todayIntervention = intervention.getTodayIntervention(mContext);
        if (Store.getBoolean(mContext, Store.TEXT_FEATURE) || Store.getBoolean(mContext, Store.IMAGE_FEATURE)) {
            showTextImageIntervention(todayIntervention);
        }

        homeDetailsTV.setVisibility(View.VISIBLE);
        if (Store.getBoolean(mContext, Store.RESCUETIME_FEATURE) && SettingsFragment.canShowRescuetimeInfo(mContext)) {
            Rescuetime rescuetime = new Rescuetime(mContext);
            rescuetime.refreshAndStoreStats();
            Display.showSuccess(rescuetimeTV, rescuetime.getStoredStats());
            homeDetailsTV.setVisibility(View.GONE);
        }

        if (Store.getBoolean(mContext, Store.CALENDAR_FEATURE) && SettingsFragment.canShowCalendarInfo(mContext)) {
            GoogleCalendar googleCalendar = new GoogleCalendar(mContext);
            googleCalendar.refreshAndStoreStats();
            Display.showSuccess(calendarTV, googleCalendar.getStoredStats());

            String msg = String.format(Constants.LOCALE, "Updated: %s\n\n %s", Helper.getTimestamp(), calendarTV.getText().toString());
            calendarTV.setText(msg);
            homeDetailsTV.setVisibility(View.GONE);
        }

    }

    private void showTextImageIntervention(JSONObject todayIntervention) {
        String todayText = todayIntervention.optString("treatment_text");
        String todayImage = todayIntervention.optString("treatment_image");

//        todayImage = todayImage.replace("http://localhost:5000", CallAPI.BASE_URL);
//        Picasso.with(mContext).load(todayImage).into(todayImageView);
//        File imagePath = Intervention.getTodayImagePath(mContext);

        if (todayImage.equals("") && !todayText.equals("")) {
            todayTV.setText(todayText + ": " + todayImage);
        } else if (todayText.equals("") && !todayImage.equals("")) {
            Picasso.with(mContext).load(todayImage).into(todayImageView);
        } else if (!todayImage.equals("") && !todayText.equals("")) {
            todayTV.setText(todayText + ": " + todayImage);
            Picasso.with(mContext).load(todayImage).into(todayImageView);
        }


    }

}

