package com.sagiadinos.garlic.launcher.helper;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.sagiadinos.garlic.launcher.helper.configxml.NetworkData;

import java.util.ArrayList;

import java.util.List;

public class WiFi
{
    private WifiManager MyWiFiManager      = null;
    private WifiConfiguration MyWifiConfig = null;
    private NetworkData MyNetWorkData = null;
    public WiFi(WifiManager myWiFiManager, WifiConfiguration myWifiConfig)
    {
        MyWiFiManager = myWiFiManager;
        MyWifiConfig = myWifiConfig;
    }

    public void prepareConnection(NetworkData network_data)
    {
        MyNetWorkData = network_data;
        MyWifiConfig.SSID = "\"" + MyNetWorkData.getWifiSSID() + "\"";   // Please note the quotes. String should contain SSID in quotes
        if (MyNetWorkData.getWifiAuthentication().equals("WPA2PSK"))
        {
            prepareWPA();
        }
        else if (MyNetWorkData.getWifiAuthentication().equals("WEPAUTO") || MyNetWorkData.getWifiAuthentication().equals("WEP"))
        {
            prepareWep();
        }
    }

    public void connectToNetwork()
    {
        enableWifi();
        List<WifiConfiguration> list = MyWiFiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list )
        {
            if(i.SSID != null && i.SSID.equals( MyWifiConfig.SSID ))
            {
                MyWiFiManager.disconnect();
                MyWiFiManager.enableNetwork(i.networkId, true);
                MyWiFiManager.reconnect();
                break;
            }
        }
    }

    public List<String> scanSSID()
    {
        enableWifi();
        List<String> ssid_list = new ArrayList<>();
        List<ScanResult> scanResults = MyWiFiManager.getScanResults();
        if (scanResults == null)
        {
            return ssid_list;
        }
        for (ScanResult scanResult : scanResults)
        {
            ssid_list.add(scanResult.SSID);
        }
        return ssid_list;
    }

    private void prepareWPA()
    {
        MyWifiConfig.preSharedKey = "\"" + MyNetWorkData.getWifiPassword() + "\"";
        MyWifiConfig.status = WifiConfiguration.Status.ENABLED;
        MyWifiConfig.priority = 40;

        MyWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        MyWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        MyWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        MyWifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        MyWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        MyWiFiManager.addNetwork(MyWifiConfig);
    }

    private void prepareWep()
    {
        MyWifiConfig.wepKeys[0] = "\"" + MyNetWorkData.getWifiPassword() + "\"";
        MyWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        MyWifiConfig.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
        MyWifiConfig.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);
        int networkId = MyWiFiManager.addNetwork(MyWifiConfig);

        if (networkId == -1)
        {
            //Try it again with no quotes in case of hex password
            MyWifiConfig.wepKeys[0] = MyNetWorkData.getWifiPassword();
            networkId = MyWiFiManager.addNetwork(MyWifiConfig);
        }
    }

    private void enableWifi()
    {
        if(!MyWiFiManager.isWifiEnabled())
        {
            MyWiFiManager.setWifiEnabled(true);
        }
    }


}
