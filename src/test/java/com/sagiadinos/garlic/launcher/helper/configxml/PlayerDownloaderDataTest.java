package com.sagiadinos.garlic.launcher.helper.configxml;

import com.sagiadinos.garlic.launcher.configuration.NetworkData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reamrk:
 * Normaly testing getter setter classes is ridiculous, but this was the
 * easiest introduction to Android testing. ;-)
 */
class PlayerDownloaderDataTest
{

    @Test
    void getWifiSSID()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "The secret ssid";
        MyNetworkdata.setWifiSSID(expected);
        assertEquals(expected,  MyNetworkdata.getWifiSSID());
    }

    @Test
    void getWifiAuthentication()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "WPA2";
        MyNetworkdata.setWifiAuthentication(expected);
        assertEquals(expected,  MyNetworkdata.getWifiAuthentication());
    }

    @Test
    void getWifiEncryption()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "TKIP";
        MyNetworkdata.setWifiEncryption(expected);
        assertEquals(expected,  MyNetworkdata.getWifiEncryption());
    }

    @Test
    void getWifiPassword()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "the totally secret passwd";
        MyNetworkdata.setWifiPassword(expected);
        assertEquals(expected,  MyNetworkdata.getWifiPassword());
    }

    @Test
    void getWifiIP()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "192.168.12.2";
        MyNetworkdata.setWifiIP(expected);
        assertEquals(expected,  MyNetworkdata.getWifiIP());
    }

    @Test
    void getWifiNetmask()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "255.255.255.0";
        MyNetworkdata.setWifiNetmask(expected);
        assertEquals(expected,  MyNetworkdata.getWifiNetmask());
    }

    @Test
    void getWifiGateway()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "192.168.12.1";
        MyNetworkdata.setWifiGateway(expected);
        assertEquals(expected,  MyNetworkdata.getWifiGateway());
    }

    @Test
    void getWifiDomain()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "garlic-launcher.test";
        MyNetworkdata.setWifiDomain(expected);
        assertEquals(expected,  MyNetworkdata.getWifiDomain());
    }

    @Test
    void getWifiDNS()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "8.8.8.8";
        MyNetworkdata.setWifiDNS(expected);
        assertEquals(expected,  MyNetworkdata.getWifiDNS());
    }

    @Test
    void isWifiDHCP()
    {
    }

    @Test
    void isEthernetDHCP()
    {
        NetworkData MyNetworkdata = new NetworkData();

        MyNetworkdata.setEthernetDHCP(true);
        assertTrue(MyNetworkdata.isEthernetDHCP());

        MyNetworkdata.setEthernetDHCP(false);
        assertFalse(MyNetworkdata.isEthernetDHCP());
    }

    @Test
    void getEthernetIP()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "192.168.12.100";
        MyNetworkdata.setEthernetIP(expected);
        assertEquals(expected,  MyNetworkdata.getEthernetIP());
    }

    @Test
    void getEthernetNetmask()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "255.255.255.255";
        MyNetworkdata.setEthernetNetmask(expected);
        assertEquals(expected,  MyNetworkdata.getEthernetNetmask());
    }

    @Test
    void getEthernetGateway()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "192.168.12.1";
        MyNetworkdata.setEthernetGateway(expected);
        assertEquals(expected,  MyNetworkdata.getEthernetGateway());
    }

    @Test
    void getEthernetDomain()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "sub.garlic-launcher.test";
        MyNetworkdata.setEthernetDomain(expected);
        assertEquals(expected,  MyNetworkdata.getEthernetDomain());
    }

    @Test
    void getEthernetDNS()
    {
        NetworkData MyNetworkdata = new NetworkData();
        String expected = "8.8.8.8";
        MyNetworkdata.setEthernetDNS(expected);
        assertEquals(expected,  MyNetworkdata.getEthernetDNS());
    }
}