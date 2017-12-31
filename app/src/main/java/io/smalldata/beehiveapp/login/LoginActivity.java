package io.smalldata.beehiveapp.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.main.MainActivity;
import io.smalldata.beehiveapp.main.Profile;
import io.smalldata.beehiveapp.utils.JsonHelper;

public class LoginActivity extends Activity {

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
            String[] dataParts = data.toString().split("=");
            if (dataParts.length > 1) {
                JSONObject user = JsonHelper.strToJsonObject(dataParts[1]);
                mProfile.saveFirstname(user.optString("firstname"));
                mProfile.saveUsername(user.optString("email"));
                onboardUserTimePref();
            }
        }

    }

    private void onboardUserTimePref() {
        Intent beginStudyIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(beginStudyIntent);
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CallAPI.GOOGLE_LOGIN_NO_OHMAGE_URL));
        startActivity(browserIntent);
    }
}
