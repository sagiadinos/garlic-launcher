package com.sagiadinos.garlic.launcher.helper.configxml;

import android.os.Environment;
import android.util.Log;

import com.sagiadinos.garlic.launcher.helper.PlayerDownload;
import com.sagiadinos.garlic.launcher.helper.SharedConfiguration;

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

public class ConfigXMLModel
{
    private String smil_index_url   = "http://indexes.smil-control.com";
    private NetworkData MyNetworkData = null;
    private SharedConfiguration MySharedConfiguration = null;

    public ConfigXMLModel(NetworkData myNetworkData, SharedConfiguration mySharedConfiguration)
    {
        this.MyNetworkData        = myNetworkData;
        this.MySharedConfiguration = mySharedConfiguration;
    }

    public void setSmilIndexUrl(String smil_index_url)
    {
        MySharedConfiguration.writeSmilIndex(smil_index_url);
        this.smil_index_url = smil_index_url;
    }

    public String readConfigXml(File config_xml)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(config_xml));
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[10];
            while (reader.read(buffer) != -1)
            {
                stringBuilder.append(new String(buffer));
                buffer = new char[10];
            }
            reader.close();
            return stringBuilder.toString();
        }
        catch (IOException e)
        {
            Log.e("config_xml", e.getMessage());
            return "";
        }
    }

    public void parseConfigXml(String xml)
    {
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(xml));
            int eventType = xpp.getEventType();
            String attribute_name = "";
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG)
                {
                    if (xpp.getName().equals("prop"))
                    {
                        attribute_name = xpp.getAttributeValue(null, "name");

                        if (attribute_name.equals("net.wifi.ssid"))
                            MyNetworkData.setWifiSSID(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.wifi.authentication"))
                            MyNetworkData.setWifiAuthentication(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.wifi.encryption"))
                            MyNetworkData.setWifiEncryption(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.wifi.password"))
                            MyNetworkData.setWifiPassword(xpp.getAttributeValue(null, "value"));

                        else if (attribute_name.equals("net.ethernet.ip"))
                            MyNetworkData.setEthernetIP(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.ethernet.dhcp.enabled"))
                            MyNetworkData.setEthernetDHCP(Boolean.parseBoolean(xpp.getAttributeValue(null, "value")));
                        else if (attribute_name.equals("net.ethernet.netmask"))
                            MyNetworkData.setEthernetNetmask(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.ethernet.gateway"))
                            MyNetworkData.setEthernetGateway(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.ethernet.domain"))
                            MyNetworkData.setEthernetDomain(xpp.getAttributeValue(null, "value"));
                        else if (attribute_name.equals("net.ethernet.dnsServers"))
                            MyNetworkData.setEthernetDNS(xpp.getAttributeValue(null, "value"));

                        else if (attribute_name.equals("content.serverUrl"))
                        {
                            setSmilIndexUrl(xpp.getAttributeValue(null, "value"));
                        }

                    }
                }
                eventType = xpp.next();
            }
        }
        catch (XmlPullParserException e)
        {
            Log.e("config_xml", e.getMessage());
        }
        catch (IOException e)
        {
            Log.e("config_xml", e.getMessage());
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
            Log.e("config_xml", e.getMessage());
        }
    }


    private String generateConfigXML()
    {
        return "<configuration>\n" +
                "\t<userPref>\n" +
                "\t\t<prop name=\"content.bootFromServer\" value=\"true\"/>\n" +
                "\t\t<prop name=\"content.serverUrl\" value=\""+  smil_index_url +"\"/>\n" +
                "\t</userPref>\n" +
                "</configuration>";
    }

}
