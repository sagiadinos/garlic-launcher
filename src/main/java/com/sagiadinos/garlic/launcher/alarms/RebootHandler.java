package com.sagiadinos.garlic.launcher.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import java.util.Calendar;
import java.util.HashSet;

public class RebootHandler
{
    private final AlarmManager MyAlarmManager;
    private boolean is_alarm_active = false;
    private final Context CommandReceiverCtx;
    private final Intent MyCreatorIntent;
    private final MainConfiguration MyMainConfiguration;

    public RebootHandler(AlarmManager alarmManager, MainConfiguration mainConfiguration, Intent creatorIntent, Context CommandReceiverContext)
    {
        MyAlarmManager       = alarmManager;
        MyCreatorIntent      = creatorIntent;
        MyMainConfiguration  = mainConfiguration;
        CommandReceiverCtx   = CommandReceiverContext;

        MyCreatorIntent.setAction("com.sagiadinos.garlic.launcher");
        MyCreatorIntent.putExtra("command", "reboot");
        MyCreatorIntent.putExtra("task_id", "Reboot via AlarmManager");    }

    public boolean isAlarmActive()
    {
        return is_alarm_active;
    }

    public boolean hasChanged(MainConfiguration MyMainConfiguration)
    {
        if (!MyMainConfiguration.getRebootDays().equals(MyMainConfiguration.getActiveRebootDays()))
            return true;

        if (!MyMainConfiguration.getRebootTime().equals(MyMainConfiguration.getActiveRebootTime()))
            return true;

        return false;
    }

    public void activateAllAlarms()
    {
        for (String week_day : MyMainConfiguration.getRebootDays())
        {
            System.out.println("Processing day: " + week_day);
            MyAlarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    determineTimeInMilliSeconds(week_day),
                    setBroadcastRebootCommand(week_day)
            );
        }

        is_alarm_active = true;
        MyMainConfiguration.storeActiveRebootDays(MyMainConfiguration.getRebootDays());
        MyMainConfiguration.storeActiveRebootTime(MyMainConfiguration.getRebootTime());
    }

    public void cancelAllAlarms()
    {
        for (String day : MyMainConfiguration.getActiveRebootDays())
        {
            MyAlarmManager.cancel(setBroadcastRebootCommand(day));
        }
        MyMainConfiguration.storeActiveRebootDays(new HashSet<>());
        is_alarm_active = false;
    }

    private PendingIntent setBroadcastRebootCommand(String day)
    {
        return PendingIntent.getBroadcast(CommandReceiverCtx, Integer.parseInt(day), MyCreatorIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private long determineTimeInMilliSeconds(String week_day)
    {
        Calendar MyCalendar = Calendar.getInstance();
        String[] time_data  = splitTimeSecure();

        // set time for current day
        MyCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_data[0]));
        MyCalendar.set(Calendar.MINUTE, Integer.parseInt(time_data[1]));
        MyCalendar.set(Calendar.SECOND, 0);
        MyCalendar.set(Calendar.MILLISECOND, 0);

        // AlarmManager executes alarms immediately when they are in the past
        // To avoid this we set passed weekdays for the coming week
        MyCalendar.add(Calendar.DATE, determineAlarmWeekDay(MyCalendar, week_day));

        return MyCalendar.getTimeInMillis();
    }

    private int determineAlarmWeekDay(Calendar MyCalendar, String week_day)
    {
        // if negative, means day is in past. So, Add 7 days to declare next weekday
        int today           = MyCalendar.get(Calendar.DAY_OF_WEEK);
        int target_day      = Integer.parseInt(week_day);
        int days_difference = target_day - today;
        if (days_difference < 0 || (days_difference == 0 && MyCalendar.getTimeInMillis() < System.currentTimeMillis()))
            days_difference += 7;

        return days_difference;
    }

    private String[] splitTimeSecure()
    {
        String[] time_data = MyMainConfiguration.getRebootTime().split(":");
        if (time_data.length != 2)
        {
            time_data[0] = "3";
            time_data[1] = "00";
        }
        return time_data;
    }

}