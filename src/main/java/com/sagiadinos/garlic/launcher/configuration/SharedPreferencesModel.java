package com.sagiadinos.garlic.launcher.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;

public class SharedPreferencesModel
{
    private SharedPreferences pref;

    public SharedPreferencesModel(Context c)
    {
            final String APP_KEY             = "GARLIC_LAUNCHER_HEIDEWITZKA_DER_KAPITAEN";
            pref  = c.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
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
            e.printStackTrace();
        }
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
            e.printStackTrace();
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
            throw new GarlicLauncherException("commit SharedPreferences failed");
        }

    }

}
