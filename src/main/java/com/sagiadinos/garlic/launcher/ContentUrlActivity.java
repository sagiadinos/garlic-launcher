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
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************************/

package com.sagiadinos.garlic.launcher;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ContentUrlActivity extends Activity
{
    private EditText ed_content_url;
    private String config_file_path  = "";
    private SharedPreferences pref;
    final private String SMIL_INDEX_URI = "smil_index_uri";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final String APP_KEY             = "GARLIC_LAUNCHER_HEIDEWITZKA_DER_KAPTAEN";
        final String CONFIG_FILE_NAME    = "config.xml";
        final String DEFAULT_INDEX_URI_1 = "http://";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_url);
        ed_content_url = (EditText) findViewById(R.id.editContentUrl);

        pref = getSharedPreferences(APP_KEY, MODE_PRIVATE);


        ed_content_url.setText(pref.getString(SMIL_INDEX_URI, DEFAULT_INDEX_URI_1));

        config_file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + CONFIG_FILE_NAME;
    }

    public void setContentUrl(View view)
    {
        writeSharedPreferences();
        writeConfigXml(generateConfigXML());
    }

    public void closeActivity(View view)
    {
        finish();
    }

    private void writeSharedPreferences()
    {
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(SMIL_INDEX_URI, ed_content_url.getText().toString().trim());
        ed.apply();
    }

    private void writeConfigXml(String content)
    {
        try
        {
            File file = new File(config_file_path);

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
              "\t\t<prop name=\"content.serverUrl\" value=\""+ ed_content_url.getText() +"\"/>\n" +
              "\t</userPref>\n" +
              "</configuration>";
    }
}
