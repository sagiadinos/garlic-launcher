package com.sagiadinos.garlic.launcher.helper;

import java.io.DataOutputStream;
import java.io.IOException;

public class ShellExecute
{
    Runtime MyRuntime;

    public ShellExecute(Runtime myRuntime)
    {
        MyRuntime = myRuntime;
    }

    public boolean executeAsRoot(String command)
    {
        boolean succeed = false;
        try
        {
            Process MyProcess = MyRuntime.exec("su\n");
            DataOutputStream os = new DataOutputStream(MyProcess.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            if (MyProcess.waitFor() == 0)
            {
                succeed = true;
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        return succeed;
    }
}
