package io.smalldata.beehiveapp.main;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import io.smalldata.beehiveapp.api.CallAPI;
import io.smalldata.beehiveapp.api.VolleyJsonCallback;
import io.smalldata.beehiveapp.config.GoogleCalendar;
import io.smalldata.beehiveapp.config.Rescuetime;
import io.smalldata.beehiveapp.utils.Store;

public class RefreshService extends Service {

    private Handler serverHandler = new Handler();
    private Rescuetime rescueTime;
    private GoogleCalendar googleCalendar;
    Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rescueTime = new Rescuetime(this);
        googleCalendar = new GoogleCalendar(this);
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverHandler.postDelayed(serverUpdateTask, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, RefreshService.class));
    }

    private Runnable serverUpdateTask = new Runnable() {
        public void run() {
            if (Store.getBoolean(mContext, Store.RESCUETIME_FEATURE)) {
                rescueTime.refreshAndStoreStats();
            }

            if (Store.getBoolean(mContext, Store.CALENDAR_FEATURE)) {
                googleCalendar.refreshAndStoreStats();
            }

            serverHandler.postDelayed(this, 15 * 60 * 1000);

            Log.i("AlarmRefresh", "Now refreshing content.");
            JSONObject params = Experiment.getUserInfo(mContext);
            CallAPI.connectStudy(mContext, params, connectStudyResponseHandler);
        }
    };

    VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
        @Override
        public void onConnectSuccess(JSONObject result) {
            Store.reset(mContext);
            Log.e("AlarmRefreshSuccess: ", result.toString());

            Experiment experiment = new Experiment(mContext);
            JSONObject experimentInfo = result.optJSONObject("experiment");
            experiment.saveToggles(experimentInfo);
            experiment.saveConfigs(experimentInfo);

            JSONObject user = result.optJSONObject("user");
            experiment.saveUserInfo(user);
        }

        @Override
        public void onConnectFailure(VolleyError error) {
            Log.e("AlarmRefreshFailure: ", error.toString());
            error.printStackTrace();
        }
    };

}
