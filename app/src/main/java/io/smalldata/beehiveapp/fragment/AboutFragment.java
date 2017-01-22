package io.smalldata.beehiveapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.smalldata.beehiveapp.R;

//import android.support.v4.app.Fragment;

/**
 * Created by fnokeke on 1/20/17.
 */

public class AboutFragment extends Fragment {

    View aboutView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        aboutView = inflater.inflate(R.layout.fragment_about, container, false);
        return aboutView;
    }
}
