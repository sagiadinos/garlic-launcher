package com.sagiadinos.garlic.launcher.receiver;

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

        // Fallback for some crappy asian images
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 && MyMainConfiguration.isDeviceRooted())
        {
            ShellExecute MyShellExecute = new ShellExecute(Runtime.getRuntime());
            MyShellExecute.executeAsRoot("input keyevent 26");
            return;
        }

  /*     for later revisions
            Todo: test how powermanager works on all root/non-root versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            DeviceOwner.setScreenBrightnessZero(
                    (DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE),
                    new ComponentName(MyContext, AdminReceiver.class)
            );
        }
        else
        {
*/            DeviceOwner.lockNow((DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE));
//        }

    }

    private void setScreenOn()
    {
        if (!MyMainConfiguration.useDeviceStandby())
            return;

        // Fallback for some crappy asian images
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 && MyMainConfiguration.isDeviceRooted())
        {
            ShellExecute MyShellExecute =  new ShellExecute(Runtime.getRuntime());
            MyShellExecute.executeAsRoot("input keyevent KEYCODE_WAKEUP");
            return;
        }

  /*     for later revisions
            Todo: test how powermanager works on all root/non-root versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            DeviceOwner.setScreenBrightnessFull(
                    (DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE),
                    new ComponentName(MyContext, AdminReceiver.class)
            );
        }
        else
        {
*/            PowerManager powerManager = (PowerManager) MyContext.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");
            //acquire will turn on the display
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
  //      }
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