package com.sagiadinos.garlic.launcher.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network
{

    public static boolean isConnected(Context ctx)
    {
        ConnectivityManager conn_manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conn_manager == null)
        {
            return false;
        }

        NetworkInfo net_info = conn_manager.getActiveNetworkInfo();

        if (net_info == null)
        {
            return false;
        }

        return net_info.isConnected();
    }

}
