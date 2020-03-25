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
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

public class PlayerDownload
{
    private Context                 ctx;
    private DownloadManager         MyDownloadManager;
    private DownloadManager.Request MyRequest = null;
    private ProgressBar             MyProgressbar = null;
    private long                    download_id;
    private static final String     DOWNLOADED_FILENAME = "garlic-player.apk";
    private String apk_path = "";
    private File   apk_file;
    private Handler MyHandler = new Handler();
    private boolean isProgressCheckerRunning = false;

    public PlayerDownload(Context c, DownloadManager dlm)
    {
        this.ctx = c;
        apk_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DOWNLOADED_FILENAME;
        apk_file = new File( apk_path);
        MyDownloadManager = dlm;
    }

    public Boolean wasGarlicPlayerDownloaded()
    {
        if (!apk_file.exists())
        {
            return false;
        }
        return Installer.getAppNameFromPkgName(ctx, apk_file.getAbsolutePath()).equals(DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME);
    }

    public void addDownloadToQueue(DownloadManager.Request dmr)
    {
        MyRequest = dmr;
        MyRequest.setTitle("garlic-player")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOADED_FILENAME)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

    }

    public void startDownload(ProgressBar pb)
    {
        if (MyRequest == null)
        {
            return;
        }
        MyProgressbar = pb;
        removeOldApk();
    //    ctx.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    //    download_id       = MyDownloadManager.enqueue(MyRequest);
   //-    startProgressChecker();
    }

    private Runnable progressChecker = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                checkProgress();
            }
            finally
            {
                MyHandler.postDelayed(progressChecker, 1000);
            }
        }
    };

    public void installDownloadedApp()
    {
        Installer MyInstaller = new Installer(ctx);
        try
        {
            MyInstaller.installPackage(apk_path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void endDownload()
    {
        ctx.unregisterReceiver(onDownloadComplete);
        stopProgressChecker();
        if (wasGarlicPlayerDownloaded())
        {
            installDownloadedApp();
        }

    }

    private void removeOldApk()
    {
        if (apk_file.exists())
        {
            apk_file.delete();
        }
    }


    private void startProgressChecker()
    {
        if (!isProgressCheckerRunning)
        {
            progressChecker.run();
            MyProgressbar.setProgress(0);
            MyProgressbar.setVisibility(View.VISIBLE);
            isProgressCheckerRunning = true;
        }
    }

    private void stopProgressChecker()
    {
        MyHandler.removeCallbacks(progressChecker);
        MyProgressbar.setVisibility(View.INVISIBLE);
        isProgressCheckerRunning = false;
    }

    private void checkProgress()
    {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(~(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_SUCCESSFUL));
        Cursor cursor = MyDownloadManager.query(query);
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return;
        }
        do
        {
            double total       = cursor.getDouble(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            double downloaded  = cursor.getDouble(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            if (total > 0)
            {
                double per_cent =  100 / total * downloaded;
                MyProgressbar.setProgress((int) per_cent);
            }
        } while (cursor.moveToNext());
        cursor.close();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (download_id == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1))
            {
                endDownload();
            }
        }
    };

}
