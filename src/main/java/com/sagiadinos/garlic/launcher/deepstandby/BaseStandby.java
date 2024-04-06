package com.sagiadinos.garlic.launcher.deepstandby;

import android.content.Context;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

public class BaseStandby implements StandByInterface
{
    protected MainConfiguration MyMainConfiguration;
    protected Context  MyCtx;



    public BaseStandby(MainConfiguration myMainConfiguration, Context myCtx)
    {
        MyMainConfiguration = myMainConfiguration;
        MyCtx = myCtx;
    }

    @Override
    public void setSecondsToWakeUp(int seconds) {

    }

    @Override
    public void executeStandby() {

    }
}
