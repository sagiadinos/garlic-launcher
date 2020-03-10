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

package com.sagiadinos.garlic.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.MainActivity;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.Installer;

/**
 * This Receiver will be called from the player background service (Watchdog)
 *
 * It decides if the player should start again or wait until a probably second app finished.
 */
public class InForegroundReceiver extends BroadcastReceiver
{
    MainActivity MyActivity;

    public InForegroundReceiver()
    {
    }

    public void setMyActivity(MainActivity act)
    {
        MyActivity = act;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        if (!intent.getAction().equals("com.sagiadinos.garlic.launcher.receiver.InForegroundReceiver") ||
            !context.getPackageName().equals(MyActivity.getPackageName()))
        {
            return;
        }

        // When we found here it means a Launcher activity is in the foreground
        // This can happen consciously or  due to a crahs app crash so we had to examine
        //
        // Opportunities
        // 1. player started or second app started booleans are true
        // that could be mean the player is crashed or second app is finished/crashed
        // => in this case start player!
        //
        // 2. if player start and second app flags booleans are both false
        // that means the player was closed consciously and the user wants to stay
        // in a launcher activity (waiting or for configuration)
        // => in this case do nothing!
        if (MyActivity.hasPlayerStarted() || MyActivity.hasSecondAppStarted())
        {
            MyActivity.startGarlicPlayer(null);
        }

    }

 }
