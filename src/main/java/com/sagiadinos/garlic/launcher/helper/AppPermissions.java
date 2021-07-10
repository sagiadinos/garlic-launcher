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
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.jetbrains.annotations.NotNull;

/**
 *  capsulate the permissions
 */
public class AppPermissions
{
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] PERMISSIONS_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String TAG = "AppPermissions";

    private String error_text;

    Activity MyActivity;
    MainConfiguration MyMainConfiguration;

    public AppPermissions(Activity ma, MainConfiguration mc)
    {
        MyActivity = ma;
        MyMainConfiguration = mc;
        error_text = "";
    }

    public static void onRequestPermissionsResult(Activity ma, int request_code, @NonNull String[] permissions, @NonNull int[] grant_results)
    {
        if (request_code == REQUEST_PERMISSIONS)
        {
            if (grant_results.length > 0)
            {
                // Validate the permissions result
                if (hasImportantPermissions(ma))
                {
                    ma.recreate();
                }
                else
                {
                    ma.finish();
                }
            }
        }
    }

    public boolean grantPlayerPermissions(@NotNull ShellExecute MyShellExecute)
    {
        if (!MyMainConfiguration.isDeviceRooted())
            return false;

        String[] permissions = {"READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE"};
        String package_name = DeviceOwner.PLAYER_PACKAGE_NAME;
        error_text = "";
        for (String perm : permissions)
        {
            if (!executeShell(MyShellExecute, package_name, perm))
            {
                Log.w(TAG, "Device is rooted, but cannot grant permission "+ perm +" to media player:" + package_name);
                return false;
            }
        }
        return true;
    }

    public static boolean hasImportantPermissions(Activity ma)
    {
        int permissions = ma.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissions != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        permissions = ma.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissions == PackageManager.PERMISSION_GRANTED;
    }

    public void handlePermissions(ShellExecute MyShellExecute)
    {
        if (MyMainConfiguration.isDeviceRooted())
        {
            requestPermissionsbyShell(MyShellExecute);
        }
        else
        {
            requestPermissions();
        }
    }

    public String getErrorText()
    {
        return error_text;
    }

    public boolean verifyOverlayPermissions(Intent intent)
    {
        // Check for overlay permission. If not enabled, request for it.
        if (MyMainConfiguration.isDeviceRooted() && !Settings.canDrawOverlays(MyActivity))
        {
            MyActivity.startActivityForResult(intent, 12);
        }
        return Settings.canDrawOverlays(MyActivity);
    }

    private void requestPermissions()
    {
        MyActivity.requestPermissions(PERMISSIONS_LIST, REQUEST_PERMISSIONS);
    }

    private void requestPermissionsbyShell(ShellExecute MyShellExecute)
    {
        String[] permissions = {"READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "SYSTEM_ALERT_WINDOW"};
        error_text = "";
        for (String perm : permissions)
        {
            if (!executeShell(MyShellExecute, "com.sagiadinos.garlic.launcher", perm))
            {
                error_text = "Add Permission: " + perm + " via Shell failed: " + MyShellExecute.getErrorText() + "\n";
                Log.e(TAG, error_text);
                return;
            }
        }
    }

    private boolean executeShell(@NotNull ShellExecute MyShellExecute, String package_name, String permission)
    {
       return MyShellExecute.executeAsRoot("pm grant "+ package_name +" android.permission." + permission);
    }
}
