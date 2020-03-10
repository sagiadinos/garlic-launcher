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
 *  set device admin with
 *  adb shell dpm set-device-owner com.sagiadinos.garlic.launcher/.receiver.AdminReceiver
 *
 *  for removing
 *  adb shell dpm remove-active-admin com.sagiadinos.garlic.launcher/.receiver.AdminReceiver
 *
 *
 */
public class DeviceOwner
{
    private DevicePolicyManager dpm;
    private ComponentName       deviceAdmin;
    private Context             ctx               = null;

    public static final String GARLIC_LAUNCHER_PACKAGE_NAME = "com.sagiadinos.garlic.launcher";
    public static final String GARLIC_PLAYER_PACKAGE_NAME = "com.sagiadinos.garlic.player";
    public static final String QT_TEST_PACKAGE_NAME = "com.sagiadinos.garlic.qttest";

    public DeviceOwner(Context c)
    {
        ctx         = c;
        deviceAdmin = new ComponentName(ctx, AdminReceiver.class);
        dpm         = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (deviceAdmin == null || dpm == null)
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
        dpm.addUserRestriction(deviceAdmin, UserManager.DISALLOW_APPS_CONTROL);
        dpm.addUserRestriction(deviceAdmin, UserManager.DISALLOW_CONFIG_CREDENTIALS);

        dpm.addUserRestriction(deviceAdmin, UserManager.DISALLOW_REMOVE_USER);
        dpm.addUserRestriction(deviceAdmin, UserManager.DISALLOW_ADD_USER);
        dpm.addUserRestriction(deviceAdmin, UserManager.DISALLOW_MODIFY_ACCOUNTS);

        dpm.addUserRestriction(deviceAdmin, UserManager.DISALLOW_FUN);
    }

    public void deactivateRestrictions()
    {
        dpm.clearUserRestriction(deviceAdmin, UserManager.DISALLOW_APPS_CONTROL);
        dpm.clearUserRestriction(deviceAdmin, UserManager.DISALLOW_CONFIG_CREDENTIALS);

        dpm.clearUserRestriction(deviceAdmin, UserManager.DISALLOW_REMOVE_USER);
        dpm.clearUserRestriction(deviceAdmin, UserManager.DISALLOW_ADD_USER);
        dpm.clearUserRestriction(deviceAdmin, UserManager.DISALLOW_MODIFY_ACCOUNTS);

        dpm.clearUserRestriction(deviceAdmin, UserManager.DISALLOW_FUN);
    }

    public boolean isLockTaskPermitted()
    {
        return dpm.isLockTaskPermitted(ctx.getPackageName());
    }

    public boolean isAdminActive()
    {
        return dpm.isAdminActive(deviceAdmin);
    }

    public boolean isDeviceOwner()
    {
        String s = ctx.getPackageName();
        return dpm.isDeviceOwnerApp(s);
    }

    public static boolean isDeviceOwner(Context ctx)
    {
        DevicePolicyManager dpm         = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName       deviceAdmin = new ComponentName(ctx, AdminReceiver.class);
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
            dpm.reboot(deviceAdmin);
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
        dpm.addPersistentPreferredActivity(deviceAdmin, intentFilter, activity);

    }

    public void clearMainPackageFromPersistent()
    {
        if (isAdminActive())
        {
            dpm.clearPackagePersistentPreferredActivities(deviceAdmin, ctx.getPackageName());
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
            dpm.setLockTaskPackages(deviceAdmin, new String[]{ctx.getPackageName(), GARLIC_PLAYER_PACKAGE_NAME, QT_TEST_PACKAGE_NAME});

        }
        else
        {
            // Todo Clear or add
            dpm.setLockTaskPackages(deviceAdmin, new String[]{ctx.getPackageName(), GARLIC_PLAYER_PACKAGE_NAME, QT_TEST_PACKAGE_NAME, second_app_name});
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
