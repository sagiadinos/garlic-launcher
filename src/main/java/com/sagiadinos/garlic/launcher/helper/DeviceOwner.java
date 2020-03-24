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

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.widget.Toast;

import com.sagiadinos.garlic.launcher.BuildConfig;
import com.sagiadinos.garlic.launcher.MainActivity;
import com.sagiadinos.garlic.launcher.receiver.AdminReceiver;

/**
 *  DeviceOwner handles the methods to check for device owner
 *  and encapsulates some simple functions like reboot.
 *
 *  set device owner with
 *  adb shell dpm set-device-owner com.sagiadinos.garlic.launcher/.receiver.AdminReceiver
 *
 *  for removing
 *  adb shell dpm remove-active-admin com.sagiadinos.garlic.launcher/.receiver.AdminReceiver
 *
 * or if root deviced
 * adb shell  "su -c 'am broadcast -a android.intent.action.MASTER_CLEAR'"
 *
 */
public class DeviceOwner
{
    private DevicePolicyManager MyDevicePolicyManager;
    private ComponentName       MyDeviceAdmin;
    private Context             ctx;

    public static final String GARLIC_LAUNCHER_PACKAGE_NAME = "com.sagiadinos.garlic.launcher";
    public static final String GARLIC_PLAYER_PACKAGE_NAME = "com.sagiadinos.garlic.player";

    public DeviceOwner(Context c)
    {
        ctx                   = c;
        MyDeviceAdmin         = new ComponentName(ctx, AdminReceiver.class);
        MyDevicePolicyManager = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (MyDeviceAdmin == null || MyDevicePolicyManager == null)
        {
            showToast("handle device owner is null");
            return;
        }
        if (!isAdminActive())
        {
            showToast("This app is not a device admin!");
            return;
        }
        if (isDeviceOwner())
        {
            determinePermittedLockTaskPackages("");
        }
        else
        {
            showToast("This app is not the device owner!");
        }
    }

    public void activateRestrictions()
    {
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_APPS_CONTROL);
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_CONFIG_CREDENTIALS);

        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_REMOVE_USER);
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_ADD_USER);
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_MODIFY_ACCOUNTS);

        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_FUN);
   }

    public void deactivateRestrictions()
    {
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_APPS_CONTROL);
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_CONFIG_CREDENTIALS);

        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_REMOVE_USER);
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_ADD_USER);
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_MODIFY_ACCOUNTS);

        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_FUN);
    }

    public boolean isLockTaskPermitted()
    {
        return MyDevicePolicyManager.isLockTaskPermitted(ctx.getPackageName());
    }

    public boolean isAdminActive()
    {
        return MyDevicePolicyManager.isAdminActive(MyDeviceAdmin);
    }

    public boolean isDeviceOwner()
    {
        String s = ctx.getPackageName();
        return MyDevicePolicyManager.isDeviceOwnerApp(s);
    }

    public static boolean isDeviceOwner(Context ctx)
    {
        DevicePolicyManager dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm == null)
        {
            return false;
        }
        return dpm.isDeviceOwnerApp(ctx.getPackageName());
    }

    public void reboot()
    {
        if (isAdminActive())
        {
            MyDevicePolicyManager.reboot(MyDeviceAdmin);
        }
        else
        {
            showToast("This app is not a device owner!");
        }
    }

    public static void reboot(Context ctx)
    {
        DevicePolicyManager dpm         = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null && dpm.isDeviceOwnerApp(ctx.getPackageName()))
        {
            dpm.reboot(new ComponentName(ctx, AdminReceiver.class));
        }
    }

    public void addPersistentPreferredActivity(IntentFilter intentFilter)
    {
        ComponentName activity = new ComponentName(ctx, MainActivity.class);
        MyDevicePolicyManager.addPersistentPreferredActivity(MyDeviceAdmin, intentFilter, activity);

    }

    public void clearMainPackageFromPersistent()
    {
        if (isAdminActive())
        {
            MyDevicePolicyManager.clearPackagePersistentPreferredActivities(MyDeviceAdmin, ctx.getPackageName());
        }
        else
        {
            showToast("This app is not the device owner!");
        }
    }

    /**
     *
     * @param second_app_name String
     */
    public void determinePermittedLockTaskPackages(String second_app_name)
    {
        if (second_app_name.equals(""))
        {
            MyDevicePolicyManager.setLockTaskPackages(MyDeviceAdmin, new String[]{ctx.getPackageName(), ctx.getPackageName()+".ActivityConfigAdmin", GARLIC_PLAYER_PACKAGE_NAME});
        }
        else
        {
            // Todo Clear or add
            MyDevicePolicyManager.setLockTaskPackages(MyDeviceAdmin, new String[]{ctx.getPackageName(), GARLIC_PLAYER_PACKAGE_NAME, second_app_name});
        }
    }

    private void showToast(String text)
    {
        if (BuildConfig.DEBUG)
        {
            Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
        }
    }

}
