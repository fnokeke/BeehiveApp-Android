package io.smalldata.beehiveapp.studyManagement;

import android.support.annotation.NonNull;

import org.researchsuite.rsrp.CSVBackend.RSRPCSVBackend;
import org.researchsuite.rsrp.Core.RSRPBackEnd;
import org.researchsuite.rsrp.Core.RSRPResultsProcessor;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSResultsProcessorManager {

    private static RSRPResultsProcessor resultsProcessor;
    private static RSRPCSVBackend backend;

    /**
     * @return singleton instance
     */
    @NonNull
    public static RSRPResultsProcessor getResultsProcessor() {
        checkState(resultsProcessor != null, "CTFResultsProcessorManager has not been initialized. ");
        return resultsProcessor;
    }

    public static void init(RSRPBackEnd backEnd) {
        RSRPResultsProcessor builder = new RSRPResultsProcessor(backEnd);
        RSResultsProcessorManager.resultsProcessor = builder;
        RSResultsProcessorManager.backend = (RSRPCSVBackend) backEnd;
    }

    public static RSRPCSVBackend getBackend(){
        return backend;
    }

}
