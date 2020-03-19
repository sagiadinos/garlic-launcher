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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.sagiadinos.garlic.launcher.BuildConfig;

public class HomeLauncherManager
{
    private Context ctx;
    private DeviceOwner MyDeviceOwner;

    public HomeLauncherManager(DeviceOwner dvo, Context c)
    {
        ctx           = c;
        MyDeviceOwner = dvo;
    }

    public boolean isHomeActivity()
    {
        if (getHomeActivity().equals(ctx.getPackageName()))
        {
            return true;
        }
        return false;
    }

    public boolean toggleHomeActivity()
    {
        boolean ret;
        if (isHomeActivity())
        {
            restoreHomeActivity();
            ret = false;
        }
        else
        {
            becomeHomeActivity();
            ret = true;
        }
        showToast("Current Launcher is: " + getHomeActivity());
        return ret;
    }

    public void becomeHomeActivity()
    {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addCategory(Intent.CATEGORY_HOME);

        MyDeviceOwner.addPersistentPreferredActivity(intentFilter);
    }

    public void restoreHomeActivity()
    {
        MyDeviceOwner.clearMainPackageFromPersistent();
    }

    private String getHomeActivity()
    {
        Intent intent     = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ComponentName cn  = intent.resolveActivity(ctx.getPackageManager());
        if (cn != null)
        {
            return cn.getPackageName();
        }
        else
        {
            return "none";
        }
    }

    private void showToast(String text)
    {
        if (BuildConfig.DEBUG)
        {
            Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
        }
    }


}
