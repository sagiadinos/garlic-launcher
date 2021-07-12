package com.sagiadinos.garlic.launcher.receiver;

import android.content.BroadcastReceiver;
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
import com.sagiadinos.garlic.launcher.helper.TaskExecutionReport;
import com.sagiadinos.garlic.launcher.helper.WiFi;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigXMLReceiver extends BroadcastReceiver
{
    Context ctx;
    NetworkData MyNetWorkData = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ctx = context;
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        File config_xml = new File(Objects.requireNonNull(intent.getStringExtra("config_path")));
        String task_id = "";
        if (intent.getStringExtra("task_id") != null)
        {
            task_id        = intent.getStringExtra("task_id");
        }

        if (parseConfigFile(config_xml))
        {
            Intent i = new Intent("com.sagiadinos.garlic.launcher.receiver.CommandReceiver");
            i.putExtra("command", "reboot");
            i.putExtra("task_id", task_id);
            ctx.sendBroadcast(i);
        }
        else
        {
            TaskExecutionReport.append(task_id, "aborted");

        }
    }

    private boolean parseConfigFile(File file)
    {
        boolean ret = false;
        try
        {
            // parse configFile first for Wifi content Url etc...
            MyNetWorkData                   = new NetworkData();
            ConfigXMLModel MyConfigXMLModel = new ConfigXMLModel(MyNetWorkData, new MainConfiguration(new SharedPreferencesModel(ctx)));
            MyConfigXMLModel.parseConfigXml(MyConfigXMLModel.readConfigXml(file));

            if (!MyNetWorkData.getWifiSSID().isEmpty())
            {
                configWiFi();
            }

            if (MyConfigXMLModel.getVolume().contains("%"))
            {
                configAudioVolume(MyConfigXMLModel.getVolume());
            }
            // Todo: set time zone

            // Todo: find a way to check if system app
            // this works only when system app
/*            if (MyConfigXMLModel.getBrightness().contains("%"))
            {
              //  int brightness = (int) (255f * convertPercent(MyConfigXMLModel.getBrightness()));
               // Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
               // Settings.System.putInt(ctx.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
            }
*/
            ret = true;
        }
        catch (IOException e)
        {
            Log.e("Usb config_xml", Objects.requireNonNull(e.getMessage()));
        }
        return ret;
    }

    private void configWiFi()
    {
        WiFi MyWiFi = new WiFi((WifiManager) ctx.getSystemService(Context.WIFI_SERVICE), new WifiConfiguration());
        MyWiFi.prepareConnection(MyNetWorkData);
        MyWiFi.connectToNetwork();
    }

    private void configAudioVolume(String percent)
    {
        AudioManager audio = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        assert audio != null;
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int) (maxVolume * convertPercent(percent));
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    private float convertPercent(String value)
    {
        return Float.parseFloat(value.substring(0, value.length()-1)) / 100f;
    }

}
