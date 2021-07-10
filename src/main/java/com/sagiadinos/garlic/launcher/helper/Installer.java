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
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *  This class install and uninstall (later) software
 *  Only apk file-path is necessary
 */
public class Installer
{
    private final Context ctx;
    private final PackageInstaller MyPackageInstaller;
    private String task_id = "";

    public Installer(Context c)
    {
        ctx = c;
        MyPackageInstaller = ctx.getPackageManager().getPackageInstaller();
    }

    public static Boolean isMediaPlayerInstalled(Context c)
    {
        return isPackageInstalled(c, DeviceOwner.PLAYER_PACKAGE_NAME);
    }

    public static Boolean hasPlayerPermissions(Context c)
    {
        boolean is_read = false;
        boolean is_write = false;

        try
        {
            PackageManager pm             = c.getPackageManager();
            PackageInfo pi                = pm.getPackageInfo(DeviceOwner.PLAYER_PACKAGE_NAME, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = pi.requestedPermissions;
            if(requestedPermissions == null)
                return false;

            int i = 0;
            for (String requestedPermission : requestedPermissions)
            {
                if (requestedPermission.equals("android.permission.READ_EXTERNAL_STORAGE"))
                    is_read =  ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0);
                if (requestedPermission.equals("android.permission.WRITE_EXTERNAL_STORAGE"))
                    is_write =  ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0);
                i++;
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
        return (is_read && is_write);
    }



    public static ResolveInfo determineOtherLauncherPackagename(PackageManager pm)
    {
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
        for (ResolveInfo resolveInfo : lst)
        {
            if (!resolveInfo.activityInfo.packageName.equals(DeviceOwner.LAUNCHER_PACKAGE_NAME))
                return resolveInfo;
        }
        return null;
    }


    /**
     * Needed, because some asian rooted images are so crappy  that
     * an install via package manger fails without a notification.
     */
    public boolean installViaRootedShell(ShellExecute MyShellExecute, String package_path)
    {
        String cmd     = "pm install -r " + package_path + "\n";
        return MyShellExecute.executeAsRoot(cmd);
    }

    public void installPackage(String t_id, String package_path) throws IOException
    {
        this.task_id = t_id;
        String package_name   = getAppNameFromPkgName(ctx, package_path);
        if (package_name.isEmpty())
        {
            return;
        }

        PackageInstaller.SessionParams params  = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(package_name);
        int                      session_id    = MyPackageInstaller.createSession(params);
        PackageInstaller.Session session       = MyPackageInstaller.openSession(session_id);

        MyPackageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback()
        {
            @Override
            public void onCreated(int sessionid)
            {
            }

            @Override
            public void onBadgingChanged(int sessionId)
            {
            }

            @Override
            public void onActiveChanged(int sessionId, boolean active)
            {
            }

            @Override
            public void onProgressChanged(int sessionId, float progress)
            {
            }

            @Override
            public void onFinished(int sessionId, boolean success)
            {
                if (success)
                {
                    TaskExecutionReport.append(task_id, "completed");
                }
                else
                {
                    TaskExecutionReport.append(task_id, "aborted");
                }
            }
        });

        InputStream  in  = createInputStream(package_path);
        OutputStream out = session.openWrite(package_name, 0, -1);
        byte[] buffer = new byte[16384];
        int c;
        while ((c = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        in.close();
        out.close();

        // to commit without re-creating MainActivity
        PendingIntent pi = PendingIntent.getBroadcast(ctx, session_id, new Intent("com.sagiadinos.garlic.launcher.ACTION_UPDATE"), 0);
        session.commit(pi.getIntentSender());
   //   session.close();
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

    public static boolean isPackageInstalled(Context c, String check_package)
    {
        PackageManager pm = c.getPackageManager();
        try
        {
            pm.getPackageInfo(check_package, PackageManager.GET_META_DATA);
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
