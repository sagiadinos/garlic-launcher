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

package com.sagiadinos.garlic.launcher.configuration;

import android.annotation.SuppressLint;

import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;
import com.sagiadinos.garlic.launcher.helper.PasswordHasher;

import java.util.UUID;

public class MainConfiguration
{
    private SharedPreferencesModel Model;
    public MainConfiguration(SharedPreferencesModel model)
    {
        this.Model = model;
    }

    @SuppressLint("ApplySharedPref")
    public void checkForUUID()
    {
        if (getUUID() == null)
        {
            Model.storeString("uuid", UUID.randomUUID().toString());
        }
    }

    public void writeSmilIndex(String smil_index)
    {
        Model.storeString("smil_index_uri", smil_index);
    }

    public String getSmilIndex()
    {
        return Model.getString("smil_index_uri");
    }

    public String getUUID()
    {
        return Model.getString("uuid");
    }

    public void toggleOwnBackButton(boolean value)
    {
        Model.storeBoolean("own_back_button", value);
    }

    public boolean hasOwnBackButton()
    {
        return Model.getBoolean("own_back_button");
    }

    public void toggleActiveServicePassword(boolean value)
    {
        Model.storeBoolean("active_service_password", value);
    }

    public boolean hasActiveServicePassword()
    {
        return Model.getBoolean("active_service_password");
    }

    public boolean isStrictKioskModeActive()
    {
        return Model.getBoolean("is_strict_kiosk_mode");
    }

    public boolean isPlayerInstalled()
    {
        return Model.getBoolean("is_player_installed");
    }

    public void togglePlayerInstalled(boolean value)
    {
        Model.storeBoolean("is_player_installed", value);
    }

    public void setServicePassword(String cleartext__password, PasswordHasher MyPasswordHasher)
    {
        String salt                = MyPasswordHasher.generateSalt();
        Model.storeString("service_password_salt", salt);

        String hashed              = MyPasswordHasher.hashClearTextWithSalt(cleartext__password, salt);
        Model.storeString("service_password_hash", hashed);
    }

    public boolean compareServicePassword(String cleartext__password, PasswordHasher MyPasswordHasher)
    {
        String salt                = Model.getString("service_password_salt");
        String hashed              = MyPasswordHasher.hashClearTextWithSalt(cleartext__password, salt);

        return (hashed.equals(Model.getString("service_password_hash")));
    }


    public boolean isDeviceRooted()
    {
        return Model.getBoolean("is_device_rooted");
    }

    public void setIsDeviceRooted(Boolean value)
    {
        Model.storeBoolean("is_device_rooted", value);
    }


    public void setStrictKioskMode(boolean value)
    {
        Model.storeBoolean("is_strict_kiosk_mode", value);
    }

  }
