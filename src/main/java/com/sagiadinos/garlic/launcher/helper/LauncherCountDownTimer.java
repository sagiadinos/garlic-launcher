package com.sagiadinos.garlic.launcher.helper;

import android.os.CountDownTimer;

import com.sagiadinos.garlic.launcher.MainActivity;
import com.sagiadinos.garlic.launcher.R;

public class LauncherCountDownTimer
{
    private final MainActivity MyMainActivity;
    private CountDownTimer MyCountDownTimer;
    private boolean is_running = false;
    private long startTimeMillis;
    private long intervalMillis;

    public LauncherCountDownTimer(MainActivity myMainActivity)
    {
        this.MyMainActivity  = myMainActivity;
    }

    public void setCountDown(long startTimeMillis, long intervalMillis)
    {
        this.startTimeMillis = startTimeMillis;
        this.intervalMillis  = intervalMillis;
   }

    public void start()
    {
        if (is_running)
            return;

        is_running = true;
        MyCountDownTimer = new CountDownTimer(startTimeMillis, intervalMillis)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                String s = MyMainActivity.getString(R.string.count_down, String.valueOf(millisUntilFinished / 1000));
                MyMainActivity.setBtStartPlayerText(s);
            }

            @Override
            public void onFinish()
            {
                is_running = false;
                MyMainActivity.setBtStartPlayerText(MyMainActivity.getString(R.string.play));
                MyMainActivity.startGarlicPlayerInstantly(null);
            }
        }.start();
    }

    public void cancel()
    {
        if (MyCountDownTimer != null)
        {
            MyCountDownTimer.cancel();
            is_running = false;
        }
    }

    public boolean isRunning()
    {
        return is_running;
    }
}
