package com.sagiadinos.garlic.launcher.helper;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppPermissionsTest
{
    @Mock
    MainConfiguration MainConfigurationMocked;
    @Mock
    Activity MainActivityMocked;

    @AfterEach
    void tearDown()
    {
        MainConfigurationMocked = null;
        MainActivityMocked         = null;
    }

    @Test
    void onRequestPermissionsResultNotGranted()
    {
        final int[] grant_results = {1, 2, 3};
        final String[] PERMISSIONS_LIST = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        final int REQUEST_PERMISSIONS = 1;

        MainActivityMocked      = mock(Activity.class);

        when(MainActivityMocked.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(0);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)).thenReturn(1);

        AppPermissions.onRequestPermissionsResult(MainActivityMocked, REQUEST_PERMISSIONS, PERMISSIONS_LIST, grant_results);

        verify(MainActivityMocked, never()).recreate();
        verify(MainActivityMocked, times(1)).finish();
    }

    @Test
    void onRequestPermissionsResultGranted()
    {
        final int[] grant_results = {1, 2, 3};
        final String[] PERMISSIONS_LIST = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        final int REQUEST_PERMISSIONS = 1;

        MainActivityMocked      = mock(Activity.class);

        when(MainActivityMocked.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(0);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)).thenReturn(0);

        AppPermissions.onRequestPermissionsResult(MainActivityMocked, REQUEST_PERMISSIONS, PERMISSIONS_LIST, grant_results);

        verify(MainActivityMocked, times(1)).recreate();
        verify(MainActivityMocked, never()).finish();

    }

    @Test
    void hasStandardPermissionsWriteNotGranted()
    {
        MainActivityMocked      = mock(Activity.class);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(1);

        assertFalse(AppPermissions.hasStandardPermissions(MainActivityMocked));
        verify(MainActivityMocked, times(1)).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        verify(MainActivityMocked, never()).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Test
    void hasStandardPermissionsReadNotGranted()
    {
        MainActivityMocked      = mock(Activity.class);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(0);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)).thenReturn(1);

        assertFalse(AppPermissions.hasStandardPermissions(MainActivityMocked));

        verify(MainActivityMocked, times(1)).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        verify(MainActivityMocked, times(1)).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }
    @Test
    void hasStandardPermissionsReadAndWriteGranted()
    {
        MainActivityMocked      = mock(Activity.class);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)).thenReturn(0);
        when(MainActivityMocked.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)).thenReturn(0);

        assertTrue(AppPermissions.hasStandardPermissions(MainActivityMocked));

        verify(MainActivityMocked, times(1)).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        verify(MainActivityMocked, times(1)).checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    @Test
    void handlePermissionsAsRoot()
    {
        TextView TextViewMocked         = mock(TextView.class);
        ShellExecute ShellExecuteMocked = mock(ShellExecute.class);

        AppPermissions MyTestClass = createClass();
        when(MainConfigurationMocked.isDeviceRooted()).thenReturn(true);
        when(ShellExecuteMocked.executeAsRoot(anyString())).thenReturn(true);

        MyTestClass.handlePermissions(TextViewMocked, ShellExecuteMocked);

        verify(ShellExecuteMocked, times(3)).executeAsRoot(anyString());
        verify(ShellExecuteMocked, never()).getErrorText();
        verify(TextViewMocked, never()).setText(anyString());
    }

    @Test
    void handlePermissionsAsRootFails()
    {
        TextView TextViewMocked         = mock(TextView.class);
        ShellExecute ShellExecuteMocked = mock(ShellExecute.class);

        AppPermissions MyTestClass = createClass();
        when(MainConfigurationMocked.isDeviceRooted()).thenReturn(true);
        when(ShellExecuteMocked.executeAsRoot(anyString())).thenReturn(false);
        when(ShellExecuteMocked.getErrorText()).thenReturn("error");

        MyTestClass.handlePermissions(TextViewMocked, ShellExecuteMocked);

        verify(ShellExecuteMocked, times(1)).executeAsRoot(anyString());
        verify(ShellExecuteMocked, times(1)).getErrorText();
        verify(TextViewMocked, times(1)).setText(anyString());
    }

    @Test
    void handlePermissionsAsNonRoot()
    {
        TextView TextViewMocked         = mock(TextView.class);
        ShellExecute ShellExecuteMocked = mock(ShellExecute.class);
        final String[] PERMISSIONS_LIST = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        final int REQUEST_PERMISSIONS = 1;

        AppPermissions MyTestClass = createClass();
        when(MainConfigurationMocked.isDeviceRooted()).thenReturn(false);
        when(ShellExecuteMocked.executeAsRoot(anyString())).thenReturn(false);

        MyTestClass.handlePermissions(TextViewMocked, ShellExecuteMocked);

        verify(ShellExecuteMocked, never()).executeAsRoot(anyString());
        verify(MainActivityMocked, times(1)).requestPermissions(PERMISSIONS_LIST, REQUEST_PERMISSIONS);
    }


    @Test
    void verifyOverlayPermissions()
    {
// Settings not really mockable
        /*        Intent IntentMocked = mock(Intent.class);

        try (MockedStatic<Settings> SettingsMocked = Mockito.mockStatic(Settings.class))
        {
            SettingsMocked.when(SettingsMocked.canDrawOverlays(MainActivityMocked)).thenReturn(false);
        }
        AppPermissions MyTestClass = createClass();
        when(MainConfigurationMocked.isDeviceRooted()).thenReturn(true);

        assertFalse(MyTestClass.verifyOverlayPermissions(IntentMocked));

        verify(MainActivityMocked, times(1)).startActivityForResult(IntentMocked, 12);
*/
    }


    AppPermissions createClass()
    {
        MainConfigurationMocked = mock(MainConfiguration.class);
        MainActivityMocked      = mock(Activity.class);

        return new AppPermissions(MainActivityMocked, MainConfigurationMocked);
    }

}