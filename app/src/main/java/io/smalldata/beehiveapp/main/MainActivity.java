package io.smalldata.beehiveapp.main;

//import android.support.v4.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.Context;
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

import com.android.volley.VolleyError;
//import com.crashlytics.android.Crashlytics; // FIXME: 12/17/17 remove comment
//import com.google.firebase.messaging.FirebaseMessaging; // FIXME: 12/17/17 remove comment

import org.json.JSONObject;

//import io.fabric.sdk.android.Fabric; // FIXME: 12/17/17 remove comment
import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.fragment.AboutFragment;
import io.smalldata.beehiveapp.fragment.ConnectFragment;
import io.smalldata.beehiveapp.fragment.SettingsFragment;
import io.smalldata.beehiveapp.fragment.StudyFragment;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.IntentLauncher;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Network;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext;
    TextView mTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        Fabric.with(this, new Crashlytics()); // FIXME: 12/17/17 remove comment

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
        transaction.replace(R.id.content_frame, new ConnectFragment()).commit();

        mTV = (TextView) findViewById(R.id.tv_timeout_prompt);
    }

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
//        if (id == R.id.action_refresh_stats) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        promptIfDisconnected();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        switch (id) {
            case (R.id.nav_study):
                transaction.replace(R.id.content_frame, new StudyFragment()).commit();
                break;
            case (R.id.nav_connect):
                transaction.replace(R.id.content_frame, new ConnectFragment()).commit();
                break;
            case (R.id.nav_settings):
                transaction.replace(R.id.content_frame, new SettingsFragment()).commit();
                break;
            case (R.id.nav_about):
                transaction.replace(R.id.content_frame, new AboutFragment()).commit();
                break;
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

}

// TODO: 5/25/17 move all raw strings to strings.xml
// TODO: 5/25/17 click on notif to go to settings 
// TODO: 5/25/17 do not disable window time in order to accommodate for days where researcher does not want to fire any autoUpdateAlarm
// TODO: 6/6/17 add: Participant since date


