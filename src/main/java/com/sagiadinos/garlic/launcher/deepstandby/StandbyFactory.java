package com.sagiadinos.garlic.launcher.deepstandby;

import android.content.Context;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

public class StandbyFactory
{

    MainConfiguration MyMainConfiguration;
    Context MyCtx;

    public StandbyFactory(MainConfiguration myMainConfiguration, Context myCtx)
    {
        MyMainConfiguration = myMainConfiguration;
        MyCtx = myCtx;
    }

    public AbstractBaseStandby determinePlayerModel()
    {
        String device_name = android.os.Build.MODEL;
        if (device_name.startsWith("BT-") && MyMainConfiguration.isDeviceRooted())
        {
            return new ChristiansenPlayer(MyMainConfiguration, MyCtx);
        }

        return null;
    }

}
