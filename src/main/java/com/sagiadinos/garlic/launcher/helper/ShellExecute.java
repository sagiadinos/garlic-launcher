package com.sagiadinos.garlic.launcher.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellExecute
{
    Runtime MyRuntime;
    String error_text;
    String output_text;

    public ShellExecute(Runtime myRuntime)
    {
        MyRuntime = myRuntime;
    }

    public boolean executeAsRoot(String command)
    {
        boolean succeed = false;
        try
        {
            error_text = "";
            Process MyProcess = MyRuntime.exec("su\n");
            DataOutputStream os = new DataOutputStream(MyProcess.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int res = MyProcess.waitFor();
            output_text = readBuffer(new BufferedReader(new InputStreamReader(MyProcess.getInputStream())));
            if (res == 0)
            {
                succeed = true;
            }
            else
            {
                error_text = readBuffer(new BufferedReader(new InputStreamReader(MyProcess.getErrorStream())));;
            }
        }
        catch (IOException | InterruptedException e)
        {
            error_text = e.getMessage();
        }
        return succeed;
    }

    public String getErrorText()
    {
        return error_text;
    }

    public String getOutputText()
    {
        return output_text;
    }

    private String readBuffer(BufferedReader std_error)
    {
        String s;
        StringBuilder ret = new StringBuilder();
        try
        {
            while ((s = std_error.readLine()) != null)
            {
                ret.append(s).append("\n");
            }
        }
        catch (IOException e)
        {
            ret.append(e.getMessage()).append("\n");
        }
        return ret.toString();
    }

}
