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

import org.json.JSONException;
import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Helper;
import io.smalldata.beehiveapp.utils.Store;


/**
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    Context mContext;
    Activity mActivity;

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
            setDefaultUI();
            resetFormInput();
        }
    };

    View.OnClickListener submitBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            setDefaultUI();
            JSONObject params = getFormInput();
            CallAPI.connectStudy(getActivity(), params, connectStudyResponseHandler);
        }
    };

    public void setDefaultUI() {
        TextView titleTV = (TextView) getActivity().findViewById(R.id.tv_check_status);
        Display.showPlain(titleTV, R.string.desc_check_conn);

        TextView howTV = (TextView) getActivity().findViewById(R.id.tv_how_to_conn);
        Display.clear(howTV);
    }

    VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e("onConnectSuccess: ", result.toString());
            updateFormInput(result);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
        }
    };

    View.OnClickListener checkRTBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            Helper.setJSONValue(params, "email", Store.getString(mContext, "email"));
            CallAPI.checkRTConn(mActivity, params, rtResponseHandler);
        }
    };

    VolleyJsonCallback rtResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e(result.toString(), "rtResponseHandler");
            TextView tv = (TextView) mActivity.findViewById(R.id.tv_check_status);
            TextView howTV = (TextView) mActivity.findViewById(R.id.tv_how_to_conn);

            if (result.optBoolean("rt_response", false)) {
                Display.showSuccess(tv, R.string.rt_is_connected);
                Display.clear(howTV);
            } else {
                Display.showError(tv, R.string.rt_is_not_connected);
                Display.showPlain(howTV, R.string.desc_how_to_connect);
            }
        }

        @Override
        public void onConnectFailure(VolleyError error) {
        }
    };

    View.OnClickListener checkCalBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            Helper.setJSONValue(params, "email", Store.getString(mContext, "email"));
            CallAPI.checkCalConn(getActivity(), params, calResponseHandler);
        }
    };

    VolleyJsonCallback calResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e(result.toString(), "calResponseHandler");
            TextView tv = (TextView) getActivity().findViewById(R.id.tv_check_status);
            TextView howTV = (TextView) getActivity().findViewById(R.id.tv_how_to_conn);

            if (result.optBoolean("cal_response", false)) {
                Display.showSuccess(tv, R.string.cal_is_connected);
                Display.clear(howTV);
            } else {
                Display.showError(tv, R.string.cal_is_not_connected);
                Display.showPlain(howTV, R.string.desc_how_to_connect);
            }
        }

        @Override
        public void onConnectFailure(VolleyError error) {
        }
    };


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

        TextView statusField = (TextView) getActivity().findViewById(R.id.connectStatusTV);

        if (result.optString("user").equals("")) {
            Display.showError(statusField, R.string.invalid_code);
            resetFormInput();
            return;
        }

        Display.showSuccess(statusField, result.optString("response"));

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

