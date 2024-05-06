package com.sagiadinos.garlic.launcher.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferencesModel
{
    private SharedPreferences pref;
    private String error_text = "";

    public SharedPreferencesModel(Context c)
    {
        pref  = c.getSharedPreferences(DeviceOwner.LAUNCHER_PACKAGE_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasParameter(String key)
    {
        return pref.contains(key);
    }

    public void removeParameter(String key)
    {
        pref.edit().remove(key).commit();
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

    public String getString(String param, String the_default)
    {
        return pref.getString(param, the_default);
    }

    public int getInt(String param)
    {
        return pref.getInt(param, 0);
    }
    public int getInt(String param, int the_default){return pref.getInt(param, the_default);}

    public void storeInt(String param, int value)
    {
        try
        {
            SharedPreferences.Editor ed = pref.edit();
            ed.putInt(param, value);
            commit(ed);
        }
        catch (GarlicLauncherException e)
        {
            error_text = e.getMessage();
        }
    }

    public void storeStringSet(String param, Set<String> value)
    {
        try
        {
            SharedPreferences.Editor ed = pref.edit();
            ed.putStringSet(param, value);
            commit(ed);
        }
        catch (GarlicLauncherException e)
        {
            error_text = e.getMessage();
        }
    }

    public Set<String> getStringSet(String param)
    {
        return pref.getStringSet(param, new HashSet<>());
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

    public boolean getBoolean(String param, boolean the_default)
    {
        return pref.getBoolean(param, the_default);
    }

    private void commit(SharedPreferences.Editor ed) throws GarlicLauncherException
    {
        if (!ed.commit())
        {
            throw new GarlicLauncherException("commit of SharedPreferences failed");
        }
    }

}
