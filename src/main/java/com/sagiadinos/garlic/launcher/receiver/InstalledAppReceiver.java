package com.sagiadinos.garlic.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;

import java.io.File;

public class InstalledAppReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        if (!intent.getAction().equals("com.sagiadinos.garlic.launcher.receiver.InstalledAppReceiver") ||
                !context.getPackageName().equals(DeviceOwner.GARLIC_LAUNCHER_PACKAGE_NAME))
        {
            return;
        }
        DeviceOwner.reboot(context);
    }
}
