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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class AppPermissions
{
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.DELETE_PACKAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static String[] binaryPaths= {
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/su/bin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/",
            "/system/app/Superuser.apk",
            "/cache",
            "/data",
            "/dev"
    };

    public static void verifyStoragePermissions(Activity ma)
    {
        int permission = ma.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            ma.requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        // Check for overlay permission. If not enabled, request for it.
        if (isDeviceRooted() && !isOverlayAllowed(ma))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + ma.getPackageName()));
            ma.startActivityForResult(intent, 12);
        }
    }

    public static boolean isOverlayAllowed(Activity ma)
    {
        return Settings.canDrawOverlays(ma);
    }

    /**
     *  Look at https://medium.com/@deekshithmoolyakavoor/root-detection-in-android-device-9144b7c2ae07
     *
     * @return Boolean
     */
    public static boolean isDeviceRooted()
    {
        return (detectTestKeys() || checkForBinary("su") || checkForBinary("busybox"));
    }

    private static boolean detectTestKeys()
    {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkForBinary(String filename)
    {
        for (String path : binaryPaths)
        {
            File f = new File(path, filename);
            boolean fileExists = f.exists();
            if (fileExists)
            {
                return true;
            }
        }
        return false;
    }
}
