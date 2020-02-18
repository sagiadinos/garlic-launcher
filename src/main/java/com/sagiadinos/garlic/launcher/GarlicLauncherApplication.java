/*************************************************************************************
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-player source code

 This program is free software: you can redistribute it and/or  modify
 it under the terms of the GNU Affero General Public License, version 3,
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************************/

package com.sagiadinos.garlic.launcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;


import java.lang.ref.WeakReference;

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
    public void onActivityCreated(Activity activity, Bundle bundle)
    {
        // had to be declared
    }

    @Override
    public void onActivityStarted(Activity activity)
    {
        // had to be declared
    }

    @Override
    public void onActivityStopped(Activity activity)
    {
        // had to be declared
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {
        // had to be declared
    }

    @Override
    public void onActivityDestroyed(Activity activity)
    {
        // had to be declared
    }

    @Override
    public void onActivityResumed(Activity activity)
    {
        foreground_activity = new WeakReference<Context>(activity);
        if (BuildConfig.DEBUG)
            showToast("Launcher onActivityResumed");
    }

    @Override
    public void onActivityPaused(Activity activity)
    {
        String class_name_activity = activity.getClass().getCanonicalName();
        if (foreground_activity != null &&
                foreground_activity.get().getClass().getCanonicalName().equals(class_name_activity))
        {
            foreground_activity = null;
        }
        if (BuildConfig.DEBUG)
            showToast("Launcher onActivityPaused");
    }

    public boolean isOnForeground()
    {
        return (foreground_activity != null);
    }

    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}