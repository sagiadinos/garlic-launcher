package com.sagiadinos.garlic.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;
import com.sagiadinos.garlic.launcher.helper.SharedConfiguration;

import java.util.Objects;

public class InstalledAppReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        String s = Objects.requireNonNull(intent.getData()).toString();
        if (s.equals("package:com.sagiadinos.garlic.launcher"))
        {
            return;
        }
        if (s.equals("package:"+ DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME))
        {
            tooglePlayerInstalled(context, isPlayerInstalled(intent));
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) ||
                intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED) ||
                intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)
            )
        {
            DeviceOwner.reboot(context);
        }
    }

    private Boolean isPlayerInstalled(Intent intent)
    {
        return !Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_PACKAGE_REMOVED);
    }

    void tooglePlayerInstalled(Context context, Boolean installed)
    {
        try
        {
            SharedConfiguration MySharedConfiguration = new SharedConfiguration(context);
            MySharedConfiguration.togglePlayerInstalled(installed);
        }
        catch (GarlicLauncherException e)
        {
            e.printStackTrace();
        }
    }

}
