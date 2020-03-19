/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-player source code

 This program is free software: you can redistribute it and/or  modify
 it under the terms of the GNU Affero General Public License, version 3,
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
