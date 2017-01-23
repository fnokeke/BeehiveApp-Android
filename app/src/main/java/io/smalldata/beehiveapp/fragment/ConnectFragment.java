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
import io.smalldata.beehiveapp.utils.Store;


/**
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Connect Study");
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateFieldsFromStore();

        Button submitBtn = (Button) getActivity().findViewById(R.id.btnSubmit);
        submitBtn.setOnClickListener(submitBtnHandler);

        Button resetBtn = (Button) getActivity().findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(resetBtnHandler);
    }

    public void populateFieldsFromStore() {
        Store st = Store.getInstance(getActivity());
        JSONObject fields = new JSONObject();
        try {
            fields.put("firstname", st.getString("firstname"));
            fields.put("lastname", st.getString("lastname"));
            fields.put("email", st.getString("email"));
            fields.put("gender", st.getString("gender"));
            fields.put("code", st.getString("code"));
        } catch (JSONException je) {
            je.printStackTrace();
        }
        populateConnectUI(fields);
    }

    View.OnClickListener resetBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Log.w("***btOnClick****:", "reset btn clicked");
            resetFormInput();
        }
    };

    View.OnClickListener submitBtnHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Log.w("***btOnClick****:", "submit btn clicked");
            JSONObject params = getFormInput();
            CallAPI.connectStudy(getActivity(), params, connectHandler);
        }
    };


    VolleyJsonCallback connectHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Log.e("onConnectSuccess: ", result.toString());
            updateFormInput(result);
        }
    };

    public JSONObject getFormInput() {
        EditText fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        EditText lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        EditText emailField = (EditText) getActivity().findViewById(R.id.et_email);
        EditText codeField = (EditText) getActivity().findViewById(R.id.et_code);
        Spinner genderField = (Spinner) getActivity().findViewById(R.id.spinner_gender);

        JSONObject map = new JSONObject();
        try {
            map.put("firstname", fnField.getText().toString());
            map.put("lastname", lnField.getText().toString());
            map.put("email", emailField.getText().toString());
            map.put("code", codeField.getText().toString());
            map.put("gender", genderField.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void updateFormInput(JSONObject result) {
        Log.e("updateFormInput", result.toString());

        TextView statusField = (TextView) getActivity().findViewById(R.id.connectStatusTV);

        try {

            if (result.optString("user", "").equals("")) {
                statusField.setText(R.string.invalid_code);
                resetFormInput();
                return;
            }
            statusField.setText(result.getString("response"));

            JSONObject user = new JSONObject(result.optString("user", ""));
            populateConnectUI(user);
            storeUser(user);

            JSONObject experiment = new JSONObject(result.optString("experiment", ""));
            storeExperiment(experiment);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void populateConnectUI(JSONObject user) {
        EditText fnField = (EditText) getActivity().findViewById(R.id.et_fn);
        EditText lnField = (EditText) getActivity().findViewById(R.id.et_ln);
        EditText emailField = (EditText) getActivity().findViewById(R.id.et_email);
        EditText codeField = (EditText) getActivity().findViewById(R.id.et_code);

        try {
            fnField.setText(user.getString("firstname"));
            lnField.setText(user.getString("lastname"));
            emailField.setText(user.getString("email"));
            codeField.setText(user.getString("code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void storeUser(JSONObject user) {
        Log.e("storeUser: ", user.toString());
        try {
            Store store = Store.getInstance(getActivity());
            store.setString("firstname", user.getString("firstname"));
            store.setString("lastname", user.getString("lastname"));
            store.setString("email", user.getString("email"));
            store.setString("gender", user.getString("gender"));
            store.setString("code", user.getString("code"));
            store.setInt("condition", user.getInt("condition"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void storeExperiment(JSONObject experiment) {
        try {
            Store store = Store.getInstance(getActivity());
            store.setBoolean("actuators", experiment.getBoolean("actuators"));
            store.setBoolean("geofence", experiment.getBoolean("geofence"));
            store.setBoolean("actuators", experiment.getBoolean("reminder"));
            store.setBoolean("text", experiment.getBoolean("text"));
            store.setBoolean("rescuetime", experiment.getBoolean("rescuetime"));
            store.setBoolean("reminder", experiment.getBoolean("reminder"));
            store.setBoolean("image", experiment.getBoolean("image"));
            store.setBoolean("aware", experiment.getBoolean("aware"));
            store.setString("title", experiment.getString("title"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

