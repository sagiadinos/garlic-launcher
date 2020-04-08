package com.sagiadinos.garlic.launcher.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;

public class SharedPreferencesModel
{
    private SharedPreferences pref;
    private String error_text = "";
    public SharedPreferencesModel(Context c)
    {
        pref  = c.getSharedPreferences(DeviceOwner.LAUNCHER_PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public void storeString(String param, String value)
    {
        try
        {
            SharedPreferences.Editor ed = pref.edit();
            ed.putString(param, value);
            commit(ed);
        }
        catch (GarlicLauncherException e)
        {
            // Todo insert some log functionality
            error_text = e.getMessage();
        }
    }

    public String getErrorText()
    {
        return error_text;
    }

    public String getString(String param)
    {
        return pref.getString(param, null);
    }

    public void storeBoolean(String param, boolean value)
    {
        try
        {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean(param, value);
            commit(ed);
        }
        catch (GarlicLauncherException e)
        {
            error_text = e.getMessage();
        }
    }

    public boolean getBoolean(String param)
    {
        return pref.getBoolean(param, false);
    }

    private void commit(SharedPreferences.Editor ed) throws GarlicLauncherException
    {
        if (!ed.commit())
        {
            throw new GarlicLauncherException("commit of SharedPreferences failed");
        }
    }

}
