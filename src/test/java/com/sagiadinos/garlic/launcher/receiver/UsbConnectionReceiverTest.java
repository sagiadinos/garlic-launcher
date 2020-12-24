package com.sagiadinos.garlic.launcher.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsbConnectionReceiverTest
{
    @Mock
    Context ContextMocked;
    @Mock
    DeviceOwner DeviceOwnerMocked;
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
        DeviceOwnerMocked = null;
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
        verify(DeviceOwnerMocked, times(0)).isDeviceOwner();
    }

    @Test
    void onReceiveDeviceOwnerNull()
    {
        UsbConnectionReceiver MyTestClass = createClass();
        MyTestClass.injectDependencies(null, null); // as it is set to a exist mock value in createClass

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        verify(DeviceOwnerMocked, times(0)).isDeviceOwner();
    }

    @Test
    void onReceiveDeviceOwnerFalse()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(false);

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        verify(DeviceOwnerMocked, times(0)).isDeviceOwner();
    }


    @Test
    void onReceiveNoMountAction()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        when(IntentMocked.getAction()).thenReturn("some obvious action");
        when(IntentMocked.getData()).thenReturn(null);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        verify(DeviceOwnerMocked, times(1)).isDeviceOwner();
        verify(IntentMocked, times(0)).getData();
    }

    @Test
    void onReceiveNoIntentUri()
    {
        UsbConnectionReceiver MyTestClass = createClass();

        when(IntentMocked.getAction()).thenReturn(Intent.ACTION_MEDIA_MOUNTED);
        when(IntentMocked.getData()).thenReturn(null);
        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);

        MyTestClass.onReceive(ContextMocked, IntentMocked);
        verify(DeviceOwnerMocked, times(1)).isDeviceOwner();
        verify(IntentMocked, times(1)).getData();
    }

    @Test
    void onReceiveIndexSmil()
    {
        // create a Genericfactory for files and Intents
        // otherwise this crap cannot be tested  properly
/*        UsbConnectionReceiver MyTestClass = createClass();
        Uri UriMocked           = mock(Uri.class);
        File FileMock           = mock(File.class);

        when(DeviceOwnerMocked.isDeviceOwner()).thenReturn(true);
        when(IntentMocked.getAction()).thenReturn(Intent.ACTION_MEDIA_MOUNTED);
        when(IntentMocked.getData()).thenReturn(UriMocked);
        when(UriMocked.getPath()).thenReturn("a/path");
        when(MyTestClass.createFile("a/path")).thenReturn(FileMock);
        when(FileMock.exists()).thenReturn(true);
        when(FileMock.canRead()).thenReturn(true);


        MyTestClass.onReceive(ContextMocked, IntentMocked);

        verify(DeviceOwnerMocked, times(1)).isDeviceOwner();
        verify(IntentMocked, times(0)).getData();
        verify(MainConfigurationMocked, times(1)).storeSmilIndex("a/path/index.smil");
              verify(ContextMocked, times(1)).sendBroadcast();

*/   }


    UsbConnectionReceiver createClass()
    {
        ContextMocked           = mock(Context.class);
        DeviceOwnerMocked       = mock(DeviceOwner.class);
        IntentMocked            = mock(Intent.class);
        MainConfigurationMocked = mock(MainConfiguration.class);
        UsbConnectionReceiver MyTestClass = spy(new UsbConnectionReceiver());
        MyTestClass.injectDependencies(DeviceOwnerMocked, MainConfigurationMocked);
        return MyTestClass;
    }


}