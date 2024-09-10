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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *  capsule permissions
 */
public class AppPermissions
{
    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] BASE_PERMISSIONS_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String TAG = "AppPermissions";

    private String error_text;

    Activity MyActivity;
    MainConfiguration MyMainConfiguration;
    ShellExecute  MyShellExecute;

    public AppPermissions(Activity ma, MainConfiguration mc, ShellExecute se)
    {
        MyActivity          = ma;
        MyMainConfiguration = mc;
        MyShellExecute      = se;
        error_text = "";
    }

    public static void onRequestPermissionsResult(Activity ma, int request_code, @NonNull String[] permissions, @NonNull int[] grant_results)
    {
        if (request_code == REQUEST_PERMISSIONS)
        {
            if (grant_results.length > 0)
            {
                if (hasAllPermissions(ma))
                {
                    ma.recreate();
                }
            }
        }
    }

    public static boolean hasAllPermissions(Activity ma)
    {
        if (!hasManifestPermission(ma, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            return false;

        if (!hasManifestPermission(ma, Manifest.permission.READ_EXTERNAL_STORAGE))
            return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return true;

        // Will return always false, when  minSdkVersion < 26
        // https://developer.android.com/reference/android/content/pm/PackageManager.html#canRequestPackageInstalls()
        if (!ma.getPackageManager().canRequestPackageInstalls()) // REQUEST_INSTALL_PACKAGES
            return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            return true;

        return Environment.isExternalStorageManager(); // MANAGE_EXTERNAL_STORAGE
    }

    public void handleAllPermissions()
    {
        if (!hasManifestPermission(MyActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            handleOnePermission("WRITE_EXTERNAL_STORAGE");

        if (!hasManifestPermission(MyActivity, Manifest.permission.READ_EXTERNAL_STORAGE))
            handleOnePermission("READ_EXTERNAL_STORAGE");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        // Will return always false, when  minSdkVersion < 26
        // https://developer.android.com/reference/android/content/pm/PackageManager.html#canRequestPackageInstalls()
        if (!MyActivity.getPackageManager().canRequestPackageInstalls()) // REQUEST_INSTALL_PACKAGES
            handleOnePermission("REQUEST_INSTALL_PACKAGES");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            return;

         if (!Environment.isExternalStorageManager()) // MANAGE_EXTERNAL_STORAGE
                handleOnePermission("MANAGE_EXTERNAL_STORAGE");

    }


    public boolean grantPlayerPermissions()
    {
        if (!MyMainConfiguration.isDeviceRooted())
            return false;

        String[] permissions = {"READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE"};
        String package_name = DeviceOwner.PLAYER_PACKAGE_NAME;
        error_text = "";
        for (String permission : permissions)
        {
            if (!MyShellExecute.executeAsRoot("pm grant "+ DeviceOwner.PLAYER_PACKAGE_NAME +" android.permission." + permission))
            {
                Log.w(TAG, "Device is rooted, but cannot grant permission "+ permission +" to media player:" + package_name);
                return false;
            }
        }
        return true;
    }

    public void handleOnePermission(String permission)
    {
        if (MyMainConfiguration.isDeviceRooted())
        {
            requestPermissionByShell(permission);
        }
        else
        {
            requestPermissionByUser(permission);
        }

    }

    public String getErrorText()
    {
        return error_text;
    }

    private void requestPermissionByShell(String permission)
    {
        error_text     = "";
        boolean result = false;
        switch (permission)
        {
            case "READ_EXTERNAL_STORAGE":
            case "WRITE_EXTERNAL_STORAGE":
            case "SYSTEM_ALERT_WINDOW":
                result = MyShellExecute.executeAsRoot("pm grant "+ DeviceOwner.LAUNCHER_PACKAGE_NAME +" android.permission." + permission);
                break;
            case "REQUEST_INSTALL_PACKAGES":
            case "MANAGE_EXTERNAL_STORAGE":
               result = MyShellExecute.executeAsRoot("appops set --uid " + DeviceOwner.LAUNCHER_PACKAGE_NAME + " " + permission + " allow");
               break;
            default:
                break;
        }

        if (!result)
        {
            error_text = "Add Permission: " + permission + " via Shell failed: " + MyShellExecute.getErrorText() + "\n";
            Log.e(TAG, error_text);
        }
    }

    private void requestPermissionByUser(String permission)
    {
        switch (permission)
        {
            case "READ_EXTERNAL_STORAGE":
            case "WRITE_EXTERNAL_STORAGE":
                requestBasePermissions();
            break;
            case "REQUEST_INSTALL_PACKAGES":
                break;
            case "MANAGE_EXTERNAL_STORAGE":
                requestInstallPermissions();
                break;
            default:
                break;
        }
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

    private void requestInstallPermissions()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            return;

        try
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", MyActivity.getPackageName(), null);
            intent.setData(uri);
            MyActivity.startActivityForResult(intent, 100); // MANAGE_EXTERNAL_STORAGE_REQUEST_CODE
        }
        catch (Exception ex)
        {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            MyActivity.startActivity(intent);
        }
    }

    private static boolean hasManifestPermission(Activity ma, String permission)
    {
        return  (ma.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestBasePermissions()
    {
        MyActivity.requestPermissions(BASE_PERMISSIONS_LIST, REQUEST_PERMISSIONS);
    }
}
