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

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;
import com.sagiadinos.garlic.launcher.helper.SharedConfiguration;

import java.util.Objects;

public class InstalledAppReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        String s = Objects.requireNonNull(intent.getData()).toString();
        if (s.equals("package:com.sagiadinos.garlic.launcher"))
        {
            return;
        }
        if (s.equals("package:"+ DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME))
        {
            tooglePlayerInstalled(context, isPlayerInstalled(intent));
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) ||
                intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
            )
        {
            DeviceOwner.reboot(context);
        }
    }

    private Boolean isPlayerInstalled(Intent intent)
    {
        return !Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_PACKAGE_REMOVED);
    }

    void tooglePlayerInstalled(Context context, Boolean installed)
    {
        try
        {
            SharedConfiguration MySharedConfiguration = new SharedConfiguration(context);
            MySharedConfiguration.togglePlayerInstalled(installed);
        }
        catch (GarlicLauncherException e)
        {
            e.printStackTrace();
        }
    }

}
