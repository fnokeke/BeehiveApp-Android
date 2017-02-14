package io.smalldata.beehiveapp.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


/**
 * CallAPI.java for REST API calls
 * Fabian Okeke
 * 1/22/2017
 */


public class CallAPI {

//    final static private String BASE_URL = "https://slm.smalldata.io";
    final static private String BASE_URL = "http://10.0.0.166:5000";
//    final static private String BASE_URL = "http://10.144.4.230:5000";
    final static private String CONNECT_URL = BASE_URL + "/mobile/connect/study";
    final static private String ALL_INTV_URL = BASE_URL + "/mobile/ordered/interventions";
    final static private String CAL_CHECK_CONN_URL = BASE_URL + "/mobile/check/calendar";
    final static private String RT_CHECK_CONN_URL = BASE_URL + "/mobile/check/rescuetime";
    final static private String RT_SUMMARY_URL = BASE_URL + "/rescuetime/summary";
    final static private String RT_ACTIVITY_URL = BASE_URL + "/rescuetime/realtime";
    final static private String CAL_EVENTS_URL = BASE_URL + "/mobile/calendar/events";


    private static JsonObjectRequest createRequest(final String url, final JSONObject params, final VolleyJsonCallback callback) {

        return new JsonObjectRequest(url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        callback.onConnectSuccess(result);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onConnectFailure(error);
                    }
                }
        );

    }

    public static void connectStudy(final Context cxt, final JSONObject params, final VolleyJsonCallback callback) {
        Log.w("Connect study details: ", params.toString());
        JsonObjectRequest request = createRequest(CONNECT_URL, params, callback);
        SingletonRequest.getInstance(cxt).addToRequestQueue(request);
    }

    public static void checkRTConn(final Context cxt, final JSONObject params, final VolleyJsonCallback callback) {
        JsonObjectRequest request = createRequest(RT_CHECK_CONN_URL, params, callback);
        SingletonRequest.getInstance(cxt).addToRequestQueue(request);
    }

    public static void checkCalConn(final Context cxt, final JSONObject params, final VolleyJsonCallback callback) {
        JsonObjectRequest request = createRequest(CAL_CHECK_CONN_URL, params, callback);
        SingletonRequest.getInstance(cxt).addToRequestQueue(request);
    }

    public static void getAllCalEvents(final Context cxt, final JSONObject params, final VolleyJsonCallback callback) {
        JsonObjectRequest request = createRequest(CAL_EVENTS_URL, params, callback);
        SingletonRequest.getInstance(cxt).addToRequestQueue(request);
    }

    public static void getRTSummary(final Context cxt, final JSONObject params, final VolleyJsonCallback callback) {
        JsonObjectRequest request = createRequest(RT_SUMMARY_URL, params, callback);
        SingletonRequest.getInstance(cxt).addToRequestQueue(request);
    }

    public static void getRTRealtimeActivity(final Context cxt, final JSONObject params, final VolleyJsonCallback callback) {
        JsonObjectRequest request = createRequest(RT_ACTIVITY_URL, params, callback);
        SingletonRequest.getInstance(cxt).addToRequestQueue(request);
    }


// TODO:
/*
    'fetch_all_intv_by_order': { // code
      method: 'GET',
      url: BASE_URL + '/mobile/ordered/interventions/:code',
      timeout: TIMEOUT,
      isArray: true
    },

 */


}

