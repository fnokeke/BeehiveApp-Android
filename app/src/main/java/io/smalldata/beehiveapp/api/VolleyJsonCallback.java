package io.smalldata.beehiveapp.api;


import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Callback for REST API requests
 * Created by fnokeke on 1/22/17.
 */


public interface VolleyJsonCallback {
    void onConnectSuccess(JSONObject result);
    void onConnectFailure(VolleyError error);
}

