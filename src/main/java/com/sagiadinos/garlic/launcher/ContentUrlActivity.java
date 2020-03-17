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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.sagiadinos.garlic.launcher.helper.SharedConfiguration;
import com.sagiadinos.garlic.launcher.helper.configxml.ConfigXMLModel;

public class ContentUrlActivity extends Activity
{
    private EditText ed_content_url;
    SharedConfiguration MySharedConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MySharedConfiguration = new SharedConfiguration(this);
        setContentView(R.layout.activity_content_url);
        ed_content_url = findViewById(R.id.editContentUrl);
        ed_content_url.setText(MySharedConfiguration.getSmilIndex(""));
    }

    public void setContentUrl(View view)
    {
        ConfigXMLModel MyConfigXMLModel = new ConfigXMLModel(null, MySharedConfiguration);
        if (MyConfigXMLModel.storeSmilIndexUrl(ed_content_url.getText().toString().trim()))
        {
            MyConfigXMLModel.storeConfigXmlForPlayer();
        }
    }

    public void closeActivity(View view)
    {
        finish();
    }


}
