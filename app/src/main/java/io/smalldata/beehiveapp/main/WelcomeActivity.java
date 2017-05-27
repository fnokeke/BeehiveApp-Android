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

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.utils.ConnectBeehiveHelper;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Network;

import static com.android.volley.Request.Method.HEAD;

public class WelcomeActivity extends AppCompatActivity {
    Context mContext;
    Button btnContinue;
    TextView tvContinueResponse;
    EditText etWelcomeEmail;
    EditText etWelcomeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        setResources();
    }

    private void setResources() {
        tvContinueResponse = (TextView) findViewById(R.id.tv_continue_response);
        etWelcomeEmail = (EditText) findViewById(R.id.et_welcome_email);
        etWelcomeCode = (EditText) findViewById(R.id.et_welcome_code);
        btnContinue = (Button) findViewById(R.id.btn_welcome_continue);
        btnContinue.setOnClickListener(btnContinueHandler);
    }

    View.OnClickListener btnContinueHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Boolean canContinue = checkUserInputAndNetworkConnection(tvContinueResponse);
            if (canContinue) {
                JSONObject userInfo = new JSONObject();
                JsonHelper.setJSONValue(userInfo, "email", etWelcomeEmail.getText().toString().trim().toLowerCase());
                JsonHelper.setJSONValue(userInfo, "code", etWelcomeCode.getText().toString().trim().toLowerCase());
                ConnectBeehiveHelper connectBeehiveHelper = new ConnectBeehiveHelper(mContext, tvContinueResponse);
                connectBeehiveHelper.connectToBeehive(userInfo);
                startActivity(new Intent(mContext, MainActivity.class));
            }
        }
    };

    private boolean checkUserInputAndNetworkConnection(TextView tvFeedback) {
        String email = etWelcomeEmail.getText().toString().trim();
        String code = etWelcomeCode.getText().toString().trim();
        boolean canContinue = false;

        if (isValidEmail(email) && isValidCode(code) && Network.isDeviceOnline(mContext)) {
            canContinue = true;
            tvFeedback.setText("Connecting...");
        }  else if (!Network.isDeviceOnline(mContext)){
            tvContinueResponse.setText("No network connection...");
        } else if (!isValidEmail(email)) {
            tvFeedback.setText("Umm... valid email needed.");
        } else if (!isValidCode(code)) {
            tvFeedback.setText("Errm... enter a valid code.");
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
