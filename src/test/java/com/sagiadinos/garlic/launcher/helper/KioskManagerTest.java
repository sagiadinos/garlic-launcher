package com.sagiadinos.garlic.launcher.helper;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KioskManagerTest
{
    @Mock
    DeviceOwner DeviceOwnerMocked;
    @Mock
    HomeLauncherManager  HomeLauncherManagerMocked;
    @Mock
    LockTaskManager      LockTaskManagerMocked;
    @Mock
    MainConfiguration MainConfigurationMocked;

    @AfterEach
    void tearDown()
    {
        DeviceOwnerMocked = null;
        HomeLauncherManagerMocked = null;
        LockTaskManagerMocked = null;
        MainConfigurationMocked = null;
    }

    @Test
    void startKioskModeSuccess()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(true);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(true);

        assertTrue(MyTestClass.startKioskMode());
        verify(LockTaskManagerMocked, times(1)).startLockTask();
        verify(HomeLauncherManagerMocked, times(1)).becomeHomeActivity();
    }

    @Test
    void startKioskModeFailsAdminActive()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(false);

        assertFalse(MyTestClass.startKioskMode());
        verify(LockTaskManagerMocked, times(0)).startLockTask();
        verify(HomeLauncherManagerMocked, times(0)).becomeHomeActivity();
    }


    @Test
    void startKioskModeFailsDeviceOwner()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(false);

        assertFalse(MyTestClass.startKioskMode());
        verify(LockTaskManagerMocked, times(0)).startLockTask();
        verify(HomeLauncherManagerMocked, times(0)).becomeHomeActivity();
    }

    @Test
    void startKioskModeFailsLockTaskPermitted()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(false);

        assertFalse(MyTestClass.startKioskMode());
        verify(LockTaskManagerMocked, times(0)).startLockTask();
        verify(HomeLauncherManagerMocked, times(0)).becomeHomeActivity();
    }

    @Test
    void toggleServiceModeWithTrue()
    {
        KioskManager MyTestClass = createClass();

        MyTestClass.toggleServiceMode(true);
        verify(MainConfigurationMocked, times(1)).setStrictKioskMode(false);
    }

    @Test
    void toggleServiceModeWithFalse()
    {
        KioskManager MyTestClass = createClass();

        MyTestClass.toggleServiceMode(false);
        verify(MainConfigurationMocked, times(1)).setStrictKioskMode(true);
    }


    @Test
    void isStrictKioskModeActive()
    {
        KioskManager MyTestClass = createClass();
        when(MainConfigurationMocked.isStrictKioskModeActive()).thenReturn(true);

        assertTrue(MyTestClass.isStrictKioskModeActive());
        verify(MainConfigurationMocked, times(1)).isStrictKioskModeActive();
    }

    @Test
    void toggleKioskModeSuccess()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(true);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(true);
        when(LockTaskManagerMocked.toggleLockTask()).thenReturn(true);

        assertTrue(MyTestClass.toggleKioskMode());
        verify(LockTaskManagerMocked, times(1)).toggleLockTask();

    }

    @Test
    void toggleKioskModeFailsToggleLockTask()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(true);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(true);
        when(LockTaskManagerMocked.toggleLockTask()).thenReturn(false);

        assertFalse(MyTestClass.toggleKioskMode());
        verify(LockTaskManagerMocked, times(1)).toggleLockTask();

    }

    @Test
    void toggleKioskModeFailsDeviceOwner()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(false);

        assertFalse(MyTestClass.toggleKioskMode());
        verify(LockTaskManagerMocked, times(0)).toggleLockTask();

    }

    @Test
    void isHomeActivityTrue()
    {
        KioskManager MyTestClass = createClass();
        when(HomeLauncherManagerMocked.isHomeActivity()).thenReturn(true);

        assertTrue(MyTestClass.isHomeActivity());
        verify(HomeLauncherManagerMocked, times(1)).isHomeActivity();
    }

    @Test
    void isHomeActivityFalse()
    {
        KioskManager MyTestClass = createClass();
        when(HomeLauncherManagerMocked.isHomeActivity()).thenReturn(false);

        assertFalse(MyTestClass.isHomeActivity());
        verify(HomeLauncherManagerMocked, times(1)).isHomeActivity();
    }


    @Test
    void becomeHomeActivitySuccess()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(true);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(true);

        MyTestClass.becomeHomeActivity();
        verify(HomeLauncherManagerMocked, times(1)).becomeHomeActivity();
    }

    @Test
    void becomeHomeActivityFails()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(false);

        MyTestClass.becomeHomeActivity();
        verify(HomeLauncherManagerMocked, times(0)).becomeHomeActivity();
    }


    @Test
    void toggleHomeActivitySuccess()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(true);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(true);
        when(HomeLauncherManagerMocked.toggleHomeActivity()).thenReturn(true);

        assertTrue(MyTestClass.toggleHomeActivity());
        verify(HomeLauncherManagerMocked, times(1)).toggleHomeActivity();
    }

    @Test
    void toggleHomeActivityFailsDeviceOwner()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(false);

        assertFalse(MyTestClass.toggleHomeActivity());
        verify(HomeLauncherManagerMocked, times(0)).toggleHomeActivity();
    }

    @Test
    void toggleHomeActivityFailsToogleHomeActivity()
    {
        KioskManager MyTestClass = createClass();
        when(DeviceOwnerMocked.isAdminActive()).thenReturn(true);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(DeviceOwnerMocked.isLockTaskPermitted()).thenReturn(true);
        when(HomeLauncherManagerMocked.toggleHomeActivity()).thenReturn(false);

        assertFalse(MyTestClass.toggleHomeActivity());
        verify(HomeLauncherManagerMocked, times(1)).toggleHomeActivity();
    }

    KioskManager createClass()
    {
        DeviceOwnerMocked         = mock(DeviceOwner.class);
        HomeLauncherManagerMocked = mock(HomeLauncherManager.class);
        LockTaskManagerMocked     = mock(LockTaskManager.class);
        MainConfigurationMocked   = mock(MainConfiguration.class);
        return new KioskManager(DeviceOwnerMocked, HomeLauncherManagerMocked, LockTaskManagerMocked, MainConfigurationMocked);
    }

}