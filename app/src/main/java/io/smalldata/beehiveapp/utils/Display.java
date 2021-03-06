package io.smalldata.beehiveapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

/**
 * Set UI display attributes for all screens
 * Created by fnokeke on 1/23/17.
 */

public class Display {

    private final static int SUCCESS = Color.BLUE;
    private final static int ERROR = Color.RED;
    private final static int PLAIN = Color.GRAY;
    private static ProgressDialog progressDialog;

    public static void showError(TextView tv, int msgRid) {
        tv.setText(msgRid);
        tv.setTextColor(ERROR);
    }

    public static void showBusy(Context context, String busyMsg) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(busyMsg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void dismissBusy() {
        progressDialog.dismiss();
    }

    public static void hide(TextView tv) {
        if (tv == null) return;
        tv.setVisibility(View.GONE);
    }

    public static void showError(TextView tv, String msg) {
        if (tv == null) return;
        tv.setVisibility(View.VISIBLE);
        tv.setText(msg);
        tv.setTextColor(ERROR);
    }

    public static void showSuccess(TextView tv, int msgRid) {
        if (tv == null) return;
        tv.setVisibility(View.VISIBLE);
        tv.setText(msgRid);
        tv.setTextColor(SUCCESS);
    }

    public static void showSuccess(TextView tv, String msg) {
        if (tv == null) return;
        tv.setVisibility(View.VISIBLE);
        tv.setText(msg);
        tv.setTextColor(SUCCESS);
    }

    public static void showPlain(TextView tv, int msgRid) {
        if (tv == null) return;
        tv.setVisibility(View.VISIBLE);
        tv.setText(msgRid);
        tv.setTextColor(PLAIN);
    }

    public static void showPlain(TextView tv, String msg) {
        if (tv == null) return;
        tv.setVisibility(View.VISIBLE);
        tv.setText(msg);
        tv.setTextColor(PLAIN);
    }
    public static void clear(TextView tv) {
        if (tv == null) return;
        tv.setVisibility(View.VISIBLE);
        tv.setText("");
        tv.setTextColor(PLAIN);
    }

}
