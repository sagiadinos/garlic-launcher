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

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import java.io.File;

/**
 * When a usb stick or SD card is mountet we should look on it for
 * a config, a software, asn index.smil, or we should use it as
 * additional space.
 */
public class UsbConnectionReceiver extends BroadcastReceiver
{
    Context ctx;
    DeviceOwner MyDeviceOwner = null;
    MainConfiguration MyMainConfiguration = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ctx = context;
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        // can crash if not device owner
        if (MyDeviceOwner == null || !MyDeviceOwner.isDeviceOwner())
        {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED) && intent.getData() != null)
        {
            // Todo: refactor the down methods with a Sending Class
            // Todo: dispatch with a factory pattern.
            dispatchFilesOnUsb(intent.getData().getPath());
        }
    }

    public void injectDependencies(DeviceOwner dow, MainConfiguration mmc)
    {
        MyDeviceOwner = dow;
        MyMainConfiguration = mmc;
    }

    /**
     *
     * @param path String
     */
    private void dispatchFilesOnUsb(String path)
    {
        File smil_index = createFile(path + "/index.smil");
        if (checkAccessibility(smil_index))
        {
            MyMainConfiguration.storeSmilIndex(path + "/index.smil");
            Intent intent = createIntent("com.sagiadinos.garlic.player.java.SmilIndexReceiver");
            intent.putExtra("smil_index_path", smil_index.getAbsolutePath());
            ctx.sendBroadcast(intent);
            return;
        }

        File config_xml = createFile(path + "/config.xml");
        if (checkAccessibility(config_xml))
        {
            Intent intent = createIntent("com.sagiadinos.garlic.launcher.receiver.ConfigXMLReceiver");
            intent.putExtra("config_path", config_xml.getAbsolutePath());
            ctx.sendBroadcast(intent);
            return;
        }

        File player_apk = createFile(path + "/garlic-player.apk");
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


    /**
     * This method is factory like for testing this class with Mockitos spy
     * So we avoid using tools like PowerMock which adds complexity
     *
     * @return Intent
     */
    protected Intent createIntent(String action)
    {
        return new Intent(action);
    }
    /**
     * This method is factory like for testing this class with Mockitos spy
     * So we avoid using tools like PowerMock which adds complexity
     *
     * @return Intent
     */
    protected File createFile(String file_path)
    {
        return new File(file_path);
    }

}
