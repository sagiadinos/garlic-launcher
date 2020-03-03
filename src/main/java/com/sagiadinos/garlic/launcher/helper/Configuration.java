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
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration
{
    private SharedPreferences pref;
    final private String SMIL_INDEX_URI      = "smil_index_uri";

    public Configuration(Context ctx)
    {
        final String APP_KEY             = "GARLIC_LAUNCHER_HEIDEWITZKA_DER_KAPITAEN";
        pref  = ctx.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
    }

    public void writeSmilIndex(String smil_index)
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(SMIL_INDEX_URI, smil_index);
        ed.apply();
        writeConfigXml(generateConfigXML());
    }

    public String getSmilIndex(String default_value)
    {
        return pref.getString(SMIL_INDEX_URI, default_value);
    }

    private void writeConfigXml(String content)
    {
        try
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/config.xml");

            if (!file.exists() && !file.createNewFile())
            {
                throw new IOException("File could not be created");
            }
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            Log.e("config_xml", e.getMessage());
        }
    }

    private String generateConfigXML()
    {
        return "<configuration>\n" +
                "\t<userPref>\n" +
                "\t\t<prop name=\"content.bootFromServer\" value=\"true\"/>\n" +
                "\t\t<prop name=\"content.serverUrl\" value=\""+  getSmilIndex("") +"\"/>\n" +
                "\t</userPref>\n" +
                "</configuration>";
    }
}
