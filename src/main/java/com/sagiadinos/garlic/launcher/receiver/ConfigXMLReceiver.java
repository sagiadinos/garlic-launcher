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
    Context MyContext;
    NetworkData MyNetWorkData = null;
    MainConfiguration MyMainConfiguration;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent == null || intent.getAction() == null)
        {
            return;
        }

        MyContext           = context;
        MyMainConfiguration = new MainConfiguration(new SharedPreferencesModel(MyContext));


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
            MyContext.sendBroadcast(i);
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
            ConfigXMLModel MyConfigXMLModel = new ConfigXMLModel(MyNetWorkData, new MainConfiguration(new SharedPreferencesModel(MyContext)));
            MyConfigXMLModel.parseConfigXml(MyConfigXMLModel.readConfigXml(file));

            if (!MyNetWorkData.getWifiSSID().isEmpty())
            {
                configWiFi();
            }

            MyMainConfiguration.storeRebootTime(MyConfigXMLModel.getRebootTime());
            MyMainConfiguration.storeRebootDays(MyConfigXMLModel.getRebootDays());
            MyMainConfiguration.storeVolume(MyConfigXMLModel.getVolume());
            MyMainConfiguration.storeBrightness(MyConfigXMLModel.getBrightness());


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
        WiFi MyWiFi = new WiFi((WifiManager) MyContext.getSystemService(Context.WIFI_SERVICE), new WifiConfiguration());
        MyWiFi.prepareConnection(MyNetWorkData);
        MyWiFi.connectToNetwork();
    }

}
