package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.R.id.studyCodeTV;
import static io.smalldata.beehiveapp.R.id.studyTitleTV;

//import android.support.v4.app.Fragment;

/**
 * Show details of connected experiment
 * Created by fnokeke on 1/20/17.
 */

public class StudyFragment extends Fragment {
    Context mContext;
    Activity mActivity;
    Locale locale = Constants.LOCALE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = getActivity();

        getActivity().setTitle("Experiment Info");
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        JSONObject experimentInfo = Experiment.getExperimentInfo(mContext);

        TextView studyCodeTV = (TextView) mActivity.findViewById(R.id.studyCodeTV);
        studyCodeTV.setText(experimentInfo.optString("code"));

        TextView studyTitleTV = (TextView) mActivity.findViewById(R.id.studyTitleTV);
        studyTitleTV.setText(experimentInfo.optString("title"));

        String start = experimentInfo.optString("start");
        String end = experimentInfo.optString("end");

        if (start.equals("") || end.equals("")) return;

        TextView studyStartTV = (TextView) mActivity.findViewById(R.id.startStudyTV);
        start = getPrettyDate(start);
        studyStartTV.setText(start);

        TextView studyEndTV = (TextView) mActivity.findViewById(R.id.endStudyTV);
        end = getPrettyDate(end);
        studyEndTV.setText(end);
    }

    private String getPrettyDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date result = new Date();
        try {
            result = dateFormat.parse(dateStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        SimpleDateFormat prettyDateFormat = new SimpleDateFormat("'Midnight' EEE, MMM d, yyyy", locale);
        return prettyDateFormat.format(result);
    }

}
