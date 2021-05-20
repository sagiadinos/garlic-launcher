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

package com.sagiadinos.garlic.launcher.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class HomeLauncherManager
{
    private final Context ctx;
    private final Intent  MyIntent;

    public HomeLauncherManager(Context c, Intent intent)
    {
        ctx           = c;
        MyIntent      = intent;
    }

    public boolean isHomeActivity()
    {
        return getHomeActivity().equals(ctx.getPackageName());
    }

    public void becomeHomeActivity(DeviceOwner MyDeviceOwner)
    {
        MyDeviceOwner.addPersistentPreferredActivity();
    }

    public void restoreHomeActivity(DeviceOwner MyDeviceOwner)
    {
        MyDeviceOwner.clearMainPackageFromPersistent();
    }

    private String getHomeActivity()
    {
        MyIntent.addCategory(Intent.CATEGORY_HOME);
        ComponentName cn  = MyIntent.resolveActivity(ctx.getPackageManager());
        if (cn != null)
        {
            return cn.getPackageName();
        }
        else
        {
            return "none";
        }
    }

}
