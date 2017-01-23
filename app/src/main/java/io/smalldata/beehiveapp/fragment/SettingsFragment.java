package io.smalldata.beehiveapp.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.smalldata.beehiveapp.R;

//import android.support.v4.app.Fragment;

/**
 * Created by fnokeke on 1/20/17.
 */


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Settings");
        addPreferencesFromResource(R.xml.preferences);
    }

}
