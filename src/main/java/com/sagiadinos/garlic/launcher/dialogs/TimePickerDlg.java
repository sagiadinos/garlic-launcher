package com.sagiadinos.garlic.launcher.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;

import java.util.Calendar;

public class TimePickerDlg extends DialogFragment
{
    MainConfiguration MyMainConfiguration;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(getActivity()));
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

}
