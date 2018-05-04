package org.researchsuite.rsrp.Core;

import android.content.Context;
import android.util.Log;

import org.researchstack.backbone.result.TaskResult;

import java.util.List;
import java.util.UUID;

import org.researchsuite.rsrp.Core.RSRPFrontEndServiceProvider.RSRPFrontEndService;

/**
 * Created by jameskizer on 2/3/17.
 */
public class RSRPResultsProcessor {

    private static final String TAG = "RSRPResultsProcessor";
    private Context context;
    private RSRPBackEnd backEnd;

    public RSRPResultsProcessor(RSRPBackEnd backEnd) {
        this.backEnd = backEnd;
    }

    public void processResult(Context context, TaskResult taskResult, UUID taskRunUUID, List<RSRPResultTransform> resultTransforms) {
        List<RSRPIntermediateResult> intermediateResults = RSRPFrontEndService.getInstance().processResult(taskResult, taskRunUUID, resultTransforms);
        for (RSRPIntermediateResult intermediateResult : intermediateResults) {
            this.backEnd.add(context, intermediateResult);
        }

        Log.d(TAG, "Processed results");

    }

}
