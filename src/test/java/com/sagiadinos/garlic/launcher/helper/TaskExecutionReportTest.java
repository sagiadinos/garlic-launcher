package com.sagiadinos.garlic.launcher.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class TaskExecutionReportTest
{
    @AfterEach
    void tearDown()
    {
        File TestFile = new File("./here/task_execution_log.xml");
        if (TestFile.exists() && !TestFile.delete())
        {
            fail();
        }

        File dir = new File("./here");
        if (dir.exists() && !dir.delete())
        {
            fail();
        }

    }

    @Test
    void constructor()
    {
        TaskExecutionReport MyTestClass  = new TaskExecutionReport("./here");
        File TestFile = new File("./here/task_execution_log.xml");
        assertTrue(TestFile.exists());
    }

    @Test
    void append()
    {
        TaskExecutionReport MyTestClass  = new TaskExecutionReport("./here");
        String task_id = "the_task_id";
        String state   = "the_state";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmXXX", Locale.US);
        df.setTimeZone(TimeZone.getDefault());
        String date = df.format(new Date());

        TaskExecutionReport.append(task_id, state);
        File TestFile = new File("./here/task_execution_log.xml");
        StringBuilder text = new StringBuilder();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(TestFile));
            String line = br.readLine();
            br.close();
            String expected = "<task id=\""+task_id+"\">"+ "<lastUpdateTime>"+date+"</lastUpdateTime>"+"<state>"+state+"</state></task>";
            assertEquals(expected, line);

        }
        catch (IOException e)
        {
            //You'll need to add proper error handling here
        }
    }
}