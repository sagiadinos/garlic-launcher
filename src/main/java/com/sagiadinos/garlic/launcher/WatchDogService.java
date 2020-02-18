/*************************************************************************************
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
 *************************************************************************************/

package com.sagiadinos.garlic.launcher;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

/**
 * This service will called every 3s to check if launcher is in background
 */
public class WatchDogService extends Service
{
    public Handler handler = null;
    public static Runnable runnable = null;
    public GarlicLauncherApplication MyApplication;

    @Override
    public void onCreate()
    {
        handler = new Handler();
        MyApplication = (GarlicLauncherApplication) getApplication();

        runnable = new Runnable()
        {
            public void run()
            {
                handler.postDelayed(runnable, 3000);

                // if the Launcher is in Foreground something could be wrong, so we need
                // to examine in InForegroundReceiver
                if (MyApplication.isOnForeground())
                {
                    Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.InForegroundReceiver");
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intent);
                }
            }

        };

        handler.postDelayed(runnable, 6000);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        return START_STICKY;
    }

}
