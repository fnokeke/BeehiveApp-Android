package io.smalldata.beehiveapp.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.main.MainActivity;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setResources();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readIntent();
    }

    private void readIntent() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String[] dataParts = data.toString().split("=");
            String username = "Not Signed In";
            if (dataParts.length > 1) {
                username = dataParts[1];
            }
            Intent beginStudyIntent = new Intent(getBaseContext(), MainActivity.class);
            beginStudyIntent.putExtra("username", username);
            startActivity(beginStudyIntent);
        }

    }

    private void setResources() {

        Button btnGoogleLogin = (Button) findViewById(R.id.btn_google_login);
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleLoginURL();
            }

            private void openGoogleLoginURL() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CallAPI.GOOGLE_LOGIN_NO_OHMAGE_URL));
                startActivity(browserIntent);
            }
        });

    }
}
