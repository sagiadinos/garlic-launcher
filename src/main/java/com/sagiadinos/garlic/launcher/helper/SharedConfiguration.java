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

import android.content.Context;
import android.content.SharedPreferences;

public class SharedConfiguration
{
    private SharedPreferences pref;

    public SharedConfiguration(Context ctx)
    {
        final String APP_KEY             = "GARLIC_LAUNCHER_HEIDEWITZKA_DER_KAPITAEN";
        pref  = ctx.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
    }

    public boolean writeSmilIndex(String smil_index)
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString("smil_index_uri", smil_index);
        return ed.commit();
    }

    public String getSmilIndex(String default_value)
    {
        return pref.getString("smil_index_uri", default_value);
    }

    public void toggleOwnBackButton(boolean value) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("own_back_button", value);
        commit(ed);
    }

    public boolean hasOwnBackButton()
    {
        return pref.getBoolean("own_back_button", false);
    }

    public void toggleActiveServicePassword(boolean value) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("active_service_password", value);
        commit(ed);
    }

    public boolean hasActiveServicePassword()
    {
        return pref.getBoolean("active_service_password", false);
    }

    public boolean isStrictKioskModeActive()
    {
        return pref.getBoolean("is_strict_kiosk_mode", false);
    }


    public void setServicePassword(String service_mode_password) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString("service_mode_password", service_mode_password);
        commit(ed);
    }

    public String getServicePassword()
    {
        return pref.getString("service_mode_password", "");
    }

    public void setStrictKioskMode(boolean value) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("is_strict_kiosk_mode", value);
        commit(ed);
    }


    private void commit(SharedPreferences.Editor ed) throws GarlicLauncherException
    {
        if (!ed.commit())
        {
            throw new GarlicLauncherException("commit SharedPreferences failed");
        }

    }
}
