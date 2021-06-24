package com.sagiadinos.garlic.launcher.helper;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TaskExecutionReport
{
    private static File LogFile;

    public TaskExecutionReport(String path)
    {
        try
        {
            createDir(path);
            LogFile = createFile(path + "/task_execution_log.xml");
        }
        catch (IOException | GarlicLauncherException e)
        {
            Log.wtf("TaskExecutionReport", e.getMessage());
        }
    }

    public static void append(String task_id, String state)
    {
        try
        {
            String text = "<task id=\""+task_id+"\">" +
                "<lastUpdateTime>"+getCurrentIsoDateTime()+"</lastUpdateTime>" +
                "<state>"+state+"</state></task>";

            BufferedWriter buf = new BufferedWriter(new FileWriter(LogFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            Log.wtf("TaskExecutionReport", e.getMessage());
        }
    }

    private static String getCurrentIsoDateTime()
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.US);
        df.setTimeZone(TimeZone.getDefault());
        return df.format(new Date());
    }

    private void createDir(String path) throws GarlicLauncherException
    {
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new GarlicLauncherException(path + " could not be created");
        }
    }

    private File createFile(String path) throws GarlicLauncherException, IOException
    {
        File file = new File(path);
        if (!file.exists() && !file.createNewFile())
        {
            throw new GarlicLauncherException(path + " could not be created");
        }
        return file;
    }
}
