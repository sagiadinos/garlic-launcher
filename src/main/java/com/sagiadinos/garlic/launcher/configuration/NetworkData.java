/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-launcher source code

 This program is free software: you can redistribute it and/or  modify
 it under the terms of the GNU Affero General Public License, version 3,
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.sagiadinos.garlic.launcher.configuration;

public class NetworkData
{
    private String wifi_ssid           = "";
    private String wifi_authentication = "WPA2PSK";
    private String wifi_encryption     = "TKIP";
    private String wifi_password       = "";
    private String wifi_ip         = "";
    private String wifi_netmask    = "255.255.255.0";
    private String wifi_gateway    = "";
    private String wifi_domain     = "";
    private String wifi_dns        = "";
	private boolean wifi_dhcp      = true;

    private boolean ethernet_dhcp      = true;
	private String ethernet_ip         = "";
    private String ethernet_netmask    = "255.255.255.0";
    private String ethernet_gateway    = "";
    private String ethernet_domain     = "";
    private String ethernet_dns        = "";

    public NetworkData()
    {
    }

    public String getWifiSSID()
    {
        return wifi_ssid;
    }

    public void setWifiSSID(String wifi_ssid)
    {
        this.wifi_ssid = wifi_ssid;
    }

    public String getWifiAuthentication()
    {
        return wifi_authentication;
    }

    public void setWifiAuthentication(String wifi_authentication)
    {
        this.wifi_authentication = wifi_authentication;
    }

    public String getWifiEncryption()
    {
        return wifi_encryption;
    }

    public void setWifiEncryption(String wifi_encryption)
    {
        this.wifi_encryption = wifi_encryption;
    }

    public String getWifiPassword()
    {
        return wifi_password;
    }

    public void setWifiPassword(String wifi_password)
    {
        this.wifi_password = wifi_password;
    }

    public String getWifiIP()
    {
        return wifi_ip;
    }

    public void setWifiIP(String wifi_ip)
    {
        this.wifi_ip = wifi_ip;
    }

    public String getWifiNetmask()
    {
        return wifi_netmask;
    }

    public void setWifiNetmask(String wifi_netmask)
    {
        this.wifi_netmask = wifi_netmask;
    }

    public String getWifiGateway()
    {
        return wifi_gateway;
    }

    public void setWifiGateway(String wifi_gateway)
    {
        this.wifi_gateway = wifi_gateway;
    }

    public String getWifiDomain()
    {
        return wifi_domain;
    }

    public void setWifiDomain(String wifi_domain)
    {
        this.wifi_domain = wifi_domain;
    }

    public String getWifiDNS()
    {
        return wifi_dns;
    }

    public void setWifiDNS(String wifi_dns)
    {
        this.wifi_dns = wifi_dns;
    }

    public boolean isWifiDHCP()
    {
        return wifi_dhcp;
    }

    public void setWifiDHCP(boolean wifi_dhcp)
    {
        this.wifi_dhcp = wifi_dhcp;
    }

    public boolean isEthernetDHCP()
    {
        return ethernet_dhcp;
    }

    public void setEthernetDHCP(boolean ethernet_dhcp)
    {
        this.ethernet_dhcp = ethernet_dhcp;
    }

    public String getEthernetIP()
    {
        return ethernet_ip;
    }

    public void setEthernetIP(String ethernet_ip)
    {
        this.ethernet_ip = ethernet_ip;
    }

    public String getEthernetNetmask()
    {
        return ethernet_netmask;
    }

    public void setEthernetNetmask(String ethernet_netmask)
    {
        this.ethernet_netmask = ethernet_netmask;
    }

    public String getEthernetGateway()
    {
        return ethernet_gateway;
    }

    public void setEthernetGateway(String ethernet_gateway)
    {
        this.ethernet_gateway = ethernet_gateway;
    }

    public String getEthernetDomain()
    {
        return ethernet_domain;
    }

    public void setEthernetDomain(String ethernet_domain)
    {
        this.ethernet_domain = ethernet_domain;
    }

    public String getEthernetDNS()
    {
        return ethernet_dns;
    }

    public void setEthernetDNS(String ethernet_dns)
    {
        this.ethernet_dns = ethernet_dns;
    }
}
