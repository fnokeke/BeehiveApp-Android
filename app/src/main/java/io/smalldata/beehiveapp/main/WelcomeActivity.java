package io.smalldata.beehiveapp.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.utils.ConnectBeehiveHelper;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Network;

import static com.android.volley.Request.Method.HEAD;

public class WelcomeActivity extends AppCompatActivity {
    Context mContext;
    Button btnContinue;
    TextView tvContinueResponse;
    //    EditText etWelcomeEmail;
    EditText etWelcomeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Experiment.userAlreadyConnected(mContext)) {
            startActivity(new Intent(mContext, MainActivity.class));
        } else {
            setContentView(R.layout.welcome);
            setResources();
        }
    }

    private void setResources() {
        tvContinueResponse = (TextView) findViewById(R.id.tv_continue_response);
//        etWelcomeEmail = (EditText) findViewById(R.id.et_welcome_email);
        etWelcomeCode = (EditText) findViewById(R.id.et_welcome_code);

        JSONObject userInfo = Experiment.getUserInfo(mContext);
//        etWelcomeEmail.setText(userInfo.optString("email"));
        etWelcomeCode.setText(userInfo.optString("code"));

        btnContinue = (Button) findViewById(R.id.btn_welcome_continue);
        btnContinue.setOnClickListener(btnContinueHandler);
    }

    View.OnClickListener btnContinueHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Boolean canContinue = checkUserInputAndNetworkConnection(tvContinueResponse);
            if (canContinue) {
                ConnectBeehiveHelper connectBeehiveHelper = new ConnectBeehiveHelper(mContext, tvContinueResponse);
                connectBeehiveHelper.fetchStudyUsingCode(etWelcomeCode.getText().toString().trim().toLowerCase());
//                startActivity(new Intent(mContext, MainActivity.class));
            }
        }
    };

    private boolean checkUserInputAndNetworkConnection(TextView tvFeedback) {
//        String email = etWelcomeEmail.getText().toString().trim();
        String code = etWelcomeCode.getText().toString().trim();
        boolean canContinue = false;

        if (isValidCode(code) && Network.isDeviceOnline(mContext)) {
            canContinue = true;
            tvFeedback.setText(R.string.welcome_connecting);
        } else if (!Network.isDeviceOnline(mContext)) {
            tvContinueResponse.setText(R.string.welcome_no_network);
//        } else if (!isValidEmail(email)) {
//            tvFeedback.setText(R.string.welcome_valid_email_needed);
        } else if (!isValidCode(code)) {
            tvFeedback.setText(R.string.welcome_valid_code_needed);
        }
        return canContinue;
    }

    private boolean isValidCode(String code) {
        return !code.equals("");
    }


    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
