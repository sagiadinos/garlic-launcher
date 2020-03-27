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

package com.sagiadinos.garlic.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.helper.AppPermissions;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;
import com.sagiadinos.garlic.launcher.helper.SharedConfiguration;
import com.sagiadinos.garlic.launcher.services.HUD;

import java.util.Objects;

public class ActivityConfigAdmin extends Activity
{
    TextView tvInformation;
    CheckBox cbOwnBackButton;
    CheckBox cbActiveServicePassword;
    EditText editServicePassword;
    SharedConfiguration MySharedConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_admin);
        cbOwnBackButton          = findViewById(R.id.cbOwnBackButton);
        cbActiveServicePassword  = findViewById(R.id.cbActiveServicePassword);
        editServicePassword      = findViewById(R.id.editServicePassword);
        tvInformation            = findViewById(R.id.textViewInformation);

        MySharedConfiguration = new SharedConfiguration(this);
        try
        {
            MySharedConfiguration.setIsDeviceRooted(AppPermissions.isDeviceRooted());
        }
        catch (GarlicLauncherException e)
        {
            e.getStackTrace();
        }
        prepareOptionsVisibility();
    }

    public void saveAndClose(View view)
    {
        hideErrorText();

        try
        {
            checkServicePassword();
            MySharedConfiguration.toggleActiveServicePassword(cbActiveServicePassword.isChecked());
            MySharedConfiguration.setServicePassword(editServicePassword.getText().toString());
            MySharedConfiguration.toggleOwnBackButton(cbOwnBackButton.isChecked());
            toggleOwnBackButton(cbOwnBackButton.isChecked()); // activate BackButton
            finish();
        }
        catch (GarlicLauncherException e)
        {
            displayErrorText(Objects.requireNonNull(e.getMessage()));
        }

    }

    public void closeActivity(View view)
    {
        finish();
    }

    private void toggleOwnBackButton(Boolean checked)
    {
        if (cbOwnBackButton.isChecked())
        {
            startService(new Intent(this, HUD.class));
        }
        else
        {
            stopService(new Intent(this, HUD.class));
        }

    }

    public void onServicePassWordClicked(View view)
    {
        prepareVisibilityOfEditServicePassword(cbActiveServicePassword.isChecked());
    }


    private void prepareOptionsVisibility()
    {
        prepareVisibilityOfBackButtonOption();
        prepareVisibilityOfServicePasswordOption();

    }

    private void prepareVisibilityOfBackButtonOption()
    {
        if (AppPermissions.isDeviceRooted() && AppPermissions.verifyOverlayPermissions(this))
        {
            cbOwnBackButton.setVisibility(View.VISIBLE);
            cbOwnBackButton.setEnabled(true);
            cbOwnBackButton.setChecked(MySharedConfiguration.hasOwnBackButton());

        }
        else
        {
            cbOwnBackButton.setEnabled(false);
            cbOwnBackButton.setVisibility(View.GONE);
        }
    }

    private void prepareVisibilityOfServicePasswordOption()
    {
        cbActiveServicePassword.setChecked(MySharedConfiguration.hasActiveServicePassword());

        prepareVisibilityOfEditServicePassword(MySharedConfiguration.hasActiveServicePassword());
        if (MySharedConfiguration.hasActiveServicePassword())
        {
            editServicePassword.setText(MySharedConfiguration.getServicePassword());
        }
    }

    public void prepareVisibilityOfEditServicePassword(Boolean is_checked)
    {
        if (is_checked)
        {
            editServicePassword.setVisibility(View.VISIBLE);
        }
        else
        {
            editServicePassword.setVisibility(View.GONE);
        }

    }

    private void checkServicePassword() throws GarlicLauncherException
    {
        if (cbActiveServicePassword.isChecked() && editServicePassword.getText().toString().isEmpty())
        {
            throw new GarlicLauncherException(getString(R.string.missing_password));
        }
    }

    private void displayErrorText(String error_text)
    {
        tvInformation.setText(error_text);
        tvInformation.setVisibility(View.VISIBLE);
    }

    private void hideErrorText()
    {
        tvInformation.setText("");
        tvInformation.setVisibility(View.INVISIBLE);
    }

}
