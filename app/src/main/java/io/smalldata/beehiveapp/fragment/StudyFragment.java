package io.smalldata.beehiveapp.fragment;

import android.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.utils.Store;

//import android.support.v4.app.Fragment;

/**
 * Created by fnokeke on 1/20/17.
 */

public class StudyFragment extends Fragment {
    Store store;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Experiment Info");
        return inflater.inflate(R.layout.fragment_study, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        store = Store.getInstance(getActivity());

        TextView studyCodeTV = (TextView) getActivity().findViewById(R.id.studyCodeTV);
        studyCodeTV.setText(store.getString("code"));

        TextView studyTitleTV = (TextView) getActivity().findViewById(R.id.studyTitleTV);
        studyTitleTV.setText(store.getString("title"));


        TextView studyStartTV = (TextView) getActivity().findViewById(R.id.startStudyTV);
        String start = getPrettyDate(store.getString("start"));
        studyStartTV.setText(start);

        TextView studyEndTV = (TextView) getActivity().findViewById(R.id.endStudyTV);
        String end = getPrettyDate(store.getString("end"));
        studyEndTV.setText(end);
    }

    private String getPrettyDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date result = new Date();
        try {
            result = dateFormat.parse(dateStr);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        SimpleDateFormat prettyDateFormat = new SimpleDateFormat("'Midnight' EEE, MMM d, yyyy", Locale.US);
        return prettyDateFormat.format(result);
    }

}
