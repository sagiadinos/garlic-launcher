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
package com.sagiadinos.garlic.launcher.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ProgressBar;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.Installer;

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
    private static final String     DOWNLOAD_PREFIX     = "tmp_";
    private String                  apk_path;
    private File                    apk_file = null;
    private Handler                 MyHandler        = new Handler();
    private boolean                 isDownloadActive = false;

    public PlayerDownload(Context c, DownloadManager dlm)
    {
        this.ctx           = c;
        apk_path          = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DOWNLOADED_FILENAME;
        MyDownloadManager = dlm;
    }

    public String getApkPath()
    {
        return apk_path;
    }

    public Boolean isGarlicPlayerAlreadyDownloaded()
    {
        if (!Installer.getAppNameFromPkgName(ctx, apk_path).equals(DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME))
        {
            // this means we could have an corrupt apk, so delete it
            removeOldApk();
            return false;
        }
        return true;
    }

    public void addDownloadToQueue(DownloadManager.Request dmr)
    {
        if (isDownloadActive)
        {
            return;
        }
        MyRequest = dmr;
        MyRequest.setTitle("garlic-player")
                .setDescription("Downloading Media Player")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_PREFIX + DOWNLOADED_FILENAME)
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
        ctx.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        download_id       = MyDownloadManager.enqueue(MyRequest);
        startProgressChecker();
    }

    public boolean isPendingDownload()
    {
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = MyDownloadManager.query(query);
        if (cursor.moveToFirst())
        {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING)
            {
                return true;
            }
        }
        cursor.close();
        return false;
    }


    private Runnable progressChecker = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                isDownloadActive  = true;
                checkProgress();
            }
            finally
            {
                MyHandler.postDelayed(progressChecker, 5000);
            }
        }
    };

    public void installDownloadedApp()
    {
        Installer MyInstaller = new Installer(ctx);
        try
        {
            MyInstaller.installPackage(apk_path);
            removeOldApk();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // call InstallApp Broadcast
/*        Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver");
        intent.putExtra("apk_path", apk_path);
        ctx.sendBroadcast(intent);
 */   }

    private void endDownload()
    {
        ctx.unregisterReceiver(onDownloadComplete);
        stopProgressChecker();
        renameDownloadedFile();

        if (isGarlicPlayerAlreadyDownloaded())
        {
            installDownloadedApp();
        }

    }

    private void removeOldApk()
    {
        if (apk_file != null && !apk_file.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            apk_file.delete();
        }
    }

    private void renameDownloadedFile()
    {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + DOWNLOAD_PREFIX + DOWNLOADED_FILENAME);
        //noinspection ResultOfMethodCallIgnored
        file.renameTo(new File(apk_path));
        apk_file = new File(apk_path);
    }


    private void startProgressChecker()
    {
        if (!isDownloadActive)
        {
            progressChecker.run();
            MyProgressbar.setProgress(0);
            MyProgressbar.setVisibility(View.VISIBLE);
        }
    }

    private void stopProgressChecker()
    {
        MyHandler.removeCallbacks(progressChecker);
        MyProgressbar.setVisibility(View.INVISIBLE);
        isDownloadActive = false;
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
            long total       = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            long downloaded  = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            if (total > 0)
            {
                long per_cent = 100L * downloaded / total; // 100L for long otherwise we risk an overflow an negative values
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


    public String getFileNameFromUri(Uri uri)
    {
        String result = null;
        if (uri.getScheme().equals("content"))
        {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            try
            {
                if (cursor != null && cursor.moveToFirst())
                {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally
            {
                cursor.close();
            }
        }
        if (result == null)
        {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1)
            {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
