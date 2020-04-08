package com.sagiadinos.garlic.launcher.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.GarlicLauncherException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SharedPreferencesModelTest
{
    @Mock
    SharedPreferences SharedPreferencesMocked;
    @Mock
    SharedPreferences.Editor EditorMock;

    @AfterEach
    void tearDown()
    {
        SharedPreferencesMocked = null;
        EditorMock              = null;
    }

    @Test
    void storeString()
    {
        SharedPreferencesModel TestClass = createClass();
        when(SharedPreferencesMocked.edit()).thenReturn(EditorMock);
        when(EditorMock.commit()).thenReturn(true);

        TestClass.storeString("param1", "value1");

        verify(EditorMock, times(1)).putString("param1", "value1");
        verify(EditorMock, times(1)).commit();
    }

    @Test
    void storeStringWithCatchedException()
    {
        SharedPreferencesModel TestClass = createClass();
        when(SharedPreferencesMocked.edit()).thenReturn(EditorMock);
        when(EditorMock.commit()).thenReturn(false);

        TestClass.storeString("", "");
        assertEquals("commit of SharedPreferences failed", TestClass.getErrorText());
        verify(EditorMock, times(1)).putString("", "");
        verify(EditorMock, times(1)).commit();
    }

    @Test
    void getString()
    {
        SharedPreferencesModel TestClass = createClass();
        when(SharedPreferencesMocked.getString("param1", null)).thenReturn("value1");

        assertEquals("value1", TestClass.getString("param1"));

        verify(SharedPreferencesMocked, times(1)).getString("param1", null);
    }

    @Test
    void storeBoolean()
    {
        SharedPreferencesModel TestClass = createClass();
        when(SharedPreferencesMocked.edit()).thenReturn(EditorMock);
        when(EditorMock.commit()).thenReturn(true);

        TestClass.storeBoolean("param2", true);

        verify(EditorMock, times(1)).putBoolean("param2", true);
        verify(EditorMock, times(1)).commit();
    }

    @Test
    void storeBooleanWithCatchedException()
    {
        SharedPreferencesModel TestClass = createClass();
        when(SharedPreferencesMocked.edit()).thenReturn(EditorMock);
        when(EditorMock.commit()).thenReturn(false);

        TestClass.storeBoolean("", true);
        assertEquals("commit of SharedPreferences failed", TestClass.getErrorText());

        verify(EditorMock, times(1)).putBoolean("", true);
        verify(EditorMock, times(1)).commit();
    }


    @Test
    void getBoolean()
    {
        SharedPreferencesModel TestClass = createClass();
        when(SharedPreferencesMocked.getBoolean("param2", false)).thenReturn(true);

        assertTrue(TestClass.getBoolean("param2"));

        verify(SharedPreferencesMocked, times(1)).getBoolean("param2", false);
    }


    SharedPreferencesModel createClass()
    {
        Context ContextMocked           = mock(Context.class);

        SharedPreferencesMocked = mock(SharedPreferences.class);
        EditorMock              = mock(SharedPreferences.Editor.class);

        when(ContextMocked.getSharedPreferences(DeviceOwner.LAUNCHER_PACKAGE_NAME, Context.MODE_PRIVATE)).thenReturn(SharedPreferencesMocked);
        return new SharedPreferencesModel(ContextMocked);
    }

}