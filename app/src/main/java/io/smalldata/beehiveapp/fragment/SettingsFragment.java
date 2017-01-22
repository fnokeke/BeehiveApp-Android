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

public class SettingsFragment extends Fragment {

    View settingsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsView = inflater.inflate(R.layout.fragment_settings, container, false);
        return settingsView;
    }
}
