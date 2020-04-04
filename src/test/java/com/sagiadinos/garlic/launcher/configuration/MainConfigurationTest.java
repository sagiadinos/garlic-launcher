package com.sagiadinos.garlic.launcher.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class MainConfigurationTest
{
    @Mock
    SharedPreferencesModel SharedPreferencesModelMocked;

    @AfterEach
    void tearDown()
    {
        SharedPreferencesModelMocked = null;
    }


    @Test
    void checkForUUIDWhenNull()
    {
        MainConfiguration MyMainConfiguration = createClass();
        when(SharedPreferencesModelMocked.getString("uuid")).thenReturn(null);

        MyMainConfiguration.checkForUUID();

        verify(SharedPreferencesModelMocked, times(1)).storeString(anyString(), anyString());

    }

    @Test
    void checkForUUIDWhenExists()
    {
        MainConfiguration MyMainConfiguration = createClass();
        when(SharedPreferencesModelMocked.getString("uuid")).thenReturn("a uuid");

        MyMainConfiguration.checkForUUID();

        verify(SharedPreferencesModelMocked, never()).storeString(anyString(), anyString());
    }


    @Test
    void writeSmilIndex()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.writeSmilIndex("an index");

        verify(SharedPreferencesModelMocked, times(1)).storeString("smil_index_uri", "an index");
    }

    @Test
    void getSmilIndex()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.getSmilIndex();

        verify(SharedPreferencesModelMocked, times(1)).getString("smil_index_uri");
    }

    @Test
    void getUUID()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.getUUID();

        verify(SharedPreferencesModelMocked, times(1)).getString("uuid");
    }

    @Test
    void toggleOwnBackButton()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.toggleOwnBackButton(true);

        verify(SharedPreferencesModelMocked, times(1)).storeBoolean("own_back_button", true);
    }

    @Test
    void hasOwnBackButton()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.hasOwnBackButton();

        verify(SharedPreferencesModelMocked, times(1)).getBoolean("own_back_button");
    }

    @Test
    void toggleActiveServicePassword()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.toggleActiveServicePassword(true);

        verify(SharedPreferencesModelMocked, times(1)).storeBoolean("active_service_password", true);
    }

    @Test
    void hasActiveServicePassword()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.hasActiveServicePassword();

        verify(SharedPreferencesModelMocked, times(1)).getBoolean("active_service_password");
    }

    @Test
    void isStrictKioskModeActive()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.isStrictKioskModeActive();

        verify(SharedPreferencesModelMocked, times(1)).getBoolean("is_strict_kiosk_mode");
    }

    @Test
    void isPlayerInstalled()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.isPlayerInstalled();

        verify(SharedPreferencesModelMocked, times(1)).getBoolean("is_player_installed");
    }

    @Test
    void togglePlayerInstalled()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.togglePlayerInstalled(false);

        verify(SharedPreferencesModelMocked, times(1)).storeBoolean("is_player_installed", false);

    }

    @Test
    void setServicePassword()
    {
        MainConfiguration MyMainConfiguration = createClass();

        PasswordHasher PasswordHasherMock = mock(PasswordHasher.class);

        String salt               = "a crazy salt";
        String cleartext_password = "a secret password";
        String hashed_password    = "heidewitzkaderkapitän";

        when(PasswordHasherMock.generateSalt()).thenReturn(salt);
        when(PasswordHasherMock.hashClearTextWithSalt(cleartext_password, salt)).thenReturn(hashed_password);

        InOrder order = inOrder(SharedPreferencesModelMocked);

        MyMainConfiguration.setServicePassword(cleartext_password, PasswordHasherMock);

        order.verify(SharedPreferencesModelMocked, times(1)).storeString("service_password_salt", salt);
        order.verify(SharedPreferencesModelMocked, times(1)).storeString("service_password_hash", hashed_password);
    }

    @Test
    void compareServicePasswordSucceed()
    {
        MainConfiguration MyMainConfiguration = createClass();

        PasswordHasher PasswordHasherMock = mock(PasswordHasher.class);

        String salt               = "a crazy salt";
        String cleartext_password = "a secret password";
        String hashed_password    = "heidewitzkaderkapitän";

        when(SharedPreferencesModelMocked.getString("service_password_salt")).thenReturn(salt);
        when(PasswordHasherMock.hashClearTextWithSalt(cleartext_password, salt)).thenReturn(hashed_password);
        when(SharedPreferencesModelMocked.getString("service_password_hash")).thenReturn(hashed_password);

        InOrder order = inOrder(SharedPreferencesModelMocked);

        assertTrue(MyMainConfiguration.compareServicePassword(cleartext_password, PasswordHasherMock));

        order.verify(SharedPreferencesModelMocked, times(1)).getString("service_password_salt");
        order.verify(SharedPreferencesModelMocked, times(1)).getString("service_password_hash");
    }

    @Test
    void compareServicePasswordFails()
    {
        MainConfiguration MyMainConfiguration = createClass();

        PasswordHasher PasswordHasherMock = mock(PasswordHasher.class);

        String salt               = "a crazy salt";
        String cleartext_password = "a secret password";

        when(SharedPreferencesModelMocked.getString("service_password_salt")).thenReturn(salt);
        when(PasswordHasherMock.hashClearTextWithSalt(cleartext_password, salt)).thenReturn("another hash");
        when(SharedPreferencesModelMocked.getString("service_password_hash")).thenReturn("differt hash");

        assertFalse(MyMainConfiguration.compareServicePassword(cleartext_password, PasswordHasherMock));
    }


    @Test
    void isDeviceRooted()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.isDeviceRooted();

        verify(SharedPreferencesModelMocked, times(1)).getBoolean("is_device_rooted");
    }

    @Test
    void setIsDeviceRooted()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.setIsDeviceRooted(false);

        verify(SharedPreferencesModelMocked, times(1)).storeBoolean("is_device_rooted", false);
    }

    @Test
    void setStrictKioskMode()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.setStrictKioskMode(true);

        verify(SharedPreferencesModelMocked, times(1)).storeBoolean("is_strict_kiosk_mode", true);
    }


    MainConfiguration createClass()
    {
        SharedPreferencesModelMocked       = mock(SharedPreferencesModel.class);
        return new MainConfiguration(SharedPreferencesModelMocked);
    }


}