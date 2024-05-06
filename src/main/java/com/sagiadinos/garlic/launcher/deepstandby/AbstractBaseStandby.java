package com.sagiadinos.garlic.launcher.deepstandby;

import android.content.Context;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

public abstract class AbstractBaseStandby
{
    protected MainConfiguration MyMainConfiguration;
    protected Context  MyCtx;


    public AbstractBaseStandby(MainConfiguration myMainConfiguration, Context myCtx)
    {
        MyMainConfiguration = myMainConfiguration;
        MyCtx = myCtx;
    }

    public abstract void setSecondsToWakup(int seconds);

    public abstract void executeStandby();
}
