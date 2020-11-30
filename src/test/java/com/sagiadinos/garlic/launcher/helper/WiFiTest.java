package com.sagiadinos.garlic.launcher.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.sagiadinos.garlic.launcher.configuration.NetworkData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WiFiTest
{
    @Mock
    WifiManager WifiManagerMocked;
    @Mock
    WifiConfiguration WifiConfigurationMocked;

    @AfterEach
    void tearDown()
    {
        WifiManagerMocked       = null;
        WifiConfigurationMocked = null;
    }

    @Test
    void prepareConnectionWPA2PSK()
    {
        WiFi MyTestClass = createClass();
        NetworkData NetworkDataMocked = mock(NetworkData.class);;

        BitSet  BitSetMocked1 = mock(BitSet.class);
        BitSet  BitSetMocked2 = mock(BitSet.class);
        BitSet  BitSetMocked3 = mock(BitSet.class);

        when(NetworkDataMocked.getWifiAuthentication()).thenReturn("WPA2PSK");
        WifiConfigurationMocked.allowedGroupCiphers    = BitSetMocked1;
        WifiConfigurationMocked.allowedPairwiseCiphers = BitSetMocked2;
        WifiConfigurationMocked.allowedKeyManagement   = BitSetMocked3;

        MyTestClass.prepareConnection(NetworkDataMocked);

        verify(WifiManagerMocked, times(1)).addNetwork(WifiConfigurationMocked);
        verify(NetworkDataMocked, times(1)).getWifiPassword();

        verify(BitSetMocked1, times(1)).set(WifiConfiguration.GroupCipher.TKIP);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.PairwiseCipher.TKIP);
        verify(BitSetMocked1, times(1)).set(WifiConfiguration.GroupCipher.CCMP);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.PairwiseCipher.CCMP);
        verify(BitSetMocked3, times(1)).set(WifiConfiguration.KeyMgmt.WPA_PSK);
    }

    @Test
    void prepareConnectionWEPAuto()
    {
        WiFi MyTestClass = createClass();
        NetworkData NetworkDataMocked = mock(NetworkData.class);;

        BitSet  BitSetMocked1 = mock(BitSet.class);
        BitSet  BitSetMocked2 = mock(BitSet.class);
        String[] StringArray  = {"one", "two"};

        when(NetworkDataMocked.getWifiAuthentication()).thenReturn("WEPAUTO");
        when(WifiManagerMocked.addNetwork(WifiConfigurationMocked)).thenReturn(1); // succeed
        WifiConfigurationMocked.allowedKeyManagement = BitSetMocked1;
        WifiConfigurationMocked.allowedGroupCiphers  = BitSetMocked2;
        WifiConfigurationMocked.wepKeys             = StringArray;

        MyTestClass.prepareConnection(NetworkDataMocked);

        verify(WifiManagerMocked, times(1)).addNetwork(WifiConfigurationMocked);
        verify(NetworkDataMocked, times(1)).getWifiPassword();

        verify(BitSetMocked1, times(1)).set(WifiConfiguration.KeyMgmt.NONE);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.AuthAlgorithm.OPEN);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.AuthAlgorithm.SHARED);
    }


    @Test
    void prepareConnectionWEPSucceed()
    {
        WiFi MyTestClass = createClass();
        NetworkData NetworkDataMocked = mock(NetworkData.class);;

        BitSet  BitSetMocked1 = mock(BitSet.class);
        BitSet  BitSetMocked2 = mock(BitSet.class);
        String[] StringArray  = {"one", "two"};

        when(NetworkDataMocked.getWifiAuthentication()).thenReturn("WEP");
        when(WifiManagerMocked.addNetwork(WifiConfigurationMocked)).thenReturn(1); // succeed
        WifiConfigurationMocked.allowedKeyManagement = BitSetMocked1;
        WifiConfigurationMocked.allowedGroupCiphers  = BitSetMocked2;
        WifiConfigurationMocked.wepKeys             = StringArray;

        MyTestClass.prepareConnection(NetworkDataMocked);

        verify(WifiManagerMocked, times(1)).addNetwork(WifiConfigurationMocked);
        verify(NetworkDataMocked, times(1)).getWifiPassword();

        verify(BitSetMocked1, times(1)).set(WifiConfiguration.KeyMgmt.NONE);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.AuthAlgorithm.OPEN);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.AuthAlgorithm.SHARED);
   }

    @Test
    void prepareConnectionWEPFails()
    {
        WiFi MyTestClass = createClass();
        NetworkData NetworkDataMocked = mock(NetworkData.class);;

        BitSet  BitSetMocked1 = mock(BitSet.class);
        BitSet  BitSetMocked2 = mock(BitSet.class);
        String[] StringArray  = {"one", "two"};

        when(NetworkDataMocked.getWifiAuthentication()).thenReturn("WEP");
        when(WifiManagerMocked.addNetwork(WifiConfigurationMocked)).thenReturn(-1); // succeed
        WifiConfigurationMocked.allowedKeyManagement = BitSetMocked1;
        WifiConfigurationMocked.allowedGroupCiphers  = BitSetMocked2;
        WifiConfigurationMocked.wepKeys             = StringArray;

        MyTestClass.prepareConnection(NetworkDataMocked);

        verify(WifiManagerMocked, times(2)).addNetwork(WifiConfigurationMocked);
        verify(NetworkDataMocked, times(2)).getWifiPassword();

        verify(BitSetMocked1, times(1)).set(WifiConfiguration.KeyMgmt.NONE);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.AuthAlgorithm.OPEN);
        verify(BitSetMocked2, times(1)).set(WifiConfiguration.AuthAlgorithm.SHARED);
    }

    @Test
    void prepareConnectionWithUnkown()
    {
        WiFi MyTestClass = createClass();
        NetworkData NetworkDataMocked = mock(NetworkData.class);;

        BitSet  BitSetMocked1 = mock(BitSet.class);
        BitSet  BitSetMocked2 = mock(BitSet.class);
        String[] StringArray  = {"one", "two"};

        when(NetworkDataMocked.getWifiAuthentication()).thenReturn("not supported"); // unknows WiFi protocol

        MyTestClass.prepareConnection(NetworkDataMocked);

        verify(WifiManagerMocked, times(0)).addNetwork(WifiConfigurationMocked);
        verify(NetworkDataMocked, times(0)).getWifiPassword();
    }

    @Test
    void connectToNetworkNullAndEnable()
    {
        WiFi MyTestClass = createClass();
        when(WifiManagerMocked.isWifiEnabled()).thenReturn(false);
        when(WifiManagerMocked.getConfiguredNetworks()).thenReturn(null);
        MyTestClass.connectToNetwork();

        verify(WifiManagerMocked, times(1)).setWifiEnabled(true);
    }

    @Test
    void connectToNetworkNullAndIsAlreadyEnabled()
    {
        WiFi MyTestClass = createClass();
        when(WifiManagerMocked.isWifiEnabled()).thenReturn(true);
        when(WifiManagerMocked.getConfiguredNetworks()).thenReturn(null);
        MyTestClass.connectToNetwork();
        verify(WifiManagerMocked, times(0)).setWifiEnabled(true);
    }

    @Test
    void connectToNetworkSucceed()
    {
        WiFi MyTestClass = createClass();
        when(WifiManagerMocked.isWifiEnabled()).thenReturn(false);

        WifiConfigurationMocked.SSID = "A_SSID";
        WifiConfigurationMocked.networkId = 12;
        List<WifiConfiguration> ListMocked = new ArrayList<>();
        ListMocked.add(WifiConfigurationMocked);

        when(WifiManagerMocked.getConfiguredNetworks()).thenReturn(ListMocked);

        MyTestClass.connectToNetwork();

        verify(WifiManagerMocked, times(1)).setWifiEnabled(true);

        verify(WifiManagerMocked, times(1)).disconnect();
        verify(WifiManagerMocked, times(1)).enableNetwork(WifiConfigurationMocked.networkId, true);
        verify(WifiManagerMocked, times(1)).reconnect();
    }

    WiFi createClass()
    {
        WifiManagerMocked       = mock(WifiManager.class);
        WifiConfigurationMocked = mock(WifiConfiguration.class);
        return new WiFi(WifiManagerMocked, WifiConfigurationMocked);
    }
}