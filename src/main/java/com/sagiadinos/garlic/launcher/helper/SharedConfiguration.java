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

import android.content.Context;
import android.content.SharedPreferences;

public class SharedConfiguration
{
    private SharedPreferences pref;
    private Context ctx;
    public SharedConfiguration(Context c)
    {
        final String APP_KEY             = "GARLIC_LAUNCHER_HEIDEWITZKA_DER_KAPITAEN";
        pref  = c.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        ctx   = c;
    }

    public void writeSmilIndex(String smil_index) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString("smil_index_uri", smil_index);
        commit(ed);
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

    public boolean isPlayerInstalled()
    {
        return pref.getBoolean("is_player_installed", false);
    }

    public void togglePlayerInstalled(boolean value) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("is_player_installed", value);
        commit(ed);
    }

    public void setServicePassword(String cleartext__password, PasswordHasher MyPasswordHasher) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        String salt                = MyPasswordHasher.generateSalt();
        ed.putString("service_password_salt", salt);

        String hashed              = MyPasswordHasher.hashClearTextWithSalt(cleartext__password, salt);
        ed.putString("service_password_hash", hashed);

        commit(ed);
    }

    public boolean compareServicePassword(String cleartext__password, PasswordHasher MyPasswordHasher)
    {
        String salt                = pref.getString("service_password_salt", "");
        String hashed              = MyPasswordHasher.hashClearTextWithSalt(cleartext__password, salt);

        return (hashed.equals(pref.getString("service_password_hash", "")));
    }


    public boolean isDeviceRooted()
    {
        return pref.getBoolean("is_device_rooted", false);
    }

    public void setIsDeviceRooted(Boolean value) throws GarlicLauncherException
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("is_device_rooted", value);
        commit(ed);
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
