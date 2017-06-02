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

    final static private String BASE_URL = "https://slm.smalldata.io";
//    final static public String BASE_URL = "http://10.0.0.166:5000";
//    final static private String BASE_URL = "http://10.144.2.146:5000";
    final static private String CONNECT_URL = BASE_URL + "/mobile/connect/study";
    final static private String CAL_CHECK_CONN_URL = BASE_URL + "/mobile/check/calendar";
    final static private String RT_CHECK_CONN_URL = BASE_URL + "/mobile/check/rescuetime";
    final static private String RT_ACTIVITY_URL = BASE_URL + "/rescuetime/realtime";
    final static private String NOTIF_CLICKED_STATS = BASE_URL + "/mobile/add/notif-clicked-stats";
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

    private static void addRequestToQueue(Context context, String url, final JSONObject params, final VolleyJsonCallback callback) {
        if (!Network.isDeviceOnline(context)) return;
        JsonObjectRequest request = createRequest(url, params, callback);
        SingletonRequest.getInstance(context).addToRequestQueue(request);
    }

    public static void connectStudy(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, CONNECT_URL, params, callback);
    }

    public static void addNotifClickedStats(final Context context, final JSONObject params, final VolleyJsonCallback callback) {
        addRequestToQueue(context, NOTIF_CLICKED_STATS, params, callback);
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

}

