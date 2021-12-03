package com.sagiadinos.garlic.launcher.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;

public class NumberPickerDialog extends DialogFragment
{
    MainConfiguration MyMainConfiguration;
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        MyMainConfiguration      = new MainConfiguration(new SharedPreferencesModel(getActivity()));

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(5);
        numberPicker.setMaxValue(300);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Value");
        builder.setMessage("Choose a number :");
        numberPicker.setValue(MyMainConfiguration.getPlayerStartDelay());
        builder.setPositiveButton("Store", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                valueChangeListener.onValueChange(numberPicker, numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });

        builder.setView(numberPicker);
        builder.create();
        return builder.show();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener()
    {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener)
    {
        this.valueChangeListener = valueChangeListener;
    }
}