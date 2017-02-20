package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.RefreshService;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.properties.Intervention;
import io.smalldata.beehiveapp.utils.Constants;
import io.smalldata.beehiveapp.utils.DeviceInfo;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;


/**
 *
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    Context mContext;
    Activity mActivity;
    TextView connResponseTV;
    TextView calRTResponseTV;
    TextView howToConnTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mActivity.setTitle("Connect to Study");

        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();

        populateFieldsFromStore();

        Button submitBtn = (Button) mActivity.findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(submitBtnHandler);

        Button resetBtn = (Button) mActivity.findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(resetBtnHandler);

        Button checkRTBtn = (Button) mActivity.findViewById(R.id.btn_check_rt);
        checkRTBtn.setOnClickListener(checkRTBtnHandler);

        Button checkCalBtn = (Button) mActivity.findViewById(R.id.btn_check_cal);
        checkCalBtn.setOnClickListener(checkCalBtnHandler);

        connResponseTV = (TextView) getActivity().findViewById(R.id.tv_connect_status);
        calRTResponseTV = (TextView) mActivity.findViewById(R.id.tv_check_conn_status);
        howToConnTV = (TextView) mActivity.findViewById(R.id.tv_how_to_conn);
    }

    public void populateFieldsFromStore() {
        JSONObject fields = new JSONObject();
        Helper.setJSONValue(fields, "firstname", Store.getString(mContext, "firstname"));
        Helper.setJSONValue(fields, "lastname", Store.getString(mContext, "lastname"));
        Helper.setJSONValue(fields, "email", Store.getString(mContext, "email"));
        Helper.setJSONValue(fields, "gender", Store.getString(mContext, "gender"));
        Helper.setJSONValue(fields, "code", Store.getString(mContext, "code"));
        populateConnectUI(fields);
    }

    View.OnClickListener resetBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            resetFormInput();
        }
    };

    View.OnClickListener submitBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Display.clear(howToConnTV);
            JSONObject fromPhoneDetails = DeviceInfo.getPhoneDetails(mContext);
            JSONObject toParams = getFormInput();
            Helper.copy(fromPhoneDetails, toParams);

            Helper.showInstantNotif(mContext, "One new message waiting for you.", "Tap to immediately see content.");

            Display.showBusy(mContext, "Transferring your bio...");
            CallAPI.connectStudy(mContext, toParams, connectStudyResponseHandler);
            Log.w("Connect study details: ", toParams.toString());
        }
    };


    VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e("onConnectSuccess: ", result.toString());
            JSONObject response = result.optJSONObject("response");

            JSONObject experiment = result.optJSONObject("experiment");
            Store.saveExperimentSettings(mContext, response, experiment);

            JSONArray calendarSetting = experiment.optJSONArray("calendar_setting");
            Store.saveCalendarSetting(mContext, response, calendarSetting);

            JSONArray interventions = experiment.optJSONArray("interventions");
            new Intervention(mContext).save(interventions);

            JSONObject user = result.optJSONObject("user");
            Store.save_user_features(mContext, response, user);

            updateFormInput(result);
            Display.dismissBusy();

            RefreshService.start(mContext);
//            JSONObject params = getInterventionParams();
//            CallAPI.fetchIntervention(mContext, params, intvResponseHandler);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.e("onConnectFailure: ", error.toString());
            String msg = String.format(Constants.locale, "Error submitting your bio. Please contact researcher. " +
                    "Error details: %s", error.toString());
            Display.showError(connResponseTV, msg);
            error.printStackTrace();
            Display.dismissBusy();
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

//    private JSONObject getInterventionParams() {
//        JSONObject params = new JSONObject();
//        Helper.setJSONValue(params, "code", Store.getString(mContext, "code"));
//        return params;
//    }


//    VolleyJsonCallback intvResponseHandler = new VolleyJsonCallback() {
//        @Override
//        public void onConnectSuccess(JSONObject result) {
//            Log.e("BeehiveIntvSuccess:", result.toString());
//            JSONArray interventions = result.optJSONArray("intv_response");
//            new Intervention(mContext).save(interventions);
//        }
//
//        @Override
//        public void onConnectFailure(VolleyError error) {
//            Log.d("BeehiveIntvError:", error.toString());
//            error.printStackTrace();
//        }
//    };

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
        String msg = String.format(Constants.locale, "Error checking %s connectivity status. " +
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

        JSONObject map = new JSONObject();
        Helper.setJSONValue(map, "firstname", fnField.getText().toString());
        Helper.setJSONValue(map, "lastname", lnField.getText().toString());
        Helper.setJSONValue(map, "email", emailField.getText().toString());
        Helper.setJSONValue(map, "code", codeField.getText().toString());
        Helper.setJSONValue(map, "gender", genderField.getSelectedItem().toString());
        return map;
    }

    public void updateFormInput(JSONObject result) {
        Log.e("updateFormInput", result.toString());

        if (result.optString("user").equals("")) {
            Display.showError(connResponseTV, R.string.invalid_code);
            resetFormInput();
            return;
        }

        JSONObject response = result.optJSONObject("response");
        Display.showSuccess(connResponseTV, response.optString("user_response"));

        try {

            JSONObject user = new JSONObject(result.getString("user"));
            populateConnectUI(user);
            storeUser(user);

            JSONObject experiment = new JSONObject(result.getString("experiment"));
            storeExperiment(experiment);

        } catch (JSONException je) {
            je.printStackTrace();
        }

    }

    public void populateConnectUI(JSONObject user) {
        EditText fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        EditText lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        EditText emailField = (EditText) getActivity().findViewById(R.id.et_email);
        EditText codeField = (EditText) getActivity().findViewById(R.id.et_code);

        fnField.setText(user.optString("firstname"));
        lnField.setText(user.optString("lastname"));
        emailField.setText(user.optString("email"));
        codeField.setText(user.optString("code"));
    }

    public void storeUser(JSONObject user) {
        Log.e("storeUser: ", user.toString());
        Store.setString(mContext, "firstname", user.optString("firstname"));
        Store.setString(mContext, "lastname", user.optString("lastname"));
        Store.setString(mContext, "email", user.optString("email"));
        Store.setString(mContext, "gender", user.optString("gender"));
        Store.setString(mContext, "code", user.optString("code"));
        Store.setInt(mContext, "condition", user.optInt("condition", 1));
    }

    public void storeExperiment(JSONObject experiment) {
        Store.setBoolean(mContext, "actuators", experiment.optBoolean("actuators"));
        Store.setBoolean(mContext, "geofence", experiment.optBoolean("geofence"));
        Store.setBoolean(mContext, "actuators", experiment.optBoolean("reminder"));
        Store.setBoolean(mContext, "text", experiment.optBoolean("text"));
        Store.setBoolean(mContext, "rescuetime", experiment.optBoolean("rescuetime"));
        Store.setBoolean(mContext, "reminder", experiment.optBoolean("reminder"));
        Store.setBoolean(mContext, "image", experiment.optBoolean("image"));
        Store.setBoolean(mContext, "aware", experiment.optBoolean("aware"));
        Store.setString(mContext, "title", experiment.optString("title", ""));
        Store.setString(mContext, "start", experiment.optString("start"));
        Store.setString(mContext, "end", experiment.optString("end"));
    }

    public void resetFormInput() {

        Display.hide(howToConnTV);
        Display.hide(calRTResponseTV);
        Display.hide(connResponseTV);

        final EditText fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        final EditText lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        final EditText emailField = (EditText) getActivity().findViewById(R.id.et_email);
        final EditText codeField = (EditText) getActivity().findViewById(R.id.et_code);

        fnField.setText("");
        lnField.setText("");
        emailField.setText("");
        codeField.setText("");
    }


}

