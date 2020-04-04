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

package com.sagiadinos.garlic.launcher.helper;


import android.widget.Toast;

import com.sagiadinos.garlic.launcher.BuildConfig;
import com.sagiadinos.garlic.launcher.MainActivity;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

/**
 * This class is responsible for methods which needed to create a Kiosk Mode
 * Kiosk Mode means:
 *
 * Standard Home-Button Activity! this App will become a system launcher and is set
 * LockTask/Pinning, No one can escape from the app.
 *
 */
public class KioskManager
{
    private DeviceOwner MyDeviceOwner;
    private HomeLauncherManager  MyLauncher;
    private LockTaskManager      MyLockTasks;
    private MainConfiguration MyMainConfiguration;

    public KioskManager(DeviceOwner dvo, HomeLauncherManager hlm, LockTaskManager ltm, MainConfiguration mc)
    {
        MyDeviceOwner       = dvo;
        MyLauncher          = hlm;
        MyLockTasks         = ltm;
        MyMainConfiguration = mc;
    }

    public boolean startKioskMode()
    {
        boolean ret = false;
        if (checkforDeviceRights())
        {
            MyLockTasks.startLockTask();
            MyLauncher.becomeHomeActivity();
            ret = true;
        }
        return ret;
  }

    public void toggleServiceMode(boolean value)
    {
        if (value)
        {
            MyMainConfiguration.setStrictKioskMode(false);
        }
        else
        {
            MyMainConfiguration.setStrictKioskMode(true);
        }
    }

    public boolean isStrictKioskModeActive()
    {
        return MyMainConfiguration.isStrictKioskModeActive();
    }

    /**
     * @return boolean returns the status of locktask
     */
    public boolean toggleKioskMode()
    {
        if (checkforDeviceRights())
        {
            return MyLockTasks.toggleLockTask();
        }
        return false;
    }

    public boolean isHomeActivity()
    {
        return MyLauncher.isHomeActivity();
    }

    public void becomeHomeActivity()
    {
        if (checkforDeviceRights())
        {
            MyLauncher.becomeHomeActivity();
        }
    }

    public boolean toggleHomeActivity()
    {
        if (checkforDeviceRights())
        {
           return MyLauncher.toggleHomeActivity();
        }
        return false;
    }

    private boolean checkforDeviceRights()
    {
        if (!MyDeviceOwner.isAdminActive())
        {
            return false;
        }
        if (!MyDeviceOwner.isDeviceOwner())
        {
            return false;
        }
        if (!MyDeviceOwner.isLockTaskPermitted())
        {
            return false;
        }
        return true;
    }


}