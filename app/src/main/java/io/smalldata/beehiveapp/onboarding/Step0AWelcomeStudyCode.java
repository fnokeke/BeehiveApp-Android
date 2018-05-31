package io.smalldata.beehiveapp.onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.main.AppInfo;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.ConnectBeehive;
import io.smalldata.beehiveapp.utils.Network;

public class Step0AWelcomeStudyCode extends AppCompatActivity {
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
