package io.smalldata.beehiveapp.api;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import io.smalldata.beehiveapp.utils.Network;


/**
 * CallAPI.java for REST API calls
 * Fabian Okeke
 * 1/22/2017
 */


public class CallAPI {

//        final static private String BASE_URL = "http://10.0.2.2:5000";
    final static private String BASE_URL = "http://slm.smalldata.io";
    final static private String CONNECT_URL = BASE_URL + "/mobile/connect/study";
    final static private String CAL_CHECK_CONN_URL = BASE_URL + "/mobile/check/calendar";
    final static private String FETCH_STUDY_URL = BASE_URL + "/mobile/fetchstudy";
    final static private String RT_CHECK_CONN_URL = BASE_URL + "/mobile/check/rescuetime";
    final static private String RT_ACTIVITY_URL = BASE_URL + "/rescuetime/realtime";
    final static private String CAL_EVENTS_URL = BASE_URL + "/mobile/calendar/events";
    final static public String GOOGLE_LOGIN_NO_OHMAGE_URL = CallAPI.BASE_URL + "/android_google_no_ohmage";

    private static final String BEEHIVE_NOTIF_LOGS_URL = BASE_URL + "/mobile/add/notif";
    private static final String BEEHIVE_ANALYTICS_URL = BASE_URL + "/mobile/add/analytics";



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

    private static void addRequestToQueue(Context context, String url, final JSONObject params, final VolleyJsonCallback callback) {
        if (!Network.isDeviceOnline(context)) return;
        JsonObjectRequest request = createRequest(url, params, callback);
        SingletonRequest.getInstance(context).addToRequestQueue(request);
    }

    public static void fetchStudy(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, FETCH_STUDY_URL, params, callback);
    }

    public static void connectStudy(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, CONNECT_URL, params, callback);
    }

    public static void checkRTConn(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, RT_CHECK_CONN_URL, params, callback);
    }

    public static void checkCalConn(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, CAL_CHECK_CONN_URL, params, callback);
    }

    public static void getAllCalEvents(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, CAL_EVENTS_URL, params, callback);
    }

    public static void getRTRealtimeActivity(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, RT_ACTIVITY_URL, params, callback);
    }

    public static void submitNotifLogs(Context context, JSONObject params, VolleyJsonCallback callback) {
        addRequestToQueue(context, BEEHIVE_NOTIF_LOGS_URL, params, callback);
    }

    public static void submitAnalytics(Context context, JSONObject params, VolleyJsonCallback callback) {
        addRequestToQueue(context, BEEHIVE_ANALYTICS_URL, params, callback);
    }
}

