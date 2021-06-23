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

package com.sagiadinos.garlic.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.helper.CopyHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * When a usb stick or SD card is mountet we should look on it for
 * a config, a software, asn index.smil, or we should use it as
 * additional space.
 */
public class UsbConnectionReceiver extends BroadcastReceiver
{
    Context ctx;
    String mount_path = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ctx = context;
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED) && intent.getData() != null)
        {
            // Todo: refactor the down methods with a Sending Class
            // Todo: dispatch with a factory pattern.
            mount_path               = intent.getData().getPath();
            dispatchFilesOnUsb();
        }
    }

    private void dispatchFilesOnUsb()
    {
        if (hasSmilDir(createMainConfiguration()))
            return;

        if (hasConfigXML())
            return;

        hasPlayerApk();
    }

    private boolean hasSmilDir(MainConfiguration MyMainConfiguration)
    {
        File smil_index = createFile(mount_path + "/SMIL");
        if (checkAccessibility(smil_index))
        {
            try
            {
                File source               = createFile(mount_path + "/SMIL");
                File destination = Environment.getExternalStoragePublicDirectory("garlic-player/cache/SMIL");

               // File destination          = createFile(ctx.getExternalFilesDir(null).getAbsolutePath()
                        // + "/garlic-player/cache/SMIL");
                CopyHandler MyCopyHandler = createCopyHandler();
                MyCopyHandler.removeRecursively(destination); // if there is a previous copy
                MyCopyHandler.copy(source, destination);

                MyMainConfiguration.storeSmilIndex(destination + "/index.smil");
                Intent intent = createIntent("com.sagiadinos.garlic.player.java.SmilIndexReceiver");
                intent.putExtra("smil_index_path", destination.getAbsolutePath() + "/index.smil");
                ctx.sendBroadcast(intent);
                return true;
            }
            catch (IOException e)
            {
                Toast toast = Toast.makeText(ctx, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
                Log.e("UsbConnectionReceiver", Objects.requireNonNull(e.getMessage()));
            }
        }
        return false;
    }

    private boolean hasConfigXML()
    {
        File config_xml = createFile(mount_path + "/config.xml");
        if (checkAccessibility(config_xml))
        {
            Intent intent = createIntent("com.sagiadinos.garlic.launcher.receiver.ConfigXMLReceiver");
            intent.putExtra("config_path", config_xml.getAbsolutePath());
            ctx.sendBroadcast(intent);
            return true;
        }

        return false;
    }

    private void hasPlayerApk()
    {
        File player_apk = createFile(mount_path + "/garlic-player.apk");
        if (checkAccessibility(player_apk))
        {
            Intent intent = createIntent("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver");
            intent.putExtra("apk_path", player_apk.getAbsolutePath());
            intent.putExtra("task_id", "via usb");
            ctx.sendBroadcast(intent);
        }
    }

    private boolean checkAccessibility(File file)
    {
        return (file.exists() && file.canRead());
    }


 // This methods are factory like for testing this class with Mockitos spy
 // So we avoid using tools like PowerMock which adds complexity

    protected Intent createIntent(String action)
    {
        return new Intent(action);
    }

    protected File createFile(String file_path)
    {
        return new File(file_path);
    }

    protected CopyHandler createCopyHandler()
    {
        return new CopyHandler();
    }

    protected MainConfiguration createMainConfiguration()
    {
        return new MainConfiguration(createSharedPreferencesModel());
    }

    protected SharedPreferencesModel createSharedPreferencesModel()
    {
        return new SharedPreferencesModel(ctx);
    }

}
