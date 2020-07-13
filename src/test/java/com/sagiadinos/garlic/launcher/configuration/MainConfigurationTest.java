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

        verify(SharedPreferencesModelMocked, times(2)).storeString(anyString(), anyString());

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
    void storeSmilIndex()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.storeSmilIndex("an index");

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
    void storePlayerStartDelay()
    {
        MainConfiguration MyMainConfiguration = createClass();

        MyMainConfiguration.storePlayerStartDelay(22);

        verify(SharedPreferencesModelMocked, times(1)).storeInt("player_start_delay", 22);
    }

    @Test
    void getPlayerStartDelayWithExistingParameter()
    {
        MainConfiguration MyMainConfiguration = createClass();
        when(SharedPreferencesModelMocked.hasParameter("player_start_delay")).thenReturn(true);
        when(SharedPreferencesModelMocked.getInt("player_start_delay")).thenReturn(10);

        assertEquals(10, MyMainConfiguration.getPlayerStartDelay());

        verify(SharedPreferencesModelMocked, times(1)).getInt("player_start_delay");
    }

    @Test
    void getPlayerStartDelayWithOutExistingParameter()
    {
        MainConfiguration MyMainConfiguration = createClass();
        when(SharedPreferencesModelMocked.hasParameter("player_start_delay")).thenReturn(false);

        assertEquals(15, MyMainConfiguration.getPlayerStartDelay());

        verify(SharedPreferencesModelMocked, times(0)).getInt("player_start_delay");
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