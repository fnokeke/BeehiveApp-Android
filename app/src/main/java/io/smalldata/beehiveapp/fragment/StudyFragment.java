package io.smalldata.beehiveapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        getActivity().setTitle("Study Details");
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
    }
}
