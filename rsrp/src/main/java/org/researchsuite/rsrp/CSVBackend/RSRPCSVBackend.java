package org.researchsuite.rsrp.CSVBackend;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.researchsuite.rsrp.Core.RSRPBackEnd;
import org.researchsuite.rsrp.Core.RSRPIntermediateResult;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;

import org.apache.http.client.utils.URIBuilder;
import org.apache.commons.lang3.StringUtils;


public class RSRPCSVBackend implements RSRPBackEnd {
    final String TAG = "RSRPBackend";

    public URI outputDirectory;

    public RSRPCSVBackend(URI outputDirectory){

        this.outputDirectory = outputDirectory;

        Boolean isDirectory = false;

        if(shouldCreateDirectory(outputDirectory)){
            this.createDirectory(outputDirectory);

        }

    }

    public Boolean shouldCreateDirectory(URI outputDirectory){

        File dir = new File(Environment.getExternalStorageDirectory() + outputDirectory.getPath());
        if(dir.exists()){
            if(dir.isDirectory()){
                return false;
            }
            else {
                this.removeDirectory(outputDirectory);
            }
        }

        return true;

    }


    private void createDirectory(URI directory){
        File dir = new File(Environment.getExternalStorageDirectory() + directory.getPath());
        //            dir.createNewFile();
        boolean status = dir.mkdir();
        Log.d(TAG, "createDirectory():  " + status);
    }

    private void removeDirectory(URI directory){
        File dir = new File(Environment.getExternalStorageDirectory() + directory.getPath());
        dir.delete();
    }

    private void removeItem(String itemName){

        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(this.outputDirectory.toString());
            URI uri = uriBuilder.setPath(uriBuilder.getPath() + itemName)
                    .build()
                    .normalize();
            File dir = new File(Environment.getExternalStorageDirectory() + uri.getPath());
            dir.delete();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void removeFileForType(String typeIdentifier){

        String stringToAppend = typeIdentifier + ".csv";

        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(this.outputDirectory.toString());
            URI uri = uriBuilder.setPath(uriBuilder.getPath() + stringToAppend)
                    .build()
                    .normalize();
            File dir = new File(Environment.getExternalStorageDirectory() + uri.getPath());
            dir.delete();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void removeAll() {

        this.removeDirectory(this.outputDirectory);
        this.createDirectory(this.outputDirectory);

    }

    public void destroy() {
        this.removeDirectory(this.outputDirectory);
    }

    private void addFile(String itemName, String text) {

        URIBuilder uriBuilder = null;
        String stringToAppend = itemName + ".csv";
        try {
            uriBuilder = new URIBuilder(this.outputDirectory.toString());
            URI fileURL = uriBuilder.setPath(uriBuilder.getPath() + stringToAppend)
                    .build()
                    .normalize();

            File dataFile = new File (fileURL);
            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(dataFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            try {
                myOutWriter.append(text);
                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e){
            e.printStackTrace();
        }

        //TODO: remember to set android permission for storage: android.permission.WRITE_EXTERNAL_STORAGE

    }

    public URL [] getFileURLs() {

        return null;
    }

    public URL getFileURLForType(String typeIdentifier){

        URIBuilder uriBuilder = null;
        try {
            uriBuilder = new URIBuilder(this.outputDirectory.toString());
            URI uri = uriBuilder.setPath(uriBuilder.getPath() + typeIdentifier + ".csv")
                    .build()
                    .normalize();
            File fileUrl = new File(Environment.getExternalStorageDirectory() + uri.getPath());
            if (fileUrl.exists()){
                try {
                    return fileUrl.toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else {
                return null;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;


    }

    private File getOrCreateFileForType(String typeIdentifier, String header){

        URIBuilder uriBuilder = null;
        String stringToAppend = typeIdentifier + ".csv";
        try {
            uriBuilder = new URIBuilder(this.outputDirectory.toString());
            URI uri = uriBuilder.setPath(uriBuilder.getPath() + stringToAppend)
                    .build()
                    .normalize();
            File fileUrl = new File(Environment.getExternalStorageDirectory() + uri.getPath());

            if(fileUrl.exists()){
                return fileUrl;
            }
            else {
                try {
                    fileUrl.createNewFile();
                    FileOutputStream fOut = null;
                    try {
                        fOut = new FileOutputStream(fileUrl);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    try {
                        myOutWriter.append(header);
                        myOutWriter.append("\n");
                        myOutWriter.close();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return fileUrl;
            }

        } catch (URISyntaxException e){

        }

        return null;
    }

    public void add(CSVEncodable[] csvRecords){

        CSVEncodable first = csvRecords[0];


        //TODO: complete this

    }

    public void add(CSVEncodable encodable){

        String [] records = encodable.toRecords();
        if (records.length == 0){
            return;
        }

        this.add(encodable.getTypeString(),encodable.getHeader(),records,true);

    }

    public Boolean add(String typeString, String header, String[] records, Boolean shouldAddToQueue){

        File fileHandle = this.getOrCreateFileForType(typeString,header);
        String joinedRecords = StringUtils.join(records);

        if(fileHandle != null) {

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(fileHandle,true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            try {
                myOutWriter.append(joinedRecords);
                myOutWriter.append("\n");
                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else if (shouldAddToQueue){
            // TODO: ADD TO TEMPQUEUE
            return true;
        }
        else {
            return false;
        }
    }

    private void syncElements() {

    }

    @Override
    public void add(Context context, RSRPIntermediateResult intermediateResult) {

        CSVEncodable datapoint = (CSVEncodable) intermediateResult;
        if (datapoint != null){
            this.add(datapoint);
        }

    }
}
