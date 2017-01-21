package io.smalldata.beehiveapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by fnokeke on 1/20/17.
 */

public class DashboardFragment extends Fragment {

    View dashView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dashView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return dashView;
    }
}
