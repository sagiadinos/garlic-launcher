package com.sagiadinos.garlic.launcher.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.sagiadinos.garlic.launcher.BuildConfig;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.ShellExecute;
import com.sagiadinos.garlic.launcher.helper.TaskExecutionReport;


public class CommandReceiver extends BroadcastReceiver
{
    Intent MyIntent;
    Context MyContext;
    MainConfiguration MyMainConfiguration;
    private PowerManager.WakeLock MyWakeLock;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        MyIntent = intent;
        MyContext = context;
        MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(MyContext));

        String command = MyIntent.getStringExtra("command");

        switch (command)
        {
            case "reboot": reboot();
                break;
            case "screen_off": setScreenOff();
                break;
            case "screen_on": setScreenOn();
                break;

        }
    }

    private void setScreenOff()
    {
        if (!MyMainConfiguration.useDeviceStandby())
            return;

        PowerManager MyPowerManager = (PowerManager) MyContext.getSystemService(Context.POWER_SERVICE);
        MyWakeLock = MyPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Launcher:WakeLockTag");
        MyWakeLock.acquire(2*24*60*60*1000L); // 2 days

        DeviceOwner.lockNow((DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE));
    }

    private void setScreenOn()
    {
        if (!MyMainConfiguration.useDeviceStandby())
            return;

        if (MyMainConfiguration.isDeviceRooted())
        {
            ShellExecute MyShellExecute =  new ShellExecute(Runtime.getRuntime());
            MyShellExecute.executeAsRoot("input keyevent KEYCODE_WAKEUP");
        }
        else
        {
            AlarmManager alarmManager = (AlarmManager) MyContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent("com.sagiadinos.garlic.launcher.receiver.InForegroundReceiver");
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(MyContext, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            long triggerTime = System.currentTimeMillis() + 1000; // set alarm for 1 second
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, alarmIntent);
        }

     }

    private void reboot()
    {
        String task_id = "";
        if (MyIntent.getStringExtra("task_id") != null)
        {
            task_id = MyIntent.getStringExtra("task_id");
        }
        TaskExecutionReport.append(task_id, "completed");
        if (!BuildConfig.DEBUG)
        {

            DeviceOwner.reboot(
                    (DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE),
                    new ComponentName(MyContext, AdminReceiver.class)
            );


        }
    }
}