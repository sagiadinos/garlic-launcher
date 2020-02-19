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

import android.content.IntentFilter;
import com.sagiadinos.garlic.launcher.MainActivity;

public class ReceiverManager
{
    private static InForegroundReceiver MyPlayerNotInForegroundReceiver = null;
    private static PlayerClosedReceiver MyPlayerClosedReceiver = null;
    private static SecondAppReceiver MySecondAppReceiver = null;
    private static RebootReceiver MyRebootReceiver = null;
    private static InstallAppReceiver MyInstallAppReceiver = null;

    // private static MainActivity Activity; see below
    public static void registerAllReceiver(MainActivity MainActivity)
    {

//      AdminReceiver has default constructor and is initialized in AndroidManifest

        MyPlayerNotInForegroundReceiver =  new InForegroundReceiver();
        MainActivity.registerReceiver(
                MyPlayerNotInForegroundReceiver,
                createIntentFilter("InForegroundReceiver")
        );
        MyPlayerNotInForegroundReceiver.setMyActivity(MainActivity);

        MyPlayerClosedReceiver =  new PlayerClosedReceiver();
        MainActivity.registerReceiver(
                MyPlayerClosedReceiver,
                createIntentFilter("PlayerClosedReceiver")
        );
        MyPlayerClosedReceiver.setMyActivity(MainActivity);

        MySecondAppReceiver =  new SecondAppReceiver();
        MainActivity.registerReceiver(
                MySecondAppReceiver,
                createIntentFilter("SecondAppReceiver")
        );
        MySecondAppReceiver.setMyActivity(MainActivity);

        MyRebootReceiver =  new RebootReceiver();
        MainActivity.registerReceiver(
                MyRebootReceiver,
                createIntentFilter("RebootReceiver")
        );
        MyRebootReceiver.setMyActivity(MainActivity);

        MyInstallAppReceiver =  new InstallAppReceiver();
        MainActivity.registerReceiver(
                MyInstallAppReceiver,
                createIntentFilter("InstallAppReceiver")
        );
        MyInstallAppReceiver.setMyActivity(MainActivity);
    }

    public static void unregisterAllReceiver(MainActivity MainActivity)
    {

        MainActivity.unregisterReceiver(MyPlayerNotInForegroundReceiver);
        MainActivity.unregisterReceiver(MyPlayerClosedReceiver);
        MainActivity.unregisterReceiver(MySecondAppReceiver);
        MainActivity.unregisterReceiver(MyRebootReceiver);
        MainActivity.unregisterReceiver(MyInstallAppReceiver);

    }


    private static IntentFilter createIntentFilter(String action_name)
    {
        return new IntentFilter("com.sagiadinos.garlic.launcher.receiver." + action_name);
    }
}
