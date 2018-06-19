package io.smalldata.beehiveapp.fcm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;

import io.smalldata.beehiveapp.onboarding.Constants;
import io.smalldata.beehiveapp.utils.AlarmHelper;

/**
 * Operations for locally storing data in files
 * that will later be transferred to a server endpoint
 * Created by fnokeke on 5/29/17.
 */

public class LocalStorage {
    private static final String TAG = "LocalStorage";

    public static void initAllStorageFiles(Context context) {
        createBeehiveDirectory();
        LocalStorage.resetFile(context, Constants.PAM_LOGS_CSV);
        LocalStorage.resetFile(context, Constants.SURVEY_LOGS_CSV);
        LocalStorage.resetFile(context, Constants.NOTIF_LOGS_CSV);
        LocalStorage.resetFile(context, Constants.ANALYTICS_LOG_CSV);
    }

    private static void createBeehiveDirectory() {
        String beehiveDir = Environment.getExternalStorageDirectory().getPath() + "/beehive";
        File dir = new File(beehiveDir);
        Log.i(TAG, "Beehive directory exists:" + dir.exists());
    }

    public static void appendToFile(Context context, String filename, String data) {
        try {
            FileOutputStream fileOutputStream;
            if (filename.contains("/")) { // in external storage
                File file = new File(filename);
                fileOutputStream = new FileOutputStream(file);
            } else {
                fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "appendToFile: error" + e.toString());
            AlarmHelper.showInstantNotif(context, "appendToFile error", e.toString(), "", 5003);
        }
    }

    static String readFromFile(Context context, String filename) {
        makeSureFileExists(filename);
        String result = "";
        InputStream inputStream;
        try {
            if (filename.contains("/")) { // in external storage
                FileInputStream fis = new FileInputStream(filename);
                inputStream = new DataInputStream(fis);
            } else {
                inputStream = context.openFileInput(filename);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString).append("\n");
            }

            inputStream.close();
            result = stringBuilder.toString();

        } catch (FileNotFoundException e) { // FIXME: 6/1/18 do something with file not found exception
            Log.e(TAG, "File not found: " + e.toString());
            AlarmHelper.showInstantNotif(context, "File not found error", e.toString(), "", 5113);
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
            AlarmHelper.showInstantNotif(context, "Cannot read file", e.toString(), "", 5223);
        }

        return result;
    }

    private static void makeSureFileExists(String filename) {
        File file = new File(filename);
        try {
            if (!file.exists()) {
                boolean status = file.createNewFile();
                Log.i(TAG, "Created new file: " + filename + " / " + status);
            } else {
                Log.i(TAG, "Already exists: " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void resetFile(Context context, String filename) {
        try {
            FileOutputStream fileOutputStream;
            String headerBackup = "";

            // delete everything except header
            if (filename.equals(Constants.PAM_LOGS_CSV)) {
                headerBackup = "timestamp,affect_arousal,affect_valence,positive_affect,mood,negative_affect\n";
            } else if (filename.equals(Constants.SURVEY_LOGS_CSV)) { // in external storage
                headerBackup = "timestamp, q1-mindful-engage\n";
//                headerBackup = readFromFile(context, filename);
//                String[] rows = headerBackup.split("\n");
//                if (rows.length > 0) {
//                    headerBackup = rows[0] + "\n";
//                }
            }

            if (filename.contains("/")) { // in external storage
                File file = new File(filename);
                fileOutputStream = new FileOutputStream(file);
            } else {
                fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(headerBackup);
            outputStreamWriter.close();
//            new OutputStreamWriter(fileOutputStream).close();
        } catch (IOException e) {
            Log.e(TAG, "resetFile: error" + e.toString());
            AlarmHelper.showInstantNotif(context, "resetFile error", e.toString(), "", 5333);
        }
    }
}
