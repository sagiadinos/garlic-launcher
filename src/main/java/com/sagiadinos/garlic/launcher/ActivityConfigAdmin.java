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

package com.sagiadinos.garlic.launcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.dialogs.NumberPickerDialog;
import com.sagiadinos.garlic.launcher.dialogs.TimePickerDlg;
import com.sagiadinos.garlic.launcher.helper.AppPermissions;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;
import com.sagiadinos.garlic.launcher.configuration.PasswordHasher;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.services.HUD;

import java.util.Objects;

public class ActivityConfigAdmin extends Activity implements NumberPicker.OnValueChangeListener
{
    TextView tvInformation;
    TextView editPlayerStartDelay;
    CheckBox cbOwnBackButton;
    CheckBox cbRebootAfterInstall;
    CheckBox cbNoPlayerStartDelayAfterBoot;
    CheckBox cbActiveServicePassword;
    EditText editServicePassword;
    EditText editContentUrl;
    Boolean  is_password_changed = false;
    MainConfiguration MyMainConfiguration;
    AppPermissions MyAppPermissions;
    int player_delay = 5;
    String reboot_time = "3:00";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_config_admin);
        cbOwnBackButton          = findViewById(R.id.cbOwnBackButton);
        cbActiveServicePassword  = findViewById(R.id.cbActiveServicePassword);
        editServicePassword      = findViewById(R.id.editServicePassword);
        tvInformation            = findViewById(R.id.textViewInformation);
        editContentUrl           = findViewById(R.id.editContentUrl);
        editPlayerStartDelay     = findViewById(R.id.editPlayerStartDelay);

        MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(this));
        MyAppPermissions         = new AppPermissions(this, MyMainConfiguration);
        editContentUrl.setText(MyMainConfiguration.getSmilIndex());
        player_delay             = MyMainConfiguration.getPlayerStartDelay();

        String str = String.format(getString(R.string.start_delay_text), String.valueOf(player_delay));

        editPlayerStartDelay.setText(str);

        cbRebootAfterInstall          = findViewById(R.id.cbRebootAfterInstall);
        cbRebootAfterInstall.setChecked(MyMainConfiguration.hasRebootAfterInstall());
        cbNoPlayerStartDelayAfterBoot = findViewById(R.id.cbNoPlayerStartDelayAfterBoot);
        cbNoPlayerStartDelayAfterBoot.setChecked(MyMainConfiguration.hasNoPlayerStartDelayAfterBoot());
        prepareOptionsVisibility();
   }

    public void saveAndClose(View view)
    {
        hideErrorText();

        try
        {
            checkServicePassword();
            toggleOwnBackButton();
            storeNewPlayerStartDelay();
            MyMainConfiguration.toogleRebootAfterInstall(cbRebootAfterInstall.isChecked());
            MyMainConfiguration.toggleNoPlayerStartDelayAfterBoot(cbNoPlayerStartDelayAfterBoot.isChecked());
            MyMainConfiguration.storeSmilIndex(editContentUrl.getText().toString().trim());
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

    private void toggleOwnBackButton()
    {
        MyMainConfiguration.toggleOwnBackButton(cbOwnBackButton.isChecked());
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


    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1)
    {
        player_delay = i;
    }

    public void onClickPlayerStartDelay(View view)
    {
        NumberPickerDialog newFragment = new NumberPickerDialog();
        newFragment.setValueChangeListener(this);
        newFragment.show(getFragmentManager(), "number picker");
    }

    private void storeNewPlayerStartDelay()
    {
        if (player_delay < 5)
        {
            player_delay = 5;
        }
        MyMainConfiguration.storePlayerStartDelay(player_delay);
    }

    private void prepareOptionsVisibility()
    {
        prepareVisibilityOfBackButtonOption();
        prepareVisibilityOfServicePasswordOption();
    }

    private void prepareVisibilityOfBackButtonOption()
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

        if (MyMainConfiguration.isDeviceRooted() && MyAppPermissions.verifyOverlayPermissions(intent))
        {
            cbOwnBackButton.setVisibility(View.VISIBLE);
            cbOwnBackButton.setEnabled(true);
            cbOwnBackButton.setChecked(MyMainConfiguration.hasOwnBackButton());

        }
        else
        {
            cbOwnBackButton.setEnabled(false);
            cbOwnBackButton.setVisibility(View.GONE);
        }
    }

    private void prepareVisibilityOfServicePasswordOption()
    {
        cbActiveServicePassword.setChecked(MyMainConfiguration.hasActiveServicePassword());

        prepareVisibilityOfEditServicePassword(MyMainConfiguration.hasActiveServicePassword());
    }

    public void prepareVisibilityOfEditServicePassword(Boolean is_checked)
    {
        if (is_checked)
        {
            editServicePassword.setVisibility(View.VISIBLE);
            editServicePassword.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s){}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    is_password_changed = true;
                }
            });
        }
        else
        {
            editServicePassword.setVisibility(View.GONE);
        }

    }

    private void checkServicePassword() throws GarlicLauncherException
    {
        String password = editServicePassword.getText().toString();

        if (cbActiveServicePassword.isChecked() && is_password_changed && password.isEmpty())
        {
            throw new GarlicLauncherException(getString(R.string.missing_password));
        }

        MyMainConfiguration.toggleActiveServicePassword(cbActiveServicePassword.isChecked());
        if (is_password_changed)
        {
            MyMainConfiguration.setServicePassword(password, new PasswordHasher());
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
