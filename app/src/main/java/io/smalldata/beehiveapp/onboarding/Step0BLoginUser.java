package io.smalldata.beehiveapp.onboarding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.fcm.LocalStorage;
import io.smalldata.beehiveapp.fcm.ServerPeriodicUpdateReceiver;

public class Step0BLoginUser extends Activity {

    Context mContext;
    Profile mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        completeGoogleLoginIfOngoing();
    }

    private void completeGoogleLoginIfOngoing() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            prepareUserEnv(data);
        }

    }

    private void prepareUserEnv(Uri data) {
        String[] dataParts = data.toString().split("\\?");
        if (dataParts.length > 1) {
            mProfile.saveFirstname(dataParts[1]);
            mProfile.saveUsername(dataParts[2]);
            mProfile.setTodayAsFirstDay();
            LocalStorage.initAllStorageFiles(mContext);
            ServerPeriodicUpdateReceiver.registerUserOnLoginComplete(mContext);
            ServerPeriodicUpdateReceiver.startRepeatingServerTask(getApplicationContext()); // FIXME: 1/25/18 only call this if user actually resets account
            onboardUserTimePref();
        }
    }

    private void onboardUserTimePref() {
        startActivity(new Intent(mContext, Step1SleepWakeTime.class));
    }

    private void setResources() {
        mContext = this;
        mProfile = new Profile(mContext);
        Button btnGoogleLogin = (Button) findViewById(R.id.btn_google_login);
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleLoginURL();
            }
        });
    }

    private void openGoogleLoginURL() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CallAPI.GOOGLE_LOGIN_URL));
        startActivity(browserIntent);
    }
}
