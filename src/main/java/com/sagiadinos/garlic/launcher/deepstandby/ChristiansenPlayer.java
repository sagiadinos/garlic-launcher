package com.sagiadinos.garlic.launcher.deepstandby;

import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import java.util.Calendar;

public class ChristiansenPlayer extends AbstractBaseStandby
{
    final int min_wakeup_seconds = 241;
    int wakeup_seconds = min_wakeup_seconds;
    public ChristiansenPlayer(MainConfiguration myMainConfiguration, Context myCtx)
    {
        super(myMainConfiguration, myCtx);
    }

    @Override
    public void setSecondsToWakup(int seconds)
    {
        wakeup_seconds = Math.max(seconds, min_wakeup_seconds);
    }

    @Override
    public void executeStandby()
    {
        setPowerOffTime();
        setPowerOnTime();
    }

    private void setPowerOffTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 121); // we need to shutdown a minute ahead
        String SET_POWEROFF_ACTION = "rk.android.turnofftime.action";
        Intent intent_powerOff = new Intent(SET_POWEROFF_ACTION);
        intent_powerOff.putExtra("offHour", calendar.get(Calendar.HOUR_OF_DAY)+"");
        intent_powerOff.putExtra("offMin", calendar.get(Calendar.MINUTE)+"");
        intent_powerOff.putExtra("offWeek", calendar.get(Calendar.DAY_OF_WEEK)+"");
        intent_powerOff.putExtra("enable", true);
        MyCtx.sendBroadcast(intent_powerOff);
    }

    private void setPowerOnTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, wakeup_seconds);

        String SET_POWERON_ACTION = "rk.android.turnontime.action";
        Intent intent_powerOn = new Intent(SET_POWERON_ACTION);
        intent_powerOn.putExtra("turnonhour", calendar.get(Calendar.HOUR_OF_DAY)+"");
        intent_powerOn.putExtra("turnonmin", calendar.get(Calendar.MINUTE)+"");
        intent_powerOn.putExtra("onWeek", calendar.get(Calendar.DAY_OF_WEEK)+"");
        intent_powerOn.putExtra("turnonenable", true);
        MyCtx.sendBroadcast(intent_powerOn);
    }

}
