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


import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class PlayerDownloader extends ConnectivityManager.NetworkCallback
{
    private final String            PLAYER_DOWNLOAD_URL = "https://garlic-player.com/downloads/garlic-player.apk";
    private static final String     DOWNLOADED_FILENAME = "garlic-player.apk";
    private ProgressBar             DownloadProgressBar;
    private String                  apk_path;
    private TextView                tvInformation;
    private Activity                MyActivity;

    public PlayerDownloader(Activity a, ProgressBar pb, TextView tv)
    {
        DownloadProgressBar = pb;
        tvInformation       = tv;
        MyActivity          = a;
        apk_path            = a.getExternalCacheDir() + "/" + DOWNLOADED_FILENAME;
    }

    @Override
    public void onAvailable(@NotNull Network network)
    {
        setInformationText(tvInformation, MyActivity.getString(R.string.download_player_in_progress));
        startPlayerDownload();
    }

    @Override
    public void onLost(@NotNull Network network)
    {
        setInformationText(tvInformation, MyActivity.getString(R.string.no_garlic_no_network));
    }

    private void startPlayerDownload()
    {
        try
        {
            deleteOldDownload();
            TimeUnit.SECONDS.sleep(5); // wait some seconds to get sure net is really reachable e.g.
            URL u                  = new URL(PLAYER_DOWNLOAD_URL);
            URLConnection c        = u.openConnection();
            c.connect();
            int file_size          = c.getContentLength();
            InputStream in         = c.getInputStream();
            byte[]      buffer     = new byte[1024];
            int  buffer_size;
            long downloaded_so_far = 0;

            FileOutputStream outputStream = new FileOutputStream(new File(apk_path));
            while ((buffer_size = in.read(buffer)) > 0)
            {
                downloaded_so_far += buffer_size;
                int percent = (int) ((downloaded_so_far * 100) / file_size);

                DownloadProgressBar.setProgress(percent, true);
                outputStream.write(buffer, 0, buffer_size);
            }
            if (Installer.getAppNameFromPkgName(MyActivity, apk_path).equals(DeviceOwner.PLAYER_PACKAGE_NAME))
            {
                installPlayer();
            }
        }
        catch (Exception e)
        {
            setInformationText(tvInformation, e.getMessage());
            e.printStackTrace();
        }
    }

    private void installPlayer()
    {
        setInformationText(tvInformation,  MyActivity.getString(R.string.install_player_in_progress));
        // call InstallApp Broadcast
        Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver");
        intent.putExtra("apk_path", apk_path);
        MyActivity.sendBroadcast(intent);
     }

    private void deleteOldDownload() throws Exception
    {
        File old_file = new File(apk_path);
        if (old_file.exists() && !old_file.delete())
        {
            throw new Exception(apk_path + " could not be deleted");
        }
    }

    private void setInformationText(final TextView text, final String value)
    {
        MyActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
}
