package io.smalldata.beehiveapp.studyManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.PinCodeActivity;
import org.researchstack.backbone.ui.ViewTaskActivity;

import io.smalldata.beehiveapp.onboarding.Constants;

/**
 * Created by jameskizer on 4/21/17.
 */

public class RSActivity extends PinCodeActivity implements RSActivityManagerDelegate {

    private boolean dataReady = false;

    private static int REQUEST_RS_ACTIVITY = 0xff30;

    private Object runningActivityLock = new Object();
    private CTFActivityRun runningActivity;
    private static String RUNNING_ACTIVITY_KEY = "RUNNING_ACTIVITY_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public void onDataReady() {
        super.onDataReady();
        this.dataReady = true;
        RSActivityManager.get().readyToLaunchActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        RSActivityManager.get().setDelegate(this, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.dataReady = false;
    }

    @Override
    public boolean tryToLaunchRSActivity(RSActivityManager activityManager, CTFActivityRun activityRun) {

        if (!this.dataReady) {
            return false;
        }

        synchronized (this.runningActivityLock) {
            //check to see if there is already a running activity
            if (this.runningActivity == null) {

                Task task = activityManager.loadTask(this, activityRun);

                if (task == null) {
                    Toast.makeText(this,
                            org.researchstack.skin.R.string.rss_local_error_load_task,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent intent = ViewTaskActivity.newIntent(this, task);

                this.runningActivity = activityRun;
                startActivityForResult(intent, REQUEST_RS_ACTIVITY);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_RS_ACTIVITY) {
            CTFActivityRun activityRun = this.runningActivity;
            synchronized (this.runningActivityLock) {
                this.runningActivity = null;
            }

            if (resultCode == Activity.RESULT_OK) {
                assert (activityRun != null);
                TaskResult taskResult = (TaskResult) data.getSerializableExtra(ViewTaskActivity.EXTRA_TASK_RESULT);
                RSActivityManager.get().completeActivity(this, taskResult, activityRun);
                finish();
                Toast.makeText(this, "Response recorded.", Toast.LENGTH_SHORT).show();

            } else {

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
