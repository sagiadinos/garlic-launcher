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

package com.sagiadinos.garlic.launcher.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import com.sagiadinos.garlic.launcher.MainActivity;

import org.jetbrains.annotations.NotNull;

public class AdminReceiver extends DeviceAdminReceiver
{
    @Override
    public void onEnabled(@NotNull Context ctx, @NotNull Intent intent)
    {
        // restart MainActivity
        Intent a = createIntent(ctx);
        a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(a);
    }

    /**
     * This method is factory like for testing this class with Mockitos spy
     * So we avoid using tools like PowerMock and adding unnecessary complexity
     * @look AdminReceiverTest
     *
     * @param ctx
     * @return Intent
     */
    protected Intent createIntent(Context ctx)
    {
        return new Intent(ctx, MainActivity.class);
    }
}
