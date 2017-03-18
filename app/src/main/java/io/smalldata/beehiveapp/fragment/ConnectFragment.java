package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.main.Experiment;
import io.smalldata.beehiveapp.main.RefreshService;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Network;
import io.smalldata.beehiveapp.utils.Store;


/**
 * Connect user to Beehive experiment platform
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    final static String TAG = "ConnectFragment";
    Context mContext;
    Activity mActivity;
    TextView connResponseTV;
    TextView calRTResponseTV;
    TextView howToConnTV;
    TextView formTitleTV;
    Button submitBtn;

    EditText fnField;
    EditText lnField;
    EditText emailField;
    EditText codeField;
    Spinner genderField;
    Resources resources;

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
        prepareDisplay();
        populateConnectUI(Experiment.getUserInfo(mContext));
    }

    private void prepareDisplay() {
        resources = getResources();

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
        calRTResponseTV = (TextView) mActivity.findViewById(R.id.tv_check_conn_status);
        howToConnTV = (TextView) mActivity.findViewById(R.id.tv_how_to_conn);

    }

    View.OnClickListener submitBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            if (!Network.isDeviceOnline(mContext)) {
                Display.showError(formTitleTV, "No network connection.");
                return;
            }

            if (Store.getBoolean(mContext, Store.IS_EXIT_BUTTON)) {
                showExitDialog();
                return;
            }

            Display.clear(howToConnTV);
            JSONObject fromPhoneDetails = DeviceInfo.getPhoneDetails(mContext);
            JSONObject toParams = getFormInput();
            Helper.copy(fromPhoneDetails, toParams);

            Display.showBusy(mContext, "Transferring your bio...");
            CallAPI.connectStudy(mContext, toParams, connectStudyResponseHandler);
            Log.i("Connect study details: ", toParams.toString());
        }
    };

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
                        Toast.makeText(mContext, "All data wiped. You are no longer part of the study.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
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

    VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.i(TAG, "onConnectStudySuccess: " + result.toString());
            Store.wipeAll(mContext);
            SettingsFragment.wipeAll(mContext);

            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, true);
            shdDisableFormButtons(true);

            Experiment experiment = new Experiment(mContext);
            JSONObject experimentInfo = result.optJSONObject("experiment");
            experiment.saveConfigs(experimentInfo);

            JSONObject user = result.optJSONObject("user");
            experiment.saveUserInfo(user);

            JSONObject response = result.optJSONObject("response");
            updateFormInput(response, user);

            Display.dismissBusy();
            RefreshService.startRefreshInIntervals(mContext);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Store.setBoolean(mContext, Store.IS_EXIT_BUTTON, false);
            Log.e("onConnectFailure: ", error.toString());
            Display.showError(formTitleTV, "Cannot submit your bio.");
            String msg = String.format(Constants.LOCALE, "Error submitting your bio. Please contact researcher. " +
                    "Error details: %s", error.toString());
            Display.showError(connResponseTV, msg);
            Display.dismissBusy();
            error.printStackTrace();
        }
    };

    View.OnClickListener checkRTBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            Helper.setJSONValue(params, "email", Store.getString(mContext, "email"));

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
            Helper.setJSONValue(params, "email", Store.getString(mContext, "email"));

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
            Display.showSuccess(calRTResponseTV, msgId);
            Display.clear(howToConnTV);
            Display.hide(connResponseTV);
        } else {
            msgId = app.equals("GoogleCalendar") ? R.string.cal_is_not_connected : R.string.rt_is_not_connected;
            Display.showError(calRTResponseTV, msgId);
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
        Helper.setJSONValue(userInfo, "firstname", fnField.getText().toString());
        Helper.setJSONValue(userInfo, "lastname", lnField.getText().toString());
        Helper.setJSONValue(userInfo, "email", emailField.getText().toString());
        Helper.setJSONValue(userInfo, "code", codeField.getText().toString());
        Helper.setJSONValue(userInfo, "gender", genderField.getSelectedItem().toString());
        return userInfo;
    }

    public void updateFormInput(JSONObject response, JSONObject user) {
        if (response.optString("user_response").equals("")) {
            Display.showError(formTitleTV, R.string.invalid_code);
            resetFormInput();
        } else {
            Display.showSuccess(formTitleTV, response.optString("user_response"));
            populateConnectUI(user);
        }
    }

    public void populateConnectUI(JSONObject user) {
        fnField.setText(user.optString("firstname"));
        lnField.setText(user.optString("lastname"));
        emailField.setText(user.optString("email"));
        codeField.setText(user.optString("code"));
    }

    public void resetFormInput() {
        Display.hide(howToConnTV);
        Display.hide(calRTResponseTV);
        Display.hide(connResponseTV);
        Display.showPlain(formTitleTV, getResources().getString(R.string.desc_connect_status));

        fnField.setText("");
        lnField.setText("");
        emailField.setText("");
        codeField.setText("");
    }
}

