package io.smalldata.beehiveapp.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.DateHelper;

/**
 * Created by jameskizer on 4/21/17.
 */

public class RSActivityManager {

    private static final String TAG = "ActivityManager";

    private static RSActivityManager sActivityManager;

    private Boolean completedOnboarding = false;

    @NonNull
    public static RSActivityManager get() {
        if (sActivityManager == null) {
            sActivityManager = new RSActivityManager();
        }
        return sActivityManager;
    }


    private Random rand;
    private ConcurrentLinkedQueue<CTFActivityRun> activityQueue;

    private Object delegateLock = new Object();
    private RSActivityManagerDelegate delegate = null;

    public RSActivityManager() {

        this.rand = new Random();
        this.activityQueue = new ConcurrentLinkedQueue<>();
    }

    public void setDelegate(Context context, RSActivityManagerDelegate delegate) {
        synchronized (this.delegateLock) {
            this.delegate = delegate;
        }
        this.tryToLaunchActivity(context);

    }

    public void clearDelegate(RSActivityManagerDelegate delegate) {
        synchronized (this.delegateLock) {
            if (this.delegate.equals(delegate)) {
                this.delegate = null;
            }
        }
    }

    @Nullable
    private CTFScheduleItem getScheduleItem(Context context, String filename) {
        ResourcePathManager.Resource resource = new ResourcePathManager.Resource(ResourcePathManager.Resource.TYPE_JSON,
                RSResourcePathManager.BASE_PATH_JSON,
                filename,
                CTFScheduleItem.class);

        return resource.create(context);
    }

    public void readyToLaunchActivity(Context context) {
        this.tryToLaunchActivity(context);
    }

    private void tryToLaunchActivity(Context context) {

        RSActivityManagerDelegate delegate;
        synchronized (this.delegateLock) {
            if (this.delegate == null) {
                return;
            }
            delegate = this.delegate;
        }


        if (!this.activityQueue.isEmpty()) {

            CTFActivityRun activityRun = this.activityQueue.peek();

            boolean launched = delegate.tryToLaunchRSActivity(this, activityRun);
            if (launched) {
                this.activityQueue.poll();
//                AlarmHelper.showInstantNotif(context, "RSTask Launched!", DateHelper.getFormattedTimestamp(), "", 2022);
//            } else {
//                AlarmHelper.showInstantNotif(context, "RSTask Failed!", DateHelper.getFormattedTimestamp(), "", 2023);
            }

            Task task = loadTask(context, activityRun);

            if (task == null) {
                Toast.makeText(context,
                        org.researchstack.skin.R.string.rss_local_error_load_task,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void queueActivity(Context context, String filename, boolean tryToLaunch) {

        CTFScheduleItem scheduleItem = this.getScheduleItem(context, filename);
        CTFActivityRun activityRun = activityRunForItem(scheduleItem);

        this.activityQueue.add(activityRun);
        this.tryToLaunchActivity(context);
    }

    public CTFActivityRun activityRunForItem(CTFScheduleItem item) {

        CTFActivityRun activityRun = new CTFActivityRun(
                item.identifier,
                UUID.randomUUID(),
                0,
                item.activity,
                item.resultTransforms
        );

        return activityRun;
    }

    @Nullable
    public Task loadTask(Context context, CTFActivityRun activityRun) {

        List<Step> stepList = null;
        try {
            stepList = RSTaskBuilderManager.getBuilder().stepsForElement(activityRun.activity);
        }
        catch(Exception e) {
            Log.w(this.TAG, "could not create steps from task json", e);
            return null;
        }
        if (stepList != null && stepList.size() > 0) {
            return new OrderedTask(activityRun.identifier, stepList);
        }
        else {
            return null;
        }
    }

    public void completeActivity(Context context, TaskResult taskResult, CTFActivityRun activityRun) {

        assert(activityRun != null);



        RSResultsProcessorManager.getResultsProcessor().processResult(
                context,
                taskResult,
                activityRun.taskRunUUID,
                activityRun.resultTransforms
        );

        // TODO: do below for when user specifies delete
//
//        if(Objects.equals(activityRun.identifier, "yadl_full")){
//            RSRPCSVBackend backend = RSResultsProcessorManager.getBackend();
//            backend.removeFileForType("yadl_full");
//
//        }


            this.tryToLaunchActivity(context);

    }



}
