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

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

public class PlayerDownload
{
    private Context                 ctx;
    private DownloadManager         MyDownloadManager;
    private long                    download_id;
    private final String            DOWNLOAD_URL        = "https://garlic-player.com/downloads/ci-builds/latest_android_player.apk";
    private final String            DOWNLOADED_FILENAME = "garlic-player.apk";

    public PlayerDownload(Context c)
    {
        this.ctx = c;
    }

    public static Boolean isGarlicPlayerInstalled(Context c)
    {
       return Installer.isPackageInstalled(c, DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME);
    }

    public void startDownload()
    {
        removePreviousDownload();
        ctx.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        MyDownloadManager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        download_id       = MyDownloadManager.enqueue(prepareRequest());

    }

    private void endDownload()
    {
        ctx.unregisterReceiver(onDownloadComplete);
        String s = Installer.getAppNameFromPkgName(ctx, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DOWNLOADED_FILENAME);
        if (s.equals(DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME))
        {
            // calll InstallApp Broadcast
            Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver");
            intent.putExtra("apk_path", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DOWNLOADED_FILENAME);
            ctx.sendBroadcast(intent);
        }

    }


    private DownloadManager.Request prepareRequest()
    {
        return new DownloadManager.Request(Uri.parse(DOWNLOAD_URL))
                .setTitle("garlic-player")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOADED_FILENAME)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            //Checking if the received broadcast is for our enqueued download by matching download id
            if (download_id == id)
            {
                endDownload();
            }
        }
    };

    private void removePreviousDownload()
    {
        File file = new File(  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DOWNLOADED_FILENAME);
        if (file.exists())
        {
            file.delete();
        }
    }
}
