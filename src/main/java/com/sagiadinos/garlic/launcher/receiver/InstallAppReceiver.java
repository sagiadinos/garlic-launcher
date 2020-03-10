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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.sagiadinos.garlic.launcher.MainActivity;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.Installer;


/**
 * This receiver is responsible for signal to install
 * a software.
 *
 * ToDo: We need to implement some security here
 * so that only registered software pakages could be installed.
 *
 */
public class InstallAppReceiver extends BroadcastReceiver
{

    public InstallAppReceiver()
    {
    }

    @Override
    public void onReceive(Context ctx, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        if (!intent.getAction().equals("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver") ||
                !ctx.getPackageName().equals(DeviceOwner.GARLIC_LAUNCHER_PACKAGE_NAME))
        {
            return;
        }
        try
        {
            Installer MyInstaller = new Installer(ctx);
            String file_path = intent.getStringExtra("apk_path");
            MyInstaller.installPackage(file_path);

            // delete downloaded files which are in player cache but not on usb
            if (file_path != null && (file_path.contains("cache") || file_path.contains("Download")))
            {
                File file = new File(file_path);
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }

         //   DeviceOwner.reboot(ctx);
        }
        catch (IOException e)
        {
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}
