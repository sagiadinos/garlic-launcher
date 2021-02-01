/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-launcher source code

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

package com.sagiadinos.garlic.launcher.receiver;

import android.content.IntentFilter;
import com.sagiadinos.garlic.launcher.MainActivity;

public class ReceiverManager
{
    private final MainActivity MyMainActivity;
    private InForegroundReceiver MyPlayerNotInForegroundReceiver = null;
    private PlayerClosedReceiver MyPlayerClosedReceiver = null;
    private SecondAppReceiver MySecondAppReceiver = null;
    private CommandReceiver MyCommandReceiver = null;
    private RebootReceiver MyRebootReceiver = null;
    private InstallAppReceiver MyInstallAppReceiver = null;
    private ConfigXMLReceiver MyConfigXMLReceiver = null;
    private UsbConnectionReceiver MyUsbConnectionReceiver = null;


    public ReceiverManager(MainActivity ma)
    {
        MyMainActivity = ma;
    }

    public void registerAllReceiver()
    {

//      AdminReceiver for Device Owner is initialized in AndroidManifest

        MyPlayerNotInForegroundReceiver =  new InForegroundReceiver();
        MyMainActivity.registerReceiver(
                MyPlayerNotInForegroundReceiver,
                createIntentFilter("InForegroundReceiver")
        );
        MyPlayerNotInForegroundReceiver.setMyActivity(MyMainActivity);

        MyPlayerClosedReceiver =  new PlayerClosedReceiver();
        MyMainActivity.registerReceiver(
                MyPlayerClosedReceiver,
                createIntentFilter("PlayerClosedReceiver")
        );
        MyPlayerClosedReceiver.setMyActivity(MyMainActivity);

        MySecondAppReceiver =  new SecondAppReceiver();
        MyMainActivity.registerReceiver(
                MySecondAppReceiver,
                createIntentFilter("SecondAppReceiver")
        );
        MySecondAppReceiver.setMyActivity(MyMainActivity);

        MyRebootReceiver =  new RebootReceiver();
        MyMainActivity.registerReceiver(
                MyRebootReceiver,
                createIntentFilter("RebootReceiver")
        );

        MyCommandReceiver =  new CommandReceiver();
        MyMainActivity.registerReceiver(
                MyCommandReceiver,
                createIntentFilter("CommandReceiver")
        );

        MyInstallAppReceiver =  new InstallAppReceiver();
        MyMainActivity.registerReceiver(
                MyInstallAppReceiver,
                createIntentFilter("InstallAppReceiver")
        );

        MyConfigXMLReceiver =  new ConfigXMLReceiver();
        MyMainActivity.registerReceiver(
                MyConfigXMLReceiver,
                createIntentFilter("ConfigXMLReceiver")
        );

        MyUsbConnectionReceiver = new UsbConnectionReceiver();
        MyMainActivity.registerReceiver(
                MyUsbConnectionReceiver,
                createIntentFilter("UsbConnectionReceiver")
        );
    }

    public void unregisterAllReceiver()
    {
        if (MyPlayerNotInForegroundReceiver == null)
            return;

        MyMainActivity.unregisterReceiver(MyPlayerNotInForegroundReceiver);
        MyMainActivity.unregisterReceiver(MyPlayerClosedReceiver);
        MyMainActivity.unregisterReceiver(MySecondAppReceiver);
        MyMainActivity.unregisterReceiver(MyCommandReceiver);
        MyMainActivity.unregisterReceiver(MyRebootReceiver);
        MyMainActivity.unregisterReceiver(MyInstallAppReceiver);
        MyMainActivity.unregisterReceiver(MyConfigXMLReceiver);
        MyMainActivity.unregisterReceiver(MyUsbConnectionReceiver);

    }

    private IntentFilter createIntentFilter(String action_name)
    {
        return new IntentFilter("com.sagiadinos.garlic.launcher.receiver." + action_name);
    }
}
