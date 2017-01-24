package io.smalldata.beehiveapp.utils;

import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by fnokeke on 1/23/17.
 */

public class Display {

    private final static int SUCCESS = Color.BLUE;
    private final static int ERROR = Color.RED;
    private final static int PLAIN = Color.BLACK;

    public static void showError(TextView tv, int msgRid) {
        tv.setText(msgRid);
        tv.setTextColor(ERROR);
    }

    public static void showSuccess(TextView tv, int msgRid) {
        tv.setText(msgRid);
        tv.setTextColor(SUCCESS);
    }

    public static void showSuccess(TextView tv, String msg) {
        tv.setText(msg);
        tv.setTextColor(SUCCESS);
    }

    public static void showPlain(TextView tv, int msgRid) {
        tv.setText(msgRid);
        tv.setTextColor(PLAIN);
    }

    public static void clear(TextView tv) {
        tv.setText("");
        tv.setTextColor(PLAIN);
    }
}
