package com.sagiadinos.garlic.launcher.configuration;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.receiver.AdminReceiver;

public class SystemSettings
{
    Context MyContext;

    public SystemSettings(Context myContext)
    {
        MyContext = myContext;
    }

    public void configMasterVolume(int percent)
    {
        AudioManager audio = (AudioManager) MyContext.getSystemService(Context.AUDIO_SERVICE);
        assert audio != null;
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int) (maxVolume * convertPercent(percent));
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    public void configBrightness(int percent)
    {
        int brightness = (int) (255 * convertPercent(percent));
        DeviceOwner.setScreenBrightness(
                (DevicePolicyManager) MyContext.getSystemService(Context.DEVICE_POLICY_SERVICE),
                new ComponentName(MyContext, AdminReceiver.class),
                brightness);
    }

    private float convertPercent(int value)
    {
        return (float) value / 100f;
    }

}
