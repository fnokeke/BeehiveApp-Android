package io.smalldata.beehiveapp.main;

//import android.support.v4.app.FragmentManager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.fragment.AboutFragment;
import io.smalldata.beehiveapp.fragment.ConnectFragment;
import io.smalldata.beehiveapp.fragment.HomeFragment;
import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.fragment.StudyFragment;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.Network;
import io.smalldata.beehiveapp.utils.Store;

import static android.R.attr.handle;
import static io.smalldata.beehiveapp.config.Intervention.getNotifDetails;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    View mView;
    Context mContext;
    TextView mTV;
    private static final String TAG = "MainActivity.class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (savedInstanceState == null) {
            handleClickedNotification(getIntent().getExtras());
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, new HomeFragment()).commit();

        mTV = (TextView) findViewById(R.id.tv_timeout_prompt);

    }

    private void handleClickedNotification(Bundle bundle) {
        if(bundle != null){
            String appIdToLaunch = bundle.getString("appId");
            boolean was_dismissed = bundle.getBoolean("was_dismissed");

            if (!was_dismissed) {
                IntentLauncher.launchApp(mContext, appIdToLaunch);
            }

            JSONObject params = Intervention.getNotifDetails(mContext);
            Helper.setJSONValue(params, "ringer_mode", DeviceInfo.getRingerMode(mContext));
            Helper.setJSONValue(params, "time_appeared", Store.getString(mContext, Store.LAST_NOTIF_TIME));
            Helper.setJSONValue(params, "time_clicked", String.valueOf(Helper.getTimestampInMillis()));
            Helper.setJSONValue(params, "was_dismissed", was_dismissed);
            CallAPI.addNotifClickedStats(mContext, params, submitNotifClickHandler);
        }
    }

    VolleyJsonCallback submitNotifClickHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.i("MainActivity.class", "submit_notif_stats success " + result.toString());
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.e("MainActivity.class", "submit_notif_stats error " + error.toString());
            error.printStackTrace();
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh_stats) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        promptIfDisconnected();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_dashboard) {

            transaction.replace(R.id.content_frame, new HomeFragment()).commit();

        } else if (id == R.id.nav_study) {

            transaction.replace(R.id.content_frame, new StudyFragment()).commit();

        } else if (id == R.id.nav_connect) {

            transaction.replace(R.id.content_frame, new ConnectFragment()).commit();

        } else if (id == R.id.nav_settings) {

            transaction.replace(R.id.content_frame, new SettingsFragment()).commit();

        } else if (id == R.id.nav_about) {

            transaction.replace(R.id.content_frame, new AboutFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void promptIfDisconnected() {
        if (!Network.isDeviceOnline(getApplicationContext())) {
            findViewById(R.id.tv_offline_prompt).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tv_offline_prompt).setVisibility(View.INVISIBLE);
        }
    }

    public void stupidErrorPrompt(Boolean shouldDisplay) {
        if (shouldDisplay) {
            mTV.setVisibility(View.VISIBLE);
        } else {
            mTV.setVisibility(View.INVISIBLE);
        }
    }

//    public TextView getTVTimeout() {
//       return (TextView)findViewById(R.id.tv_timeout_prompt);
//    }

//    public void (Boolean isTimeoutError) {
//        if (isTimeoutError) {
//            findViewById(R.id.tv_timeout_prompt).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.tv_timeout_prompt).setVisibility(View.INVISIBLE);
//        }
//
//    }
}
