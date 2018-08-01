/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smalldata.beehiveapp.fcm;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.smalldata.beehiveapp.notification.NewAlarmHelper;
import io.smalldata.beehiveapp.onboarding.Profile;
import io.smalldata.beehiveapp.utils.AlarmHelper;
import io.smalldata.beehiveapp.utils.ConnectBeehive;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "BeehiveFirebaseMsg";
    private static final String SYNC_STUDY = "syncStudy";
    private static final String FORCE_SERVER_SYNC = "forceServerSync";
    private static final String NOTIFY_USER = "notifyUser";
    private static final String PROMPT_APP_UPDATE = "promptUpdate";
    private Context mContext;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mContext = getApplicationContext();


        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();
            String type = data.get("type");
            if (type == null) return;

            String title = data.get("title");
            String content = data.get("content");
            String url = data.get("url");
            switch (type) {
                case SYNC_STUDY:
                    Profile mProfile = new Profile(mContext);
                    ConnectBeehive mConnectBeehive = new ConnectBeehive(mContext);
                    mConnectBeehive.updateStudyThenApplyAnyInstantSurvey(mContext, mProfile.getStudyCode());
                    break;

                case FORCE_SERVER_SYNC:
                    AppJobService.sendAllSurveyLogs(mContext);
                    ServerPeriodicUpdateReceiver.startRepeatingServerTask(mContext);
                    break;

                case NOTIFY_USER:
                    if (title != null || content != null) {
                        AlarmHelper.showInstantNotif(mContext, title, content, "io.smalldata.beehiveapp", 5003);
                    }
                    break;

                case PROMPT_APP_UPDATE:
                    updateApp(title, content, url);
                    break;
            }
        }
    }

    private void updateApp(String title, String content, String url) {
        if (title != null && content != null && url != null) {
            NewAlarmHelper.notifyUpdateApp(mContext, title, content, url);
        }
    }

}
