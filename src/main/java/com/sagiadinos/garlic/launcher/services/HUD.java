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

package com.sagiadinos.garlic.launcher.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sagiadinos.garlic.launcher.R;

import java.io.IOException;

/**
 * creates overlay back button
 *
 * Necessary when you a have a custom Android image with deactivated navigation bar
 * It is intended to prevent a user from being stranded in an activity,
 * e.g. a second app, and not being able to return to the launcher.
 *
 * Attention: Needs root rights, which is suboptimal.
 *
 * We strongly recommended to use native API functionality with  SYSTEM_UI_FLAG_IMMERSIVE_STICKY
 * described in: https://developer.android.com/training/system-ui/immersive
 *
 */
public class HUD extends Service
{
    private ImageView overlayPowerBtn;

    private WindowManager windowManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Starts the button overlay.
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayPowerBtn = new ImageView(this);
        overlayPowerBtn.setImageResource(R.drawable.back);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // APPLICATION_OVERLAY FOR ANDROID 26+ AS THE PREVIOUS VERSION RAISES ERRORS
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else
        {
            // FOR PREVIOUS VERSIONS USE TYPE_PHONE AS THE NEW VERSION IS NOT SUPPORTED
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.height = 64;
        params.width = 64;
        params.x = 10;
        params.y = 10;

        windowManager.addView(overlayPowerBtn, params);

        overlayPowerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                try
                {
                    Runtime.getRuntime().exec(new String[]{"su","-c","input keyevent KEYCODE_BACK"});
                }
                catch (IOException e)
                {
                    e.getStackTrace();
                }
            }

        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
            if (overlayPowerBtn != null)
                windowManager.removeView(overlayPowerBtn);
    }
}
