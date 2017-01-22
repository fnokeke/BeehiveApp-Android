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

import org.json.JSONException;
import org.json.JSONObject;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;


/**
 * Created by fnokeke on 1/20/17.
 */

public class ConnectFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button submitBtn = (Button) getActivity().findViewById(R.id.btnSubmit);
        submitBtn.setOnClickListener(submitBtnHandler);

        Button resetBtn = (Button) getActivity().findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(resetBtnHandler);
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

