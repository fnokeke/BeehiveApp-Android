package io.smalldata.beehiveapp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.smalldata.beehiveapp.R;
import io.smalldata.beehiveapp.RefreshService;
import io.smalldata.beehiveapp.utils.Display;
import io.smalldata.beehiveapp.utils.Store;

/**
 * Fabian Okeke
 * HomeFragment: displays dashboard for any ongoing intervention
 * Created 1/20/17
 */

public class HomeFragment extends Fragment {

    private Context mContext;
    private Activity mActivity;
    private TextView needToConnectTV;
    private TextView rescuetimeTV;
    private TextView calendarTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mContext = getActivity();

        mActivity.setTitle("Home");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        needToConnectTV = (TextView) mActivity.findViewById(R.id.tv_need_to_connect);
        rescuetimeTV = (TextView) mActivity.findViewById(R.id.tv_rescuetime);
        calendarTV = (TextView) mActivity.findViewById(R.id.tv_calendar);

        Display.showSuccess(rescuetimeTV, Store.getString(mContext, "statsRT"));
        Display.showSuccess(calendarTV, Store.getString(mContext, "statsCal"));
        checkPromptToConnect();

        RefreshService.start(mContext);
    }

    private void checkPromptToConnect() {

        String email = Store.getString(mContext, "email");
        if (email.equals("")) {
            needToConnectTV.setVisibility(View.VISIBLE);
            rescuetimeTV.setVisibility(View.GONE);
            calendarTV.setVisibility(View.GONE);
        } else {
            needToConnectTV.setVisibility(View.GONE);
            rescuetimeTV.setVisibility(View.VISIBLE);
            calendarTV.setVisibility(View.VISIBLE);
        }
    } 


//    private void scheduleNotification(Context cxt, Notification notification, int delay) {
//        Intent notificationIntent = new Intent(cxt, NotificationPublisher.class);
//        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
//        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(cxt, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        long futureInMillis = SystemClock.elapsedRealtime() + delay;
//        AlarmManager alarmManager = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
//    }

//    private Notification getNotification(Context cxt, String title, String content) {
//        Notification.Builder builder = new Notification.Builder(cxt);
//
//        Intent resultIntent = new Intent(cxt, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(cxt);
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(resultPendingIntent);
//
//        builder.setContentTitle(title)
//                .setContentText(content)
//                .setShowWhen(true)
//                .addAction(android.R.drawable.ic_input_add, "Ok, do now.", resultPendingIntent) // #0
//                .addAction(android.R.drawable.ic_input_delete, "Do later.", resultPendingIntent)  // #1
//                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Remove!", resultPendingIntent) // #2
//                .setSmallIcon(android.R.drawable.ic_menu_recent_history);
//
//        return builder.build();
//    }

}

