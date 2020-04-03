package com.sagiadinos.garlic.launcher.helper.configxml;

import com.sagiadinos.garlic.launcher.configuration.ConfigXMLModel;
import com.sagiadinos.garlic.launcher.configuration.NetworkData;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ConfigXMLModelTest
{
    @Mock
    NetworkData NetworkDataMocked;
    @Mock
    MainConfiguration mainConfigurationMocked;

    @AfterEach
    void tearDown()
    {
        NetworkDataMocked = null;
        mainConfigurationMocked = null;
    }

    @Test
    void readConfigXmlWithExistingConfig() throws IOException
    {
        ConfigXMLModel MyModel = createModel();
        String result = MyModel.readConfigXml(new File("sampledata/files/config_wlan_dhcp.xml"));

        assertTrue(result.contains("<prop name=\"info.playerName\" value=\"Playername\"/>"));
        assertTrue(result.contains("<prop name=\"content.bootFromServer\" value=\"true\"/>"));
        assertTrue(result.contains("<prop name=\"content.serverUrl\" value=\"https://indexes.a-server.test/\"/>"));
        assertTrue(result.contains("<prop name=\"net.wifi.ssid\" value=\"THESSID\"/>"));
        assertTrue(result.contains("<prop name=\"net.wifi.authentication\" value=\"WPA2PSK\"/>"));
        assertTrue(result.contains("<prop name=\"net.wifi.encryption\" value=\"TKIP\"/>"));
        assertTrue(result.contains("<prop name=\"net.wifi.password\" value=\"secret_passwd\"/>"));
        assertTrue(result.contains("<prop name=\"net.ethernet.dhcp.enabled\" value=\"true\"/>"));
    }

    @Test
    void readConfigXmlWithMissingConfig()
    {
        final ConfigXMLModel MyModel = createModel();
        Locale.setDefault(new Locale("pt", "BR"));

        Exception exception = assertThrows(FileNotFoundException.class, new Executable()
        {
            @Override
            public void execute() throws Throwable
            {
                MyModel.readConfigXml(new File("no/existing/filepath"));
            }
        });

       // assertEquals("no/existing/filepath (Datei oder Verzeichnis nicht gefunden)", exception.getMessage());
        // Todo Try to find out who we get this idiotic system to throw error messages in english for testing
        assertTrue(Objects.requireNonNull(exception.getMessage()).contains("no/existing/filepath"));
    }


    @Test
    void parseConfigXml()
    {
    }

    @Test
    void copy()
    {
    }

    @Test
    void storeConfigXmlForPlayer()
    {
    }

    ConfigXMLModel createModel()
    {
        NetworkDataMocked         = mock(NetworkData.class);
        mainConfigurationMocked = mock(MainConfiguration.class);
        return new ConfigXMLModel(NetworkDataMocked, mainConfigurationMocked);
    }

}