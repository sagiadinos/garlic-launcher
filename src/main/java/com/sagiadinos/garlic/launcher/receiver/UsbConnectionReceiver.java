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

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
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
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ctx = context;
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        // otherwise it can crash beacause we
        // try to do things which do not work without device owner rights
        if (!DeviceOwner.isDeviceOwner((DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE)))
        {
            return;
        }

        String action = intent.getAction();
        Uri uri = intent.getData();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED) && uri != null)
        {
            // Todo: refactor the down methods with a Sending Class
            // Todo: dispatch with a factory pattern.
            dispatchFilesOnUsb(intent.getData().getPath());
        }
    }

    /**
     *
     * @param path String
     */
    private void dispatchFilesOnUsb(String path)
    {
        File smil_index = new File(path + "/index.smil");
        if (checkAccessibility(smil_index))
        {
            MainConfiguration MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(ctx));
            MyMainConfiguration.storeSmilIndex(path + "/index.smil");

            ctx.sendBroadcast(createIntentForSmilIndex(smil_index));
            return;
        }

        File config_xml = new File(path + "/config.xml");
        if (checkAccessibility(config_xml))
        {
            Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.ConfigXMLReceiver");
            intent.putExtra("config_path", config_xml.getAbsolutePath());
            ctx.sendBroadcast(intent);
            return;
        }

        File player_apk = new File(path + "/garlic-player.apk");
        if (checkAccessibility(player_apk)/* &&
                Installer.getAppNameFromPkgName(ctx, player_apk.getAbsolutePath()).equals(DeviceOwner.PLAYER_PACKAGE_NAME)*/)
        {
            Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver");
            intent.putExtra("apk_path", player_apk.getAbsolutePath());
            intent.putExtra("task_id", "via usb");
            ctx.sendBroadcast(intent);
        }
    }

    private Intent createIntentForSmilIndex(File file)
    {
        Intent intent = new Intent("com.sagiadinos.garlic.player.java.SmilIndexReceiver");
        intent.putExtra("smil_index_path", file.getAbsolutePath());
        return intent;
    }

    private boolean checkAccessibility(File file)
    {
        return (file.exists() && file.canRead());
    }
}
