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

import com.sagiadinos.garlic.launcher.MainActivity;

public class LockTaskManager
{
    private final MainActivity MyMainActivity;


    public LockTaskManager(MainActivity ma)
    {
        MyMainActivity = ma;
    }

    public void startLockTask()
    {
       MyMainActivity.startLockTask();
    }

    public void stopLockTask()
    {
        MyMainActivity.stopLockTask();
    }

}
