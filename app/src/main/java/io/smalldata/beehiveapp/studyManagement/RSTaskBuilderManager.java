package io.smalldata.beehiveapp.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;

import org.researchstack.backbone.ResourcePathManager;

import edu.cornell.tech.foundry.researchsuitetaskbuilder.RSTBStateHelper;
import edu.cornell.tech.foundry.researchsuitetaskbuilder.RSTBTaskBuilder;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSTaskBuilderManager {

    private static RSTBTaskBuilder builder;

    /**
     * @return singleton instance
     */
    @NonNull
    public static RSTBTaskBuilder getBuilder() {
        checkState(builder != null, "CTFResultsProcessorManager has not been initialized. ");

        return builder;
    }

    public static void init(
            Context context,
            ResourcePathManager resourcePathManager,
            RSTBStateHelper stateHelper
    ) {
        RSTBTaskBuilder builder = new RSTBTaskBuilder(
                context,
                resourcePathManager,
                stateHelper);

        builder.getStepBuilderHelper().setDefaultResourceType(ResourcePathManager.Resource.TYPE_JSON);

        RSTaskBuilderManager.builder = builder;
    }

}