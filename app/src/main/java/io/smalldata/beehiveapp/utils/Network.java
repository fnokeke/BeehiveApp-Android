package io.smalldata.beehiveapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 * Created by fnokeke on 1/23/17.
 */

public class Network {

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    public static boolean isDeviceOnline(Context cxt) {
        ConnectivityManager connMgr = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
