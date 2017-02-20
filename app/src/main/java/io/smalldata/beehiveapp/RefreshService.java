package io.smalldata.beehiveapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import io.smalldata.beehiveapp.properties.GoogleCalendar;
import io.smalldata.beehiveapp.properties.Rescuetime;

public class RefreshService extends Service {

    private Handler serverHandler = new Handler();
    private Rescuetime rescueTime;
    private GoogleCalendar googleCalendar;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rescueTime = new Rescuetime(this);
        googleCalendar = new GoogleCalendar(this);
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
            rescueTime.refreshAndStoreStats();
            googleCalendar.refreshAndStoreStats();
            serverHandler.postDelayed(this, 30 * 60 * 1000);
        }
    };

}
