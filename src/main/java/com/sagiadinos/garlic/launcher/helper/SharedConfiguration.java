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
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *************************************************************************************/
package com.sagiadinos.garlic.launcher.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedConfiguration
{
    private SharedPreferences pref;
    final private String SMIL_INDEX_URI      = "smil_index_uri";

    public SharedConfiguration(Context ctx)
    {
        final String APP_KEY             = "GARLIC_LAUNCHER_HEIDEWITZKA_DER_KAPITAEN";
        pref  = ctx.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
    }

    public boolean writeSmilIndex(String smil_index)
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(SMIL_INDEX_URI, smil_index);
        return ed.commit();
    }

    public String getSmilIndex(String default_value)
    {
        return pref.getString(SMIL_INDEX_URI, default_value);
    }

}
