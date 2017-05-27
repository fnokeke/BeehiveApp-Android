package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.utils.ConnectHelper;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.JsonHelper;
import io.smalldata.beehiveapp.utils.Network;
import io.smalldata.beehiveapp.utils.Store;


/**
 * Connect user to Beehive experiment platform
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    Context mContext;
    Activity mActivity;
    TextView connResponseTV;
    TextView checkDataStreamsStatusTV;
    TextView howToConnTV;
    TextView formTitleTV;
    Button submitBtn;

    EditText fnField;
    EditText lnField;
    EditText emailField;
    EditText codeField;
    Spinner genderField;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = getActivity();
        mActivity.setTitle("Connect to Study");
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(uiBroadcastReceiver, new IntentFilter("ui-form-update"));
        populateConnectUI(Experiment.getUserInfo(mContext));
    }

    private BroadcastReceiver uiBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String connResponse = intent.getStringExtra("formInputResponse");
            String connUser = intent.getStringExtra("formInputUser");
            updateFormInput(connResponse, connUser);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(uiBroadcastReceiver);
    }

    private void initView() {

        fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        emailField = (EditText) getActivity().findViewById(R.id.et_email);
        codeField = (EditText) getActivity().findViewById(R.id.et_code);
        genderField = (Spinner) getActivity().findViewById(R.id.spinner_gender);

        submitBtn = (Button) mActivity.findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(submitBtnHandler);
        shdDisableFormButtons(Store.getBoolean(mContext, Store.IS_EXIT_BUTTON));

        Button checkRTBtn = (Button) mActivity.findViewById(R.id.btn_check_rt);
        checkRTBtn.setOnClickListener(checkRTBtnHandler);

        Button checkCalBtn = (Button) mActivity.findViewById(R.id.btn_check_cal);
        checkCalBtn.setOnClickListener(checkCalBtnHandler);

        connResponseTV = (TextView) getActivity().findViewById(R.id.tv_connect_status);
        formTitleTV = (TextView) mActivity.findViewById(R.id.tv_form_title);
        checkDataStreamsStatusTV = (TextView) mActivity.findViewById(R.id.tv_check_conn_status);
        howToConnTV = (TextView) mActivity.findViewById(R.id.tv_how_to_conn);
    }

    View.OnClickListener submitBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            checkPhoneStateThenConnect();
        }
    };

    private void checkPhoneStateThenConnect() {
        if (!Network.isDeviceOnline(mContext)) {
            Display.showError(formTitleTV, "No network connection.");
            return;
        }

        if (Store.getBoolean(mContext, Store.IS_EXIT_BUTTON)) {
            showExitDialog();
            return;
        }

        Display.clear(howToConnTV);
        JSONObject userInfo = getFormInput();
        new ConnectHelper(mContext, formTitleTV).connectToBeehive(userInfo);
    }


    private void showExitDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("Exit Research Experiment")
                .setMessage("Leaving experiment will reset app. Are you sure you want to exit?")
                .setIcon(R.drawable.ic_experiment)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, false);
                        shdDisableFormButtons(false);
                        resetFormInput();
                        Store.wipeAll(mContext);
                        SettingsFragment.wipeAll(mContext);
                        Toast.makeText(mContext, "Study data wiped from phone. You are no longer enrolled in study.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    View.OnClickListener checkRTBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            JsonHelper.setJSONValue(params, "email", Store.getString(mContext, "email"));

            Display.showBusy(mContext, "Checking your Rescuetime connectivity...");
            CallAPI.checkRTConn(mContext, params, rtResponseHandler);
        }
    };

    VolleyJsonCallback rtResponseHandler = new VolleyJsonCallback() {

        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e("rtCheckSuccess:", result.toString());
            handleConnSuccess(result, "Rescuetime");
            Display.dismissBusy();
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.e("rtCheckConnError:", error.toString());
            handleConnErrors(error, "Rescuetime");
            Display.dismissBusy();
        }
    };

    View.OnClickListener checkCalBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            JsonHelper.setJSONValue(params, "email", Store.getString(mContext, "email"));

            Display.showBusy(mContext, "Checking your Google GoogleCalendar connectivity...");
            CallAPI.checkCalConn(mContext, params, calResponseHandler);
        }
    };

    VolleyJsonCallback calResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e("CalendarCheckSuccess:", result.toString());
            handleConnSuccess(result, "GoogleCalendar");
            Display.dismissBusy();
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.e("CalCheckConnError:", error.toString());
            handleConnErrors(error, "GoogleCalendar");
            Display.dismissBusy();
        }
    };


    public void handleConnSuccess(JSONObject result, String app) {
        Log.e(app + "ResponseHandler", result.toString());

        Integer msgId;
        String responseType = app.equals("Rescuetime") ? "rt_response" : "cal_response";

        if (result.optBoolean(responseType, false)) {
            msgId = app.equals("GoogleCalendar") ? R.string.cal_is_connected : R.string.rt_is_connected;
            Display.showSuccess(checkDataStreamsStatusTV, msgId);
            Display.clear(howToConnTV);
            Display.hide(connResponseTV);
        } else {
            msgId = app.equals("GoogleCalendar") ? R.string.cal_is_not_connected : R.string.rt_is_not_connected;
            Display.showError(checkDataStreamsStatusTV, msgId);
            Display.showPlain(howToConnTV, R.string.desc_how_to_connect);
        }
    }

    public void handleConnErrors(VolleyError error, String app) {
        String msg = String.format(Constants.LOCALE, "Error checking %s connectivity status. " +
                "Please contact researcher. Error details: %s", app, error.toString());
        Display.showError(connResponseTV, msg);
        error.printStackTrace();
    }

    public JSONObject getFormInput() {
        EditText fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        EditText lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        EditText emailField = (EditText) getActivity().findViewById(R.id.et_email);
        EditText codeField = (EditText) getActivity().findViewById(R.id.et_code);
        Spinner genderField = (Spinner) getActivity().findViewById(R.id.spinner_gender);

        JSONObject userInfo = new JSONObject();
        JsonHelper.setJSONValue(userInfo, "firstname", fnField.getText().toString().trim().toLowerCase());
        JsonHelper.setJSONValue(userInfo, "lastname", lnField.getText().toString().trim().toLowerCase());
        JsonHelper.setJSONValue(userInfo, "email", emailField.getText().toString().trim().toLowerCase());
        JsonHelper.setJSONValue(userInfo, "code", codeField.getText().toString().trim().toLowerCase());
        JsonHelper.setJSONValue(userInfo, "gender", genderField.getSelectedItem().toString());
        return userInfo;
    }

    public void updateFormInput(String responseStr, String userStr) {
        if (formTitleTV == null) return;

        JSONObject response = new JSONObject();
        JSONObject user = new JSONObject();

        try {
            response = new JSONObject(responseStr);
            user = new JSONObject(userStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (response.optString("user_response").equals("")) {
            resetFormInput();
            Display.showError(formTitleTV, getString(R.string.invalid_code));
        } else {
            Display.showSuccess(formTitleTV, response.optString("user_response"));
            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, true);
            populateConnectUI(user);
            shdDisableFormButtons(true);
        }
    }

    public void populateConnectUI(JSONObject user) {
        fnField.setText(user.optString("firstname"));
        lnField.setText(user.optString("lastname"));
        emailField.setText(user.optString("email"));
        codeField.setText(user.optString("code"));
    }

    private void shdDisableFormButtons(boolean status) {
        boolean enable = !status;
        fnField.setEnabled(enable);
        lnField.setEnabled(enable);
        genderField.setEnabled(enable);
        emailField.setEnabled(enable);
        codeField.setEnabled(enable);

        if (status) {
            submitBtn.setText(getResources().getString(R.string.exit_study));
            submitBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            submitBtn.setText(getResources().getString(R.string.join_study));
            submitBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void resetFormInput() {
        Display.hide(howToConnTV);
        Display.hide(connResponseTV);
        Display.showPlain(formTitleTV, getString(R.string.desc_connect_status));
        Display.showPlain(checkDataStreamsStatusTV, getString(R.string.desc_check_conn));

        fnField.setText("");
        lnField.setText("");
        emailField.setText("");
        codeField.setText("");
    }

}

