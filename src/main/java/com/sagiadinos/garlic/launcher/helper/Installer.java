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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *  This class install and uninstall (later) software
 *  Only apk file-path is neccessary
 */
public class Installer
{
    private Context ctx;
    private PackageInstaller MyPackageInstaller;
    private PackageInstaller.Session session;
    public static final String ACTION_INSTALL_COMPLETE
            = "com.sagiadinos.garlic.launcher.INSTALL_COMPLETE";

    public Installer(Context c)
    {
        ctx = c;
        MyPackageInstaller = ctx.getPackageManager().getPackageInstaller();
    }


    public static Boolean isGarlicPlayerInstalled(Context c)
    {
        return isPackageInstalled(c, DeviceOwner.PLAYER_PACKAGE_NAME);
    }

    public void installPackage(String package_path) throws IOException
    {

        InputStream fileInputStream            = createInputStream(package_path);
        PackageInstaller.SessionParams params  = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        String package_name                    = getAppNameFromPkgName(ctx, package_path);
        if (package_name.isEmpty())
        {
            return;
        }
        params.setAppPackageName(package_name);

        // set params
        int                      session_id = MyPackageInstaller.createSession(params);
        session                             = MyPackageInstaller.openSession(session_id);
        OutputStream              out       = session.openWrite(package_name, 0, -1);

        byte[] buffer = new byte[65536];
        int c;
        while ((c = fileInputStream.read(buffer)) != -1)
        {
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        fileInputStream.close();
        out.close();

        // to commit without re-creating MainActivity
        session.commit(PendingIntent.getBroadcast(ctx, session_id, new Intent(ACTION_INSTALL_COMPLETE), 0).getIntentSender());
        session.close();
    }

    public void uninstall(String package_name)
    {
        if (!isPackageInstalled(ctx, package_name))
        {
            return;
        }
        Intent intent = new Intent(ctx, ctx.getClass());
        PendingIntent sender = PendingIntent.getActivity(ctx, 0, intent, 0);
        MyPackageInstaller.uninstall(package_name, sender.getIntentSender());
    }

    public static boolean isPackageInstalled(Context c, String targetPackage)
    {
        PackageManager pm = c.getPackageManager();
        try
        {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
        return true;
    }

    public static String getAppNameFromPkgName(Context ctx, String package_path)
    {
        PackageManager packageManager = ctx.getPackageManager();
        PackageInfo info              = packageManager.getPackageArchiveInfo(package_path, 0);
        if (info == null)
        {
            return "";
        }

        return info.packageName;
    }

    private InputStream createInputStream(String path_name)
            throws FileNotFoundException
    {
        File file = new File(path_name);
        return new FileInputStream(file);
    }

}
