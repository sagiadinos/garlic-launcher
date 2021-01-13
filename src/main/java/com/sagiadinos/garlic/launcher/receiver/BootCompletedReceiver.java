package com.sagiadinos.garlic.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;

public class BootCompletedReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            MainConfiguration MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(context));
            MyMainConfiguration.toggleJustBooted(true);
        }
    }
}
