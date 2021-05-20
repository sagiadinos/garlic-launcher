package com.sagiadinos.garlic.launcher.helper;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import com.sagiadinos.garlic.launcher.MainActivity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HomeLauncherManagerTest
{
    @Mock
    DeviceOwner DeviceOwnerMocked;
    @Mock
    Context ContextMocked;
    @Mock
    Intent IntentMocked;

    @AfterEach
    void tearDown()
    {
        DeviceOwnerMocked = null;
        ContextMocked     = null;
        IntentMocked      = null;
    }

    @Test
    void isHomeActivitySuccess()
    {
        ComponentName ComponentNameMocked    = mock(ComponentName.class);
        HomeLauncherManager MyTestClass = createClass();
        when(IntentMocked.resolveActivity(ContextMocked.getPackageManager())).thenReturn(ComponentNameMocked);
        when(ComponentNameMocked.getPackageName()).thenReturn("com.sagiadinos.garlic.launcher");
        when(ContextMocked.getPackageName()).thenReturn("com.sagiadinos.garlic.launcher");

        assertTrue(MyTestClass.isHomeActivity());
    }

    @Test
    void isHomeActivityFailByName()
    {
        ComponentName ComponentNameMocked    = mock(ComponentName.class);
        HomeLauncherManager MyTestClass = createClass();
        when(IntentMocked.resolveActivity(ContextMocked.getPackageManager())).thenReturn(ComponentNameMocked);
        when(ComponentNameMocked.getPackageName()).thenReturn("com.sagiadinos.garlic.launcher");
        when(ContextMocked.getPackageName()).thenReturn("com.sagiadinos.garlic.heidewitzka");
        assertFalse(MyTestClass.isHomeActivity());
    }

    @Test
    void isHomeActivityFailsOnActivity()
    {
        HomeLauncherManager MyTestClass = createClass();
        when(IntentMocked.resolveActivity(ContextMocked.getPackageManager())).thenReturn(null);

        assertFalse(MyTestClass.isHomeActivity());
    }

    @Test
    void becomeHomeActivity()
    {
        HomeLauncherManager MyTestClass = createClass();

        DeviceOwnerMocked       = mock(DeviceOwner.class);
        MyTestClass.becomeHomeActivity(DeviceOwnerMocked);

        verify(DeviceOwnerMocked, times(1)).addPersistentPreferredActivity();

    }

    @Test
    void restoreHomeActivity()
    {
        HomeLauncherManager MyTestClass = createClass();

        DeviceOwnerMocked       = mock(DeviceOwner.class);
        MyTestClass.restoreHomeActivity(DeviceOwnerMocked);

        verify(DeviceOwnerMocked, times(1)).clearMainPackageFromPersistent();
    }


    HomeLauncherManager createClass()
    {
        ContextMocked           = mock(Context.class);
        IntentMocked            = mock(Intent.class);
        return new HomeLauncherManager(ContextMocked, IntentMocked);
    }


}