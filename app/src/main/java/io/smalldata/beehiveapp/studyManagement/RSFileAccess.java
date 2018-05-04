package io.smalldata.beehiveapp.studyManagement;

/**
 * Created by Christina on 8/11/17.
 */

import android.content.Context;
import android.support.annotation.Nullable;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.utils.FormatHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cornell.tech.foundry.ohmageomhsdk.OhmageOMHSDKCredentialStore;
import edu.cornell.tech.foundry.researchsuitetaskbuilder.RSTBStateHelper;

/**
 * Created by jameskizer on 4/12/17.
 */

public class RSFileAccess extends SimpleFileAccess implements RSTBStateHelper, OhmageOMHSDKCredentialStore {

    private String pathName;

    private static String SIGNED_IN_DATE = "SIGNED_IN_DATE";

    public static RSFileAccess getInstance()
    {
        return (RSFileAccess) StorageAccess.getInstance().getFileAccess();
    }

    public RSFileAccess(String pathName)
    {
        this.pathName = pathName;
    }

    private String pathForRSTBKey(String key) {
        StringBuilder pathBuilder = new StringBuilder(this.pathName);
        pathBuilder.append("/rstb/");
        pathBuilder.append(key);

        return pathBuilder.toString();
    }

    private String pathForOSDKKey(String key) {
        StringBuilder pathBuilder = new StringBuilder(this.pathName);
        pathBuilder.append("/osdk/");
        pathBuilder.append(key);

        return pathBuilder.toString();
    }

    @Override
    public byte[] valueInState(Context context, String key) {

        if (this.dataExists(context, this.pathForRSTBKey(key))) {
            return this.readData(context, this.pathForRSTBKey(key));
        }
        else {
            return null;
        }
    }


    @Override
    public void setValueInState(Context context, String key, byte[] value) {
        this.writeData(
                context, this.pathForRSTBKey(key),
                value
        );
    }

    @Override
    public byte[] get(Context context, String key) {
        if (this.dataExists(context, this.pathForOSDKKey(key))) {
            return this.readData(context, this.pathForOSDKKey(key));
        }
        else {
            return null;
        }
    }

    @Override
    public void set(Context context, String key, byte[] value) {
        this.writeData(
                context, this.pathForOSDKKey(key),
                value
        );
    }

    @Override
    public void remove(Context context, String key) {
        if (this.dataExists(context, this.pathForOSDKKey(key))) {
            this.clearData(context, this.pathForOSDKKey(key));
        }
    }



    private void setDateInState(Context context, String key, Date date) {
        DateFormat isoFormatter = FormatHelper.DEFAULT_FORMAT;
        String dateString = isoFormatter.format(date);

        this.setValueInState(context, key, dateString.getBytes());
    }



    @Nullable
    private Date getDateInState(Context context, String key) {
        byte[] bytes = this.valueInState(context, key);
        if (bytes == null) {
            return null;
        }

        String dateString = new String(bytes);

        try {
            return FormatHelper.DEFAULT_FORMAT.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSignedInDate(Context context, Date date) {
        this.setDateInState(context, SIGNED_IN_DATE, date);
    }

    public String getParticipantSinceDate(Context context) {
        Date participantSinceDate = this.getDateInState(context, SIGNED_IN_DATE);
        if (participantSinceDate != null) {
            return new SimpleDateFormat("MMM d, yyyy").format(participantSinceDate);
        }
        else return "";
    }

    public void clearFileAccess(Context context) {
        File rootPath = new File(context.getFilesDir() + this.pathName);
        deleteRecursive(rootPath);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}