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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ActivityConfigAdmin extends Activity implements NumberPicker.OnValueChangeListener, TimePickerDialog.OnTimeSetListener
{
    TextView tvInformation;
    TextView editPlayerStartDelay;
    CheckBox cbOwnBackButton;
    CheckBox cbRebootAfterInstall;
    CheckBox cbNoPlayerStartDelayAfterBoot;
    CheckBox cbActiveServicePassword;
    CheckBox cbToggleDailyReboot;
    CheckBox cbToggleMondayReboot;
    CheckBox cbToggleTuesdayReboot;
    CheckBox cbToggleWednesdayReboot;
    CheckBox cbToggleThursdayReboot;
    CheckBox cbToggleFridayReboot;
    CheckBox cbToggleSaturdayReboot;
    CheckBox cbToggleSundayReboot;

    Spinner  viewStandbyMode;
    TextView editRebootTime;
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
        MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(this));
        cbOwnBackButton          = findViewById(R.id.cbOwnBackButton);
        cbActiveServicePassword  = findViewById(R.id.cbActiveServicePassword);
        editServicePassword      = findViewById(R.id.editServicePassword);
        tvInformation            = findViewById(R.id.textViewInformation);
        editContentUrl           = findViewById(R.id.editContentUrl);
        editPlayerStartDelay     = findViewById(R.id.editPlayerStartDelay);
        cbToggleDailyReboot      = findViewById(R.id.cbToggleDailyReboot);
        cbToggleMondayReboot     = findViewById(R.id.cbToggleMondayReboot);
        cbToggleTuesdayReboot    = findViewById(R.id.cbToggleTuesdayReboot);
        cbToggleWednesdayReboot  = findViewById(R.id.cbToggleWednesdayReboot);
        cbToggleThursdayReboot   = findViewById(R.id.cbToggleThursdayReboot);
        cbToggleFridayReboot     = findViewById(R.id.cbToggleFridayReboot);
        cbToggleSaturdayReboot   = findViewById(R.id.cbToggleSaturdayReboot);
        cbToggleSundayReboot     = findViewById(R.id.cbToggleSundayReboot);

        viewStandbyMode          = findViewById(R.id.spinnerStandbyMode);
        List<String> items = new ArrayList<String>();
        for (MainConfiguration.STANDBY_MODE mode : MainConfiguration.STANDBY_MODE.values())
        {
            items.add(mode.toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        viewStandbyMode.setAdapter(adapter);
        viewStandbyMode.setSelection(adapter.getPosition(MyMainConfiguration.getStandbyMode()));

        editRebootTime           = findViewById(R.id.editRebootTime);
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
            storeDailyReboot();
            MyMainConfiguration.toogleRebootAfterInstall(cbRebootAfterInstall.isChecked());
            MyMainConfiguration.toggleNoPlayerStartDelayAfterBoot(cbNoPlayerStartDelayAfterBoot.isChecked());

            MyMainConfiguration.setStandbyMode(viewStandbyMode.getSelectedItem().toString());
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
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        reboot_time = hourOfDay + ":" + String.format("%02d", minute);
        String str = String.format(getString(R.string.reboot_time), reboot_time);
        editRebootTime.setText(str);
        MyMainConfiguration.storeRebootTime(reboot_time);
    }


    public void onClickRebootTime(View view)
    {
        TimePickerDlg newFragment = new TimePickerDlg();
        newFragment.show(getFragmentManager(), "time picker");
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1)
    {
        player_delay = i;
        prepareVisibilityOfDailyRebootOption();
    }

    public void onClickPlayerStartDelay(View view)
    {
        NumberPickerDialog newFragment = new NumberPickerDialog();
        newFragment.setValueChangeListener(this);
        newFragment.show(getFragmentManager(), "number picker");
    }

    public void toggleDailyReboot(View view)
    {
        handleDailyRebootOptionVisibility(cbToggleDailyReboot.isChecked());
    }

    private void prepareVisibilityOfDailyRebootOption()
    {
        boolean bo = MyMainConfiguration.hasDailyReboot();
        cbToggleDailyReboot.setChecked(bo);
        handleDailyRebootOptionVisibility(bo);
    }

    private void handleDailyRebootOptionVisibility(boolean bo)
    {
        if (bo)
        {
            makeDailyRebootOptionVisible();
        }
        else
        {
            makeDailyRebootOptionInVisible();
        }
    }

    private void makeDailyRebootOptionVisible()
    {
        editRebootTime.setVisibility(View.VISIBLE);
        String str = String.format(getString(R.string.reboot_time),  MyMainConfiguration.getRebootTime());
        editRebootTime.setText(str);

        Set<String> reboot_days = MyMainConfiguration.getRebootDays();
        cbToggleMondayReboot.setVisibility(View.VISIBLE);
        cbToggleTuesdayReboot.setVisibility(View.VISIBLE);
        cbToggleWednesdayReboot.setVisibility(View.VISIBLE);
        cbToggleThursdayReboot.setVisibility(View.VISIBLE);
        cbToggleFridayReboot.setVisibility(View.VISIBLE);
        cbToggleSaturdayReboot.setVisibility(View.VISIBLE);
        cbToggleSundayReboot.setVisibility(View.VISIBLE);

        cbToggleMondayReboot.setChecked(false);
        cbToggleTuesdayReboot.setChecked(false);
        cbToggleWednesdayReboot.setChecked(false);
        cbToggleThursdayReboot.setChecked(false);
        cbToggleFridayReboot.setChecked(false);
        cbToggleSaturdayReboot.setChecked(false);
        cbToggleSundayReboot.setChecked(false);
        for (String element : reboot_days)
        {
            if (element.equals("1"))
                cbToggleMondayReboot.setChecked(true);
            if (element.equals("2"))
                cbToggleTuesdayReboot.setChecked(true);
            if (element.equals("3"))
                cbToggleWednesdayReboot.setChecked(true);
            if (element.equals("4"))
                cbToggleThursdayReboot.setChecked(true);
            if (element.equals("5"))
                cbToggleFridayReboot.setChecked(true);
            if (element.equals("6"))
                cbToggleSaturdayReboot.setChecked(true);
            if (element.equals("7"))
                cbToggleSundayReboot.setChecked(true);
        }
    }

    private void makeDailyRebootOptionInVisible()
    {
        editRebootTime.setVisibility(View.GONE);
        cbToggleMondayReboot.setVisibility(View.GONE);
        cbToggleTuesdayReboot.setVisibility(View.GONE);
        cbToggleWednesdayReboot.setVisibility(View.GONE);
        cbToggleThursdayReboot.setVisibility(View.GONE);
        cbToggleFridayReboot.setVisibility(View.GONE);
        cbToggleSaturdayReboot.setVisibility(View.GONE);
        cbToggleSundayReboot.setVisibility(View.GONE);
    }

    private void storeDailyReboot()
    {
        boolean is_reboot = cbToggleDailyReboot.isChecked();
        Set<String> selectedDays = new HashSet<>();
        if (is_reboot)
        {
            if (cbToggleMondayReboot.isChecked())
                selectedDays.add("1");
            if (cbToggleTuesdayReboot.isChecked())
                selectedDays.add("2");
            if (cbToggleWednesdayReboot.isChecked())
                selectedDays.add("3");
            if (cbToggleThursdayReboot.isChecked())
                selectedDays.add("4");
            if (cbToggleFridayReboot.isChecked())
                selectedDays.add("5");
            if (cbToggleSaturdayReboot.isChecked())
                selectedDays.add("6");
            if (cbToggleSundayReboot.isChecked())
                selectedDays.add("7");
        }

        MyMainConfiguration.toggleDailyReboot(is_reboot);
        MyMainConfiguration.storeRebootDays(selectedDays);
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
        prepareVisibilityOfDailyRebootOption();
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
