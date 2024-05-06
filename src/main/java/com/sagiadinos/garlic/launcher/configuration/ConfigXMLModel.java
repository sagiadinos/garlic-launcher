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

import android.os.Environment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigXMLModel
{
    private NetworkData MyNetworkData;
    private MainConfiguration MyMainConfiguration;
    private int volume = 100;
    private int brightness = 100;
    private String standby_mode = "";
    private Set<String> reboot_days;
    private String reboot_time = "";

    public ConfigXMLModel(NetworkData myNetworkData, MainConfiguration myMainConfiguration)
    {
        this.MyNetworkData        = myNetworkData;
        this.MyMainConfiguration = myMainConfiguration;
    }

    public int getVolume()
    {
        return volume;
    }

    public int getBrightness()
    {
        return brightness;
    }

    public String getStandbyMode()
    {
        return standby_mode;
    }

    public Set<String> getRebootDays()
    {

        return reboot_days;
    }

    public String getRebootTime()
    {
        return reboot_time;
    }

    public String readConfigXml(File config_xml) throws IOException
    {
        BufferedReader reader        = new BufferedReader(new FileReader(config_xml));
        StringBuilder  stringBuilder = new StringBuilder();
        char[]         buffer        = new char[10];
        while (reader.read(buffer) != -1)
        {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();
        return stringBuilder.toString();
    }

    public void parseConfigXml(String xml)
    {
        try
        {
            XmlPullParser xpp = createXmlPullParser();
            xpp.setInput(new StringReader(xml));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("prop"))
                {
                    parseProp(xpp);
                }
                eventType = xpp.next();
            }
        }
        catch (XmlPullParserException | IOException e)
        {
            Log.e("config_xml", Objects.requireNonNull(e.getMessage()));
        }

    }

    public void copy(File src, File dst) throws IOException
    {
        try (InputStream in = new FileInputStream(src))
        {
            try (OutputStream out = new FileOutputStream(dst))
            {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0)
                {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public void storeConfigXmlForPlayer()
    {
        try
        {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/config.xml");

            if (!file.exists() && !file.createNewFile())
            {
                throw new IOException("File could not be created");
            }
            FileWriter writer = new FileWriter(file);
            writer.write(generateConfigXML());
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            Log.e("config_xml", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void parseProp(XmlPullParser xpp)
    {
        switch (xpp.getAttributeValue(null, "name"))
        {
            case "net.wifi.ssid":
                MyNetworkData.setWifiSSID(xpp.getAttributeValue(null, "value"));
                break;
            case "net.wifi.authentication":
                MyNetworkData.setWifiAuthentication(xpp.getAttributeValue(null, "value"));
                break;
            case "net.wifi.encryption":
                MyNetworkData.setWifiEncryption(xpp.getAttributeValue(null, "value"));
                break;
            case "net.wifi.password":
                MyNetworkData.setWifiPassword(xpp.getAttributeValue(null, "value"));
                break;
            case "net.ethernet.ip":
                MyNetworkData.setEthernetIP(xpp.getAttributeValue(null, "value"));
                break;
            case "net.ethernet.dhcp.enabled":
                MyNetworkData.setEthernetDHCP(Boolean.parseBoolean(xpp.getAttributeValue(null, "value")));
                break;
            case "net.ethernet.netmask":
                MyNetworkData.setEthernetNetmask(xpp.getAttributeValue(null, "value"));
                break;
            case "net.ethernet.gateway":
                MyNetworkData.setEthernetGateway(xpp.getAttributeValue(null, "value"));
                break;
            case "net.ethernet.domain":
                MyNetworkData.setEthernetDomain(xpp.getAttributeValue(null, "value"));
                break;
            case "audio.soundLevel":
            case "volume":
            case "hardware.audioOut.0.masterSoundLevel":
                String s = xpp.getAttributeValue(null, "value").replaceAll("%", "");
                volume = Integer.parseInt(s);
                break;
            case "display.brightness":
            case "brightness":
                String str = xpp.getAttributeValue(null, "value").replaceAll("%", "");
                brightness = Integer.parseInt(str);
                break;
            case "standby_mode":
                standby_mode = xpp.getAttributeValue(null, "value");
                break;
            case "schedule.reboot.time":
                reboot_time = xpp.getAttributeValue(null, "value");
                break;
            case "schedule.reboot.days":
                String[] tmp = xpp.getAttributeValue(null, "value").toLowerCase().trim().split(" ");
                reboot_days = new HashSet<>();
                reboot_days.addAll(Arrays.asList(tmp));
                break;
            case "net.ethernet.dnsServers":
                MyNetworkData.setEthernetDNS(xpp.getAttributeValue(null, "value"));
                break;
            case "content.serverUrl":
                MyMainConfiguration.storeSmilIndex(xpp.getAttributeValue(null, "value"));
                break;
            default:
                // do nothing on unsupported attributes
                break;
        }
    }

    private XmlPullParser createXmlPullParser() throws XmlPullParserException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newPullParser();
    }

    private String generateConfigXML()
    {
        String smil_index_url =  MyMainConfiguration.getSmilIndex().replace("&amp;", "&"); // make sure that there are no ampersands setted
        return "<configuration>\n" +
                "\t<userPref>\n" +
                "\t\t<prop name=\"content.bootFromServer\" value=\"true\"/>\n" +
                "\t\t<prop name=\"content.serverUrl\" value=\""+  smil_index_url.replaceAll("&", "&amp;") +"\"/>\n" +
                "\t</userPref>\n" +
                "</configuration>";
    }

}
