package com.sagiadinos.garlic.launcher.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.ShellExecute;
import com.sagiadinos.garlic.launcher.helper.TaskExecutionReport;

import java.io.File;

public class CommandReceiver extends BroadcastReceiver
{
    Intent MyIntent;
    Context MyContext;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        MyIntent = intent;
        MyContext = context;

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
        ShellExecute MyShellExecute =  new ShellExecute(Runtime.getRuntime());
        MyShellExecute.executeAsRoot("input keyevent 26");
        /*   DeviceOwner.lock(
                (DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE),
                new ComponentName(MyContext, AdminReceiver.class));
        Intent it = new         Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        it.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(this, AdminReceiver.class));
        startActivityForResult(it, 0);
       */

//       ShellExecute MyShellExecute =  new ShellExecute(Runtime.getRuntime());
 //       MyShellExecute.executeAsUser("input keyevent 26");
    }

    private void setScreenOn()
    {
        ShellExecute MyShellExecute =  new ShellExecute(Runtime.getRuntime());
        MyShellExecute.executeAsRoot("input keyevent KEYCODE_WAKEUP");
    }

    private void reboot()
    {
        String task_id = "";
        if (MyIntent.getStringExtra("task_id") != null)
        {
            task_id = MyIntent.getStringExtra("task_id");
        }
        TaskExecutionReport.append(task_id, "completed");
        DeviceOwner.reboot(
                (DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE),
                new ComponentName(MyContext, AdminReceiver.class)
        );
    }
}