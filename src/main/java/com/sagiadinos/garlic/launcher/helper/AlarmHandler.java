package com.sagiadinos.garlic.launcher.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmHandler
{
    final private String TAG = "AlarmHandler";
    private AlarmManager MyAlarmManager   = null;
    private PendingIntent MyPendingIntent = null;
    private Context     MyContext;
    private Calendar    MyCalendar;
    private String      alarm_time;
    private boolean is_alarm_active = false;

    public AlarmHandler(Context myContext)
    {
        MyContext       = myContext;
    }

    public boolean isAlarmActive()
    {
        return is_alarm_active;
    }

    public void setBroadcastRebootCommand(Intent intent)
    {
       intent.setAction("com.sagiadinos.garlic.launcher");
       intent.putExtra("command", "reboot1");
       MyPendingIntent = PendingIntent.getBroadcast(MyContext, 0, intent, 0);
    }

    public String getAlarmTime()
    {
        return alarm_time;
    }

    public void activateExactAlarm(Calendar myCalendar, String at)
    {
        alarm_time = at;
        setAlarmManager();
        MyCalendar = myCalendar;
        setAlarmCalendarTime(alarm_time);
        long tmp = MyCalendar.getTimeInMillis();
        MyAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                tmp,
                MyPendingIntent);
        is_alarm_active = true;
    }

    public void cancelAlarm()
    {
        MyAlarmManager.cancel(MyPendingIntent);
        is_alarm_active = false;
    }

    private void setAlarmManager()
    {
        if (MyAlarmManager == null)
            MyAlarmManager  = (AlarmManager) MyContext.getSystemService(Context.ALARM_SERVICE);
    }

    private void setAlarmCalendarTime(String alarm_time)
    {
        String[] time_data = alarm_time.split(":");

        if (time_data.length != 2)
        {
            Log.w(TAG, "no valid alarm time");
        }

        int hour   = Integer.parseInt(time_data[0]);
        int minute = Integer.parseInt(time_data[1]);
        long current = System.currentTimeMillis();
        MyCalendar.setTimeInMillis(current);
        MyCalendar.set(Calendar.HOUR_OF_DAY, hour);
        MyCalendar.set(Calendar.MINUTE, minute);
        MyCalendar.set(Calendar.SECOND, 0);
        MyCalendar.set(Calendar.MILLISECOND, 0);

        if (current >= MyCalendar.getTimeInMillis())
            MyCalendar.add(Calendar.HOUR_OF_DAY, 24);

    }

}
