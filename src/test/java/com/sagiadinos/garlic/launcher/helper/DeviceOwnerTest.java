package com.sagiadinos.garlic.launcher.helper;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sagiadinos.garlic.launcher.MainActivity;
import com.sagiadinos.garlic.launcher.receiver.AdminReceiver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceOwnerTest
{

    @Mock
    DevicePolicyManager DevicePolicyManagerMocked;
    @Mock
    ComponentName DeviceAdminMocked;
    @Mock
    IntentFilter IntentFilterMocked;
    @Mock
    ComponentName MainActivityComponentMocked;


    @AfterEach
    void tearDown()
    {
        DevicePolicyManagerMocked = null;
        DeviceAdminMocked         = null;
    }

    @Test
    void makeDeviceOwner() throws IOException
    {
        DeviceOwner MyTestClass = createClass();
        Runtime RuntimeMocked = mock(Runtime.class);

        MyTestClass.makeDeviceOwner(RuntimeMocked);
        verify(RuntimeMocked, times(1)).exec(new String[]{"su","-c","dpm set-device-owner com.sagiadinos.garlic.launcher/.receiver.AdminReceiver"});
    }

    @Test
    void activateRestrictions()
    {
        DeviceOwner MyTestClass = createClass();

        MyTestClass.activateRestrictions();
        verify(DevicePolicyManagerMocked, times(6)).addUserRestriction(any(ComponentName.class), anyString());

    }


    @Test
    void deactivateRestrictions()
    {
        DeviceOwner MyTestClass = createClass();
        MyTestClass.deactivateRestrictions();
        verify(DevicePolicyManagerMocked, times(6)).clearUserRestriction(any(ComponentName.class), anyString());
    }

    @Test
    void isLockTaskPermitted()
    {
        DeviceOwner MyTestClass = createClass();
        when(DevicePolicyManagerMocked.isLockTaskPermitted(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(false);
        assertFalse(MyTestClass.isLockTaskPermitted());
        verify(DevicePolicyManagerMocked, times(1)).isLockTaskPermitted(DeviceOwner.LAUNCHER_PACKAGE_NAME);
    }

    @Test
    void isAdminActive()
    {
        DeviceOwner MyTestClass = createClass();
        when(DevicePolicyManagerMocked.isAdminActive(DeviceAdminMocked)).thenReturn(true);
        assertTrue(MyTestClass.isAdminActive());
        verify(DevicePolicyManagerMocked, times(1)).isAdminActive(any(ComponentName.class));
    }

    @Test
    void isDeviceOwner()
    {
        DeviceOwner MyTestClass = createClass();
        when(DevicePolicyManagerMocked.isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(true);
        assertTrue(MyTestClass.isDeviceOwner());
        verify(DevicePolicyManagerMocked, times(1)).isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME);
    }

    @Test
    void testIsDeviceOwner()
    {
        // class is static, so we need the DevicePolicyManagerMocked only so no return value
        createClass();

        when(DevicePolicyManagerMocked.isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(false);
        assertFalse(DeviceOwner.isDeviceOwner(DevicePolicyManagerMocked));
        verify(DevicePolicyManagerMocked, times(1)).isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME);

    }

    @Test
    void reboot()
    {
        // class is static, so we need the DevicePolicyManagerMocked  and DeviceAdminMocked
        // no return value necessary
        createClass();
        when(DevicePolicyManagerMocked.isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(true);
        DeviceOwner.reboot(DevicePolicyManagerMocked, DeviceAdminMocked);
        verify(DevicePolicyManagerMocked, times(1)).reboot(DeviceAdminMocked);
    }

    @Test
    void rebootWithNull()
    {
        // class is static, so we need the DevicePolicyManagerMocked  and DeviceAdminMocked
        // no return value necessary
        createClass();
        DeviceOwner.reboot(null, DeviceAdminMocked);
        verify(DevicePolicyManagerMocked, never()).reboot(DeviceAdminMocked);
        verify(DevicePolicyManagerMocked, never()).isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME);
    }

    @Test
    void rebootWithFalse()
    {
        // class is static, so we need the DevicePolicyManagerMocked  and DeviceAdminMocked
        // no return value necessary
        createClass();
        when(DevicePolicyManagerMocked.isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(false);
        DeviceOwner.reboot(DevicePolicyManagerMocked, DeviceAdminMocked);
        verify(DevicePolicyManagerMocked, never()).reboot(DeviceAdminMocked);
        verify(DevicePolicyManagerMocked, times(1)).isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME);
    }

    @Test
    void addPersistentPreferredActivity()
    {
        DeviceOwner MyTestClass         = createClass();
        MyTestClass.addPersistentPreferredActivity();
        verify(DevicePolicyManagerMocked, times(1)).addPersistentPreferredActivity(DeviceAdminMocked, IntentFilterMocked, MainActivityComponentMocked);
        verify(IntentFilterMocked, times(1)).addCategory(Intent.CATEGORY_DEFAULT);
        verify(IntentFilterMocked, times(1)).addCategory(Intent.CATEGORY_HOME);
    }

    @Test
    void clearMainPackageFromPersistent()
    {
        DeviceOwner MyTestClass = createClass();
        when(DevicePolicyManagerMocked.isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(true);
        MyTestClass.clearMainPackageFromPersistent();
        verify(DevicePolicyManagerMocked, times(1)).clearPackagePersistentPreferredActivities(DeviceAdminMocked, DeviceOwner.LAUNCHER_PACKAGE_NAME);
    }

    @Test
    void clearMainPackageFromPersistentNoDeviceOwner()
    {
        DeviceOwner MyTestClass = createClass();
        when(DevicePolicyManagerMocked.isDeviceOwnerApp(DeviceOwner.LAUNCHER_PACKAGE_NAME)).thenReturn(false);
        MyTestClass.clearMainPackageFromPersistent();
        verify(DevicePolicyManagerMocked, never()).clearPackagePersistentPreferredActivities(DeviceAdminMocked, DeviceOwner.LAUNCHER_PACKAGE_NAME);
    }

    @Test
    void determinePermittedLockTaskPackagesEmpty()
    {
        DeviceOwner MyTestClass = createClass();
        MyTestClass.determinePermittedLockTaskPackages("");
        String[] s = new String[]{DeviceOwner.LAUNCHER_PACKAGE_NAME, DeviceOwner.PLAYER_PACKAGE_NAME};
        verify(DevicePolicyManagerMocked, times(1)).setLockTaskPackages(DeviceAdminMocked, s);
    }

    @Test
    void determinePermittedLockTaskPackagesNull()
    {
        DeviceOwner MyTestClass = createClass();
        MyTestClass.determinePermittedLockTaskPackages(null);
        String[] s = new String[]{DeviceOwner.LAUNCHER_PACKAGE_NAME, DeviceOwner.PLAYER_PACKAGE_NAME};
        verify(DevicePolicyManagerMocked, never()).setLockTaskPackages(DeviceAdminMocked, s);
    }

    @Test
    void determinePermittedLockTaskPackagesWithValue()
    {
        DeviceOwner MyTestClass = createClass();
        MyTestClass.determinePermittedLockTaskPackages("com.sagiadinos.secondapp");
        String[] s = new String[]{DeviceOwner.LAUNCHER_PACKAGE_NAME, DeviceOwner.PLAYER_PACKAGE_NAME, "com.sagiadinos.secondapp"};
        verify(DevicePolicyManagerMocked, times(1)).setLockTaskPackages(DeviceAdminMocked, s);
    }


    DeviceOwner createClass()
    {
        DevicePolicyManagerMocked       = mock(DevicePolicyManager.class);
        DeviceAdminMocked               = mock(ComponentName.class);
        MainActivityComponentMocked     = mock(ComponentName.class);
        IntentFilterMocked              = mock(IntentFilter.class);;

        return new DeviceOwner(DevicePolicyManagerMocked, DeviceAdminMocked, MainActivityComponentMocked, IntentFilterMocked);
    }

}