/*
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
 */
package com.sagiadinos.garlic.launcher.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.sagiadinos.garlic.launcher.HUD;

import java.security.Permission;

/**
 * class to handle the show/hide of the Navigation bar
 */
public class NavigationBar
{
    public static void hide(Activity a)
    {
        if (AppPermissions.isOverlayAllowed(a))
        {
            a.stopService(new Intent(a, HUD.class));
        }

        a.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.INVISIBLE);

    }

    public static void show(Activity a)
    {
        if (AppPermissions.isOverlayAllowed(a))
        {
            a.startService(new Intent(a, HUD.class));
        }
        a.getWindow().getDecorView()
            .setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }


}
