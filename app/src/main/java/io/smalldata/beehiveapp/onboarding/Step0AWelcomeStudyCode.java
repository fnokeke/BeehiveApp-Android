package io.smalldata.beehiveapp.onboarding;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.studyManagement.RSActivityManager;
import io.smalldata.beehiveapp.studyManagement.RSActivity;
import io.smalldata.beehiveapp.utils.ConnectBeehive;
import io.smalldata.beehiveapp.utils.Network;

public class Step0AWelcomeStudyCode extends  RSActivity {
    Context mContext;
    Profile mProfile;
    Button btnContinue;
    TextView tvContinueResponse;
    EditText etWelcomeCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        mProfile = new Profile(mContext);
        super.onCreate(savedInstanceState);
//        if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {
//            RSActivityManager.get().queueActivity(this, "demography", true);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userIsLoggedIn()) {
            showAppInfo();
        } else {
            startOnBoarding();
        }
    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;

            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }

    private boolean userIsLoggedIn() {
        return Profile.usernameExists(mContext);
    }

    private void showAppInfo() {
        if (!mProfile.userCompletedAllSteps()) {
            startActivity(new Intent(mContext, Step1SleepWakeTime.class));
        } else {
            startActivity(new Intent(mContext, AppInfo.class));
        }
    }

    private void startOnBoarding() {
        setContentView(R.layout.welcome);
        tvContinueResponse = (TextView) findViewById(R.id.tv_continue_response);
        etWelcomeCode = (EditText) findViewById(R.id.et_welcome_code);

        JSONObject userInfo = Experiment.getUserInfo(mContext);
        etWelcomeCode.setText(userInfo.optString("code"));

        btnContinue = (Button) findViewById(R.id.btn_welcome_continue);
        btnContinue.setOnClickListener(btnContinueHandler);
    }

    View.OnClickListener btnContinueHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Boolean canContinue = isValidInputAndIsOnline(tvContinueResponse);
            if (canContinue) {
                ConnectBeehive connectBeehive = new ConnectBeehive(mContext, tvContinueResponse);
                connectBeehive.fetchStudyUsingCode(etWelcomeCode.getText().toString().trim().toLowerCase());
            }
        }
    };

    private boolean isValidInputAndIsOnline(TextView tvFeedback) {
        String code = etWelcomeCode.getText().toString().trim();
        boolean canContinue = false;

        if (isValidCodeEntered(code) && Network.isDeviceOnline(mContext)) {
            canContinue = true;
            tvFeedback.setText(R.string.welcome_connecting);
        } else if (!Network.isDeviceOnline(mContext)) {
            tvContinueResponse.setText(R.string.welcome_no_network);
        } else if (!isValidCodeEntered(code)) {
            tvFeedback.setText(R.string.welcome_valid_code_needed);
        }
        return canContinue;
    }

    private boolean isValidCodeEntered(String code) {
        return !code.equals("");
    }

}
