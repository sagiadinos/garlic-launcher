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

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;

import com.sagiadinos.garlic.launcher.BuildConfig;


/**
 *  DeviceOwner handles the methods to check for device owner
 *  and encapsulates some simple functions like reboot.
 *
 *  set device owner with
 *  adb shell dpm set-device-owner com.sagiadinos.garlic.launcher/.receiver.AdminReceiver
 *
 *  for removing. Only debug version
 *  adb shell dpm remove-active-admin com.sagiadinos.garlic.launcher/.receiver.AdminReceiver
 *
 * adb shell  "su -c 'am broadcast -a android.intent.action.MASTER_CLEAR'"
 *
 */
public class DeviceOwner
{
    private static final String TAG  = "DeviceAdmin";
    private final DevicePolicyManager MyDevicePolicyManager;
    private final ComponentName       MyDeviceAdmin;
    private final ComponentName       MyActivityComponent;
    private final IntentFilter        MyIntentFilter;

    public static final String LAUNCHER_PACKAGE_NAME = "com.sagiadinos.garlic.launcher";
    public static final String PLAYER_PACKAGE_NAME = "com.sagiadinos.garlic.player";

    public DeviceOwner(DevicePolicyManager dpm, ComponentName da, ComponentName activity, IntentFilter filter)
    {
        MyDeviceAdmin         = da;
        MyDevicePolicyManager = dpm;
        MyActivityComponent   = activity;
        MyIntentFilter        = filter;
    }

    /**
     * This works only on rooted devices
     */
    public boolean makeDeviceOwner(ShellExecute MyShellExecute)
    {

       if (!MyShellExecute.executeAsRoot("dpm set-device-owner com.sagiadinos.garlic.launcher/.receiver.AdminReceiver"))
       {
           Log.e(TAG, MyShellExecute.getErrorText());
           return false;
       }
       return true;
    }

    public void activateRestrictions()
    {
      //  MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_APPS_CONTROL);
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_CONFIG_CREDENTIALS);

        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_REMOVE_USER);
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_ADD_USER);
        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_MODIFY_ACCOUNTS);

        MyDevicePolicyManager.addUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_FUN);
    }

    public void deactivateRestrictions()
    {
        // MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_APPS_CONTROL);
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_CONFIG_CREDENTIALS);

        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_REMOVE_USER);
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_ADD_USER);
        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_MODIFY_ACCOUNTS);

        MyDevicePolicyManager.clearUserRestriction(MyDeviceAdmin, UserManager.DISALLOW_FUN);
    }

    public boolean grandPermissions(String permission)
    {
         return MyDevicePolicyManager.setPermissionGrantState(
                MyDeviceAdmin,
                LAUNCHER_PACKAGE_NAME,
                permission,
                DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED
         );
    }

    public boolean isLockTaskPermitted()
    {
        return MyDevicePolicyManager.isLockTaskPermitted(LAUNCHER_PACKAGE_NAME);
    }

    public boolean isAdminActive()
    {
        return MyDevicePolicyManager.isAdminActive(MyDeviceAdmin);
    }

    public boolean isDeviceOwner()
    {
        return MyDevicePolicyManager.isDeviceOwnerApp(LAUNCHER_PACKAGE_NAME);
    }

    public static void lockNow(DevicePolicyManager dpm)
    {
        dpm.lockNow();
    }

    public static void setScreenBrightness(DevicePolicyManager dpm, ComponentName da, int value)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            dpm.setSystemSetting(da, Settings.System.SCREEN_BRIGHTNESS_MODE,
                    String.valueOf(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL));
            dpm.setSystemSetting(da, Settings.System.SCREEN_BRIGHTNESS, String.valueOf(value));
        }
    }

    public static void reboot(DevicePolicyManager dpm, ComponentName da)
    {
        if (dpm != null && dpm.isDeviceOwnerApp(LAUNCHER_PACKAGE_NAME))
        {
            dpm.reboot(da);
        }

    }

    public void addPersistentPreferredActivity()
    {
        MyIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        MyIntentFilter.addCategory(Intent.CATEGORY_HOME);
        MyDevicePolicyManager.addPersistentPreferredActivity(MyDeviceAdmin, MyIntentFilter, MyActivityComponent);
    }

    public void clearMainPackageFromPersistent()
    {
        if (isDeviceOwner())
        {
            MyDevicePolicyManager.clearPackagePersistentPreferredActivities(MyDeviceAdmin, LAUNCHER_PACKAGE_NAME);
        }
    }

    /**
     *
     * @param second_app_name String
     */
    public void determinePermittedLockTaskPackages(String second_app_name)
    {
        String[] s;
        if (second_app_name == null)
        {
            return;
        }
        if (second_app_name.isEmpty())
        {
            s = new String[]{DeviceOwner.LAUNCHER_PACKAGE_NAME, DeviceOwner.PLAYER_PACKAGE_NAME};
        }
        else
        {
            s = new String[]{LAUNCHER_PACKAGE_NAME, PLAYER_PACKAGE_NAME, second_app_name};
        }
        // Todo Later Clear or add functionality
        MyDevicePolicyManager.setLockTaskPackages(MyDeviceAdmin, s);
    }

}
