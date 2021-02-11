package com.sagiadinos.garlic.launcher.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class UsbConnectionReceiverTest
{
    @Mock
    Context ContextMocked;
    @Mock
    Intent IntentMocked;
    @Mock
    Intent NewIntentMocked;
    @Mock
    File NewFileMocked;
    @Mock
    MainConfiguration MainConfigurationMocked;

    @AfterEach
    void tearDown()
    {
        ContextMocked = null;
        IntentMocked = null;
        NewIntentMocked = null;
        NewFileMocked = null;
        MainConfigurationMocked = null;
    }

    @Test
    void onReceiveIntentNull()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        MyTestClass.onReceive(ContextMocked, null);
        verify(MyTestClass, never()).createFile(anyString());

    }

    @Test
    void onReceiveIntentGetActionNull()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        when(IntentMocked.getAction()).thenReturn(null);
        MyTestClass.onReceive(ContextMocked, IntentMocked);

        verify(IntentMocked, times(1)).getAction();
        verify(MyTestClass, never()).createFile(anyString());
    }

    @Test
    void onReceiveNoMountAction()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        when(IntentMocked.getAction()).thenReturn("some obvious action");
        when(IntentMocked.getData()).thenReturn(null);

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        verify(IntentMocked, never()).getData();
    }

    @Test
    void onReceiveNoIntentUri()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        when(IntentMocked.getAction()).thenReturn(Intent.ACTION_MEDIA_MOUNTED);
        when(IntentMocked.getData()).thenReturn(null);

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        verify(IntentMocked, times(1)).getData();
    }

/*
    Fails because of a mockito update
    @Test
    void onReceiveIndexSmil()
    {
        UsbConnectionReceiver MyTestClass = createClassForDispatch();
        File FileMock              = mock(File.class);
        Intent BroadCastIntentMock = mock(Intent.class);
        MainConfigurationMocked    = mock(MainConfiguration.class);
        SharedPreferencesModel SharedPreferencesModelMocked = mock(SharedPreferencesModel.class);

        when(ContextMocked.getSharedPreferences(DeviceOwner.LAUNCHER_PACKAGE_NAME, Context.MODE_PRIVATE)).thenReturn(mock(SharedPreferences.class));
        when(MyTestClass.createSharedPreferencesModel()).thenReturn(SharedPreferencesModelMocked);
        when(MyTestClass.createMainConfiguration()).thenReturn(MainConfigurationMocked);

        when(MyTestClass.createFile("//path/to/usb-mount/index.smil")).thenReturn(FileMock);
        when(FileMock.exists()).thenReturn(true);
        when(FileMock.canRead()).thenReturn(true);
        when(FileMock.getAbsolutePath()).thenReturn("//path/to/usb-mount/index.smil");

        when(MyTestClass.createIntent("com.sagiadinos.garlic.player.java.SmilIndexReceiver")).thenReturn(BroadCastIntentMock);

        MyTestClass.onReceive(ContextMocked, IntentMocked);

        verify(MainConfigurationMocked, times(1)).storeSmilIndex("//path/to/usb-mount/index.smil");
        verify(FileMock, times(1)).getAbsolutePath();
        verify(BroadCastIntentMock, times(1)).putExtra("smil_index_path", "//path/to/usb-mount/index.smil");
        verify(ContextMocked, times(1)).sendBroadcast(BroadCastIntentMock);
    }
*/
    @Test
    void onReceiveConfigXML()
    {
        UsbConnectionReceiver MyTestClass = createClassForDispatch();
        File FileMock              = mock(File.class);
        Intent BroadCastIntentMock = mock(Intent.class);

        when(MyTestClass.createFile("//path/to/usb-mount/config.xml")).thenReturn(FileMock);
        when(FileMock.exists()).thenReturn(true);
        when(FileMock.canRead()).thenReturn(true);
        when(FileMock.getAbsolutePath()).thenReturn("//path/to/usb-mount/config.xml");

        when(MyTestClass.createIntent("com.sagiadinos.garlic.launcher.receiver.ConfigXMLReceiver")).thenReturn(BroadCastIntentMock);

        MyTestClass.onReceive(ContextMocked, IntentMocked);

        verify(FileMock, times(1)).getAbsolutePath();
        verify(BroadCastIntentMock, times(1)).putExtra("config_path", "//path/to/usb-mount/config.xml");
        verify(ContextMocked, times(1)).sendBroadcast(BroadCastIntentMock);
    }

    @Test
    void onReceivePlayerApk()
    {
        UsbConnectionReceiver MyTestClass = createClassForDispatch();
        File FileMock              = mock(File.class);
        Intent BroadCastIntentMock = mock(Intent.class);

        when(MyTestClass.createFile("//path/to/usb-mount/garlic-player.apk")).thenReturn(FileMock);
        when(FileMock.exists()).thenReturn(true);
        when(FileMock.canRead()).thenReturn(true);
        when(FileMock.getAbsolutePath()).thenReturn("//path/to/usb-mount/garlic-player.apk");

        when(MyTestClass.createIntent("com.sagiadinos.garlic.launcher.receiver.InstallAppReceiver")).thenReturn(BroadCastIntentMock);

        MyTestClass.onReceive(ContextMocked, IntentMocked);

        verify(FileMock, times(1)).getAbsolutePath();
        verify(BroadCastIntentMock, times(1)).putExtra("apk_path", "//path/to/usb-mount/garlic-player.apk");
        verify(ContextMocked, times(1)).sendBroadcast(BroadCastIntentMock);
    }

    UsbConnectionReceiver createClassForDispatch()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        Uri UriMock = mock(Uri.class);
        when(IntentMocked.getAction()).thenReturn(Intent.ACTION_MEDIA_MOUNTED);
        when(IntentMocked.getData()).thenReturn(UriMock);
        when(UriMock.getPath()).thenReturn("//path/to/usb-mount");

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        return MyTestClass;
    }


    UsbConnectionReceiver createClass()
    {
        ContextMocked           = mock(Context.class);
        IntentMocked            = mock(Intent.class);
        UsbConnectionReceiver MyTestClass = spy(new UsbConnectionReceiver());
        return MyTestClass;
    }


}