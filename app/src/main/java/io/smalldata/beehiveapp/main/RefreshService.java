package io.smalldata.beehiveapp.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import io.smalldata.beehiveapp.config.GoogleCalendar;
import io.smalldata.beehiveapp.config.Intervention;
import io.smalldata.beehiveapp.config.Rescuetime;
import io.smalldata.beehiveapp.utils.Store;

import static io.smalldata.beehiveapp.utils.Helper.getTimestampInMillis;

public class RefreshService extends Service {

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
//        Helper.showInstantNotif(this, "Refresh Performed at ", Helper.millisToDateFormat(), "", 1112);
//        JSONObject params = Experiment.getUserInfo(mContext);
//        CallAPI.connectStudy(mContext, params, connectStudyResponseHandler);
        updateContents();
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateContents() {
        if (Store.getBoolean(mContext, Store.IS_RESCUETIME_ENABLED)) {
            rescueTime.refreshAndStoreStats();
        }

        if (Store.getBoolean(mContext, Store.IS_CALENDAR_ENABLED)) {
            googleCalendar.refreshAndStoreStats();
        }

        Intervention.prepareTodayIntervention(mContext);
    }

//    public static void start(Context context) {
//        context.startService(new Intent(context, RefreshService.class));
//    }

//    VolleyJsonCallback connectStudyResponseHandler = new VolleyJsonCallback() {
//        @Override
//        public void onConnectSuccess(JSONObject result) {
//            Log.i("AlarmRefreshSuccess: ", result.toString());
//
//            Experiment experiment = new Experiment(mContext);
//            JSONObject experimentInfo = result.optJSONObject("experiment");
//            experiment.saveConfigs(experimentInfo);
//
//            JSONObject user = result.optJSONObject("user");
//            experiment.saveUserInfo(user);
//
//            if (Store.getBoolean(mContext, Store.IS_RESCUETIME_ENABLED)) {
//                rescueTime.refreshAndStoreStats();
//            }
//
//            if (Store.getBoolean(mContext, Store.IS_CALENDAR_ENABLED)) {
//                googleCalendar.refreshAndStoreStats();
//            }
//        }
//
//        @Override
//        public void onConnectFailure(VolleyError error) {
//            Log.e("AlarmRefreshFailure: ", error.toString());
//            error.printStackTrace();
//        }
//    };

    public static void startRefreshInIntervals(Context context) {
        Intent refreshIntent = new Intent(context, RefreshService.class);
        PendingIntent pendingRefreshIntent = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTimestampInMillis(), 6*AlarmManager.INTERVAL_HOUR, pendingRefreshIntent);
    }

}
