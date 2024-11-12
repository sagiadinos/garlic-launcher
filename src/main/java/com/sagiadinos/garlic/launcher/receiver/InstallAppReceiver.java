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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.Installer;
import com.sagiadinos.garlic.launcher.helper.ShellExecute;
import com.sagiadinos.garlic.launcher.helper.TaskExecutionReport;


/**
 * This receiver is responsible for signal to install
 * a software.
 *
 * ToDo: We need to implement some security here
 * so that only registered software packages could be installed.
 *
 */
public class InstallAppReceiver extends BroadcastReceiver
{
    MainConfiguration MyMainConfiguration;
    @Override
    public void onReceive(Context ctx, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        if (!intent.getAction().equals("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver") ||
                !ctx.getPackageName().equals(DeviceOwner.LAUNCHER_PACKAGE_NAME))
        {
            return;
        }

        MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(ctx));
        Installer MyInstaller    = new Installer(ctx);
        String file_path         = intent.getStringExtra("apk_path");
        String task_id           = "";
        if (intent.getStringExtra("task_id") != null)
        {
            task_id = intent.getStringExtra("task_id");
        }
        try
        {
            if (MyMainConfiguration.isDeviceRooted())
            {
                if (MyInstaller.installViaRootedShell(new ShellExecute(Runtime.getRuntime()), file_path))
                {
                    TaskExecutionReport.append(task_id, "completed");
                }
                else
                {
                    TaskExecutionReport.append(task_id, "aborted");
                }
            }
            else
            {
                MyInstaller.installPackage(task_id, file_path);
            }
        }
        catch (IOException e)
        {
            TaskExecutionReport.append(task_id, "aborted");
        }

        // delete downloaded files in all cases which are in player cache
        // important! No delete - No further updates
        if (file_path == null || !file_path.contains("cache"))
            return;

        File file = new File(file_path);
        if (file.delete() && !MyMainConfiguration.isDeviceRooted())
        {
            Log.e("InstallAppReceiver", "Delete failed and no root");
            return;
        }

        // if root and file is not deleted
        ShellExecute MyShellExecute = new ShellExecute(Runtime.getRuntime());
        boolean result = MyShellExecute.executeAsRoot("rm -rf "+ file_path);
        if (!result)
        {
            Log.e("InstallAppReceiver", "Even root delete failed: " + MyShellExecute.getErrorText() );
        }


    }
}
