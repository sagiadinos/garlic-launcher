/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-launcher source code

 This program is free software: you can redistribute it and/or  modify
 it under the terms of the GNU Affero General Public License, version 3,
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sagiadinos.garlic.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 *  Unfortunately subclassing Application class with ActivityLifecycleCallbacks implemented
 *  seems to be the only efficient and "correct" way to determine if an activity of this
 *  application is in foreground or not.
 */
public class GarlicLauncherApplication extends Application implements Application.ActivityLifecycleCallbacks
{

    private WeakReference<Context> foreground_activity;

    @Override
    public void onCreate()
    {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NotNull Activity activity, Bundle bundle)
    {
        // had to be declared
    }

    @Override
    public void onActivityStarted(@NotNull Activity activity)
    {
        // had to be declared
    }

    @Override
    public void onActivityStopped(@NotNull Activity activity)
    {
        // had to be declared
    }

    @Override
    public void onActivitySaveInstanceState(@NotNull Activity activity, @NotNull Bundle bundle)
    {
        // had to be declared
    }

    @Override
    public void onActivityDestroyed(@NotNull Activity activity)
    {
        // had to be declared
    }

    @Override
    public void onActivityResumed(@NotNull Activity activity)
    {
        foreground_activity = new WeakReference<Context>(activity);
    }

    @Override
    public void onActivityPaused(Activity activity)
    {
        String class_name_activity = activity.getClass().getCanonicalName();
        if (foreground_activity != null &&
                Objects.equals(foreground_activity.get().getClass().getCanonicalName(), class_name_activity))
        {
            foreground_activity = null;
        }
    }

    public boolean isOnForeground()
    {
        return (foreground_activity != null);
    }
}