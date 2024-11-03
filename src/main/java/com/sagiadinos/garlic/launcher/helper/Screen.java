package com.sagiadinos.garlic.launcher.helper;

import android.util.DisplayMetrics;

public class Screen
{
    DisplayMetrics MyDisplayMetrics;
    private static final int REQUIRED_CLICKS = 10;
    private int                 click_count    = 0;
    private int                 activate_x     = 0;
    private int                 activate_y     = 0;

    public Screen(DisplayMetrics myDisplayMetrics)
    {
        MyDisplayMetrics = myDisplayMetrics;
        int display_height = MyDisplayMetrics.heightPixels;
        int display_width = MyDisplayMetrics.widthPixels;

        double area_factor = 0.9;
        activate_y       = (int) (display_height * area_factor);
        activate_x       = (int) (display_width * area_factor);
    }

    public boolean isEventInPermitUIArea(int x, int y)
    {
        if (x < activate_x && y < activate_y)
            return false;

        click_count++;
        if (click_count < REQUIRED_CLICKS)
            return false;

        click_count = 0;

        return true;
    }
}
