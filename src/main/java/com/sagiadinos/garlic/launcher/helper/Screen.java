package com.sagiadinos.garlic.launcher.helper;

import android.util.DisplayMetrics;

public class Screen
{
    DisplayMetrics MyDisplayMetrics;
    private int                 click_count    = 0;
    private int                 display_height = 0;
    private int                 display_width  = 0;
    private int                 activate_x     = 0;
    private int                 activate_y     = 0;
    final private  double       area_factor    = 0.9;

    public Screen(DisplayMetrics myDisplayMetrics)
    {
        MyDisplayMetrics = myDisplayMetrics;
        display_height   = MyDisplayMetrics.heightPixels;
        display_width    = MyDisplayMetrics.widthPixels;

        activate_x       = (int) (display_height * area_factor);
        activate_x       = (int) (display_width  * area_factor);
    }

    public boolean isEventInPermitUIArea(int x, int y)
    {
        if (x < activate_x && y < activate_y)
            return false;

        click_count++;
        if (click_count < 10)
            return false;

        click_count = 0;

        return true;
    }
}
