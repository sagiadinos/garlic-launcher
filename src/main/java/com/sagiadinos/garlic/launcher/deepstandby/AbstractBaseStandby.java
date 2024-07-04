package com.sagiadinos.garlic.launcher.deepstandby;

import android.content.Context;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

public abstract class AbstractBaseStandby
{
    final int min_wakeup_seconds = 600;
    int wakeup_seconds = min_wakeup_seconds;

    protected MainConfiguration MyMainConfiguration;
    protected Context  MyCtx;

    public AbstractBaseStandby(MainConfiguration myMainConfiguration, Context myCtx)
    {
        MyMainConfiguration = myMainConfiguration;
        MyCtx = myCtx;
    }

    public void setSecondsToWakup(int seconds)
    {
        wakeup_seconds = Math.max(seconds, min_wakeup_seconds);
    }

    public abstract void executeStandby();
}
