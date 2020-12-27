package com.sagiadinos.garlic.launcher.helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InstallerTest
{
    @Mock
    Context ContextMocked;
    @Mock
    PackageInstaller PackageInstallerMocked;

    @BeforeEach
    void setUp()
    {
        ContextMocked = mock(Context.class);
        PackageInstallerMocked = mock(PackageInstaller.class);
    }

    @AfterEach
    void tearDown()
    {
        ContextMocked = null;
        PackageInstallerMocked = null;
    }

    @Test
    void isGarlicPlayerInstalled() throws PackageManager.NameNotFoundException
    {
        PackageManager PackageManagerMocked = mock(PackageManager.class);
        when(ContextMocked.getPackageManager()).thenReturn(PackageManagerMocked);

        assertTrue(Installer.isGarlicPlayerInstalled(ContextMocked));
        verify(PackageManagerMocked, times(1)).getPackageInfo(DeviceOwner.PLAYER_PACKAGE_NAME, PackageManager.GET_META_DATA);
    }

    @Test
    void installViaShell()
    {
        ShellExecute ShellExecuteMocked = mock(ShellExecute.class);
        PackageManager PackageManagerMocked = mock(PackageManager.class);
        String package_path = "heidewitzka.der.kapitän";

        when(ShellExecuteMocked.executeAsRoot("pm install -r " + package_path + "\n")).thenReturn(true);
        when(ContextMocked.getPackageManager()).thenReturn(PackageManagerMocked);
        when(PackageManagerMocked.getPackageInstaller()).thenReturn(PackageInstallerMocked);

        Installer MyTestClass = new Installer(ContextMocked);

        assertTrue(MyTestClass.installViaShell(ShellExecuteMocked, package_path));

        verify(ShellExecuteMocked, times(1)).executeAsRoot("pm install -r " + package_path + "\n");

    }

    @Test
    void installPackage()
    {
    }

    @Test
    void uninstall()
    {
    }

    @Test
    void isPackageInstalled() throws PackageManager.NameNotFoundException
    {
        PackageManager PackageManagerMocked = mock(PackageManager.class);
        when(ContextMocked.getPackageManager()).thenReturn(PackageManagerMocked);
        String check_package = "com.sagiadinos.test";

        assertTrue(Installer.isPackageInstalled(ContextMocked, check_package));
        verify(PackageManagerMocked, times(1)).getPackageInfo(check_package, PackageManager.GET_META_DATA);
    }

    @Test
    void isPackageInstalledFails() throws PackageManager.NameNotFoundException
    {
        PackageManager PackageManagerMocked = mock(PackageManager.class);
        when(ContextMocked.getPackageManager()).thenReturn(PackageManagerMocked);
        String check_package = "com.sagiadinos.test";

        when(PackageManagerMocked.getPackageInfo(check_package, PackageManager.GET_META_DATA)).thenThrow(new PackageManager.NameNotFoundException());

        assertFalse(Installer.isPackageInstalled(ContextMocked, check_package));

    }

    @Test
    void getAppNameFromPkgNameSucceed()
    {
        PackageManager PackageManagerMocked = mock(PackageManager.class);
        PackageInfo    PackageInfoMocked    = mock(PackageInfo.class);
        PackageInfoMocked.packageName = "the_package_name";
        String         file_path = "the/path/to/file.apk";
        String         expected  = "the_package_name";

        when(ContextMocked.getPackageManager()).thenReturn(PackageManagerMocked);
        when(PackageManagerMocked.getPackageArchiveInfo(file_path, 0)).thenReturn(PackageInfoMocked);

        assertEquals(expected, Installer.getAppNameFromPkgName(ContextMocked, file_path));

        //assertEquals(expected, verify(PackageInfoMocked, times(1)).packageName);

    }

    @Test
    void getAppNameFromPkgNameFails()
    {
        PackageManager PackageManagerMocked = mock(PackageManager.class);
        PackageInfo    PackageInfoMocked    = mock(PackageInfo.class);
        String         file_path = "the/path/to/file.apk";
        when(ContextMocked.getPackageManager()).thenReturn(PackageManagerMocked);
        when(PackageManagerMocked.getPackageArchiveInfo(file_path, 0)).thenReturn(null);

        assertEquals("", Installer.getAppNameFromPkgName(ContextMocked, file_path));

      //  assertNull(verify(PackageInfoMocked, times(0)).packageName);
    }
}