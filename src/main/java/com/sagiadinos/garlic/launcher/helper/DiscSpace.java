package com.sagiadinos.garlic.launcher.helper;

import android.os.StatFs;

import java.io.File;

public class DiscSpace
{
    StatFs MyStats;

    long total = 0;
    long free  = 0;
    int  free_percent = 0;
    String cache_path;
    public DiscSpace(StatFs myStats, String cache_path)
    {
        MyStats         = myStats;
        this.cache_path = cache_path;
        determineFreeSpace();
    }

    public int getFreePercent()
    {
        return free_percent;
    }

    public void refresh()
    {
        MyStats.restat(cache_path);
        determineFreeSpace();
    }

    private void determineFreeSpace()
    {
        total = MyStats.getTotalBytes();
        free  = MyStats.getFreeBytes();
        free_percent = (int) (((100.0 / (double) total) * free));
    }
}
