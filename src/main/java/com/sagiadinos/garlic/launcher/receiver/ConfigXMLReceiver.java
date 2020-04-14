package com.sagiadinos.garlic.launcher.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.sagiadinos.garlic.launcher.configuration.ConfigXMLModel;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.NetworkData;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.WiFi;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigXMLReceiver extends BroadcastReceiver
{
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ctx = context;
        if (intent == null || intent.getAction() == null)
        {
            return;
        }
        // otherwise it can crash beacause we
        // try to do things which do not work without device owner rights
        if (!DeviceOwner.isDeviceOwner((DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE)))
        {
            return;
        }
        String path = intent.getStringExtra("config_path");
        File config_xml = new File(path + "/config.xml");

        parseConfigFile(config_xml);

        ctx.sendBroadcast(new Intent("com.sagiadinos.garlic.launcher.receiver.RebootReceiver"));
    }


    private void parseConfigFile(File file)
    {
        try
        {
            // parse configFile first for Wifi content Url etc...
            NetworkData MyNetWorkData = new NetworkData();
            ConfigXMLModel MyConfigXMLModel = new ConfigXMLModel(MyNetWorkData, new MainConfiguration(new SharedPreferencesModel(ctx)));
            String xml = MyConfigXMLModel.readConfigXml(file);
            MyConfigXMLModel.parseConfigXml(xml);
            WiFi MyWiFi = new WiFi((WifiManager) ctx.getSystemService(Context.WIFI_SERVICE), new WifiConfiguration());

            MyWiFi.prepareConnection(MyNetWorkData);
            MyWiFi.connectToNetwork();

            // Todo later: set time and time zone

            if (MyConfigXMLModel.getVolume().contains("%"))
            {
                AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                assert audio != null;
                int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float percent = convertPercent(MyConfigXMLModel.getVolume());
                int volume = (int) (maxVolume * percent);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }
            // Todo: find a way to check if system app
            // this works only when system app
/*            if (MyConfigXMLModel.getBrightness().contains("%"))
            {
              //  int brightness = (int) (255f * convertPercent(MyConfigXMLModel.getBrightness()));
               // Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
               // Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            }
*/
        }
        catch (IOException e)
        {
            Log.e("Usb config_xml", Objects.requireNonNull(e.getMessage()));
        }
    }

    private float convertPercent(String value)
    {
        return Float.parseFloat(value.substring(0, value.length()-1)) / 100f;
    }

}
