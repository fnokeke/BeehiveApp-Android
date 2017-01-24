package io.smalldata.beehiveapp.fragment;

import android.app.Fragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Store;


/**
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Connect to Study");
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateFieldsFromStore();

        Button submitBtn = (Button) getActivity().findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(submitBtnHandler);

        Button resetBtn = (Button) getActivity().findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(resetBtnHandler);

        Button checkRTBtn = (Button) getActivity().findViewById(R.id.btn_check_rt);
        checkRTBtn.setOnClickListener(checkRTBtnHandler);

        Button checkCalBtn = (Button) getActivity().findViewById(R.id.btn_check_cal);
        checkCalBtn.setOnClickListener(checkCalBtnHandler);
    }

    public void populateFieldsFromStore() {
        Store st = Store.getInstance(getActivity());
        JSONObject fields = new JSONObject();
        setJSONValue(fields, "firstname", st.getString("firstname"));
        setJSONValue(fields, "lastname", st.getString("lastname"));
        setJSONValue(fields, "email", st.getString("email"));
        setJSONValue(fields, "gender", st.getString("gender"));
        setJSONValue(fields, "code", st.getString("code"));
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
    };

    View.OnClickListener checkRTBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            setJSONValue(params, "email", Store.getInstance(getActivity()).getString("email"));
            CallAPI.checkRTConn(getActivity(), params, rtResponseHandler);
        }
    };

    VolleyJsonCallback rtResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e(result.toString(), "rtResponseHandler");
            TextView tv = (TextView) getActivity().findViewById(R.id.tv_check_status);
            TextView howTV = (TextView) getActivity().findViewById(R.id.tv_how_to_conn);

            if (result.optBoolean("rt_response", false)) {
                Display.showSuccess(tv, R.string.rt_is_connected);
                Display.clear(howTV);
            } else {
                Display.showError(tv, R.string.rt_is_not_connected);
                Display.showPlain(howTV, R.string.desc_how_to_connect);
            }
        }
    };

    View.OnClickListener checkCalBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            JSONObject params = new JSONObject();
            setJSONValue(params, "email", Store.getInstance(getActivity()).getString("email"));
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
    };


    public void validateInput() {

    }

    public void setJSONValue(JSONObject jsonObject, String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public JSONObject getFormInput() {
        EditText fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        EditText lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        EditText emailField = (EditText) getActivity().findViewById(R.id.et_email);
        EditText codeField = (EditText) getActivity().findViewById(R.id.et_code);
        Spinner genderField = (Spinner) getActivity().findViewById(R.id.spinner_gender);

        JSONObject map = new JSONObject();
        setJSONValue(map, "firstname", fnField.getText().toString());
        setJSONValue(map, "lastname", lnField.getText().toString());
        setJSONValue(map, "email", emailField.getText().toString());
        setJSONValue(map, "code", codeField.getText().toString());
        setJSONValue(map, "gender", genderField.getSelectedItem().toString());
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
        Store store = Store.getInstance(getActivity());
        store.setString("firstname", user.optString("firstname"));
        store.setString("lastname", user.optString("lastname"));
        store.setString("email", user.optString("email"));
        store.setString("gender", user.optString("gender"));
        store.setString("code", user.optString("code"));
        store.setInt("condition", user.optInt("condition", 1));
    }

    public void storeExperiment(JSONObject experiment) {
        Store store = Store.getInstance(getActivity());
        store.setBoolean("actuators", experiment.optBoolean("actuators"));
        store.setBoolean("geofence", experiment.optBoolean("geofence"));
        store.setBoolean("actuators", experiment.optBoolean("reminder"));
        store.setBoolean("text", experiment.optBoolean("text"));
        store.setBoolean("rescuetime", experiment.optBoolean("rescuetime"));
        store.setBoolean("reminder", experiment.optBoolean("reminder"));
        store.setBoolean("image", experiment.optBoolean("image"));
        store.setBoolean("aware", experiment.optBoolean("aware"));
        store.setString("title", experiment.optString("title", ""));
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

