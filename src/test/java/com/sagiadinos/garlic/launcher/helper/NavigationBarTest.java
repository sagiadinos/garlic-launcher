package com.sagiadinos.garlic.launcher.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.services.HUD;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NavigationBarTest
{

    @Test
    void hide()
    {
        Activity ActivityMocked = mock(Activity.class);
        MainConfiguration MainConfigurationMocked = mock(MainConfiguration.class);
        Window WindowMock = mock(Window.class);
        View   ViewMock   = mock(View.class);

        Intent IntentMocked = mock(Intent.class);

        when(MainConfigurationMocked.hasOwnBackButton()).thenReturn(true);
        when(ActivityMocked.getWindow()).thenReturn(WindowMock);
        when(WindowMock.getDecorView()).thenReturn(ViewMock);

        NavigationBar.hide(ActivityMocked, MainConfigurationMocked, IntentMocked);
        verify(ActivityMocked, times(1)).stopService(IntentMocked);
        verify(ViewMock, times(1)).setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.INVISIBLE);
    }

    @Test
    void hideWithoutBAckButton()
    {
        Activity ActivityMocked = mock(Activity.class);
        MainConfiguration MainConfigurationMocked = mock(MainConfiguration.class);
        Window WindowMock = mock(Window.class);
        View   ViewMock   = mock(View.class);

        Intent IntentMocked = mock(Intent.class);

        when(MainConfigurationMocked.hasOwnBackButton()).thenReturn(false);
        when(ActivityMocked.getWindow()).thenReturn(WindowMock);
        when(WindowMock.getDecorView()).thenReturn(ViewMock);

        NavigationBar.hide(ActivityMocked, MainConfigurationMocked, IntentMocked);
        verify(ActivityMocked, times(0)).stopService(IntentMocked);
        verify(ViewMock, times(1)).setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.INVISIBLE);
    }


    @Test
    void show()
    {
        Activity ActivityMocked = mock(Activity.class);
        MainConfiguration MainConfigurationMocked = mock(MainConfiguration.class);
        Window WindowMock = mock(Window.class);
        View   ViewMock   = mock(View.class);

        Intent IntentMocked = mock(Intent.class);

        when(MainConfigurationMocked.hasOwnBackButton()).thenReturn(true);
        when(ActivityMocked.getWindow()).thenReturn(WindowMock);
        when(WindowMock.getDecorView()).thenReturn(ViewMock);

        NavigationBar.show(ActivityMocked, MainConfigurationMocked, IntentMocked);
        verify(ActivityMocked, times(1)).startService(IntentMocked);
        verify(ViewMock, times(1)).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Test
    void showWithoutBackButton()
    {
        Activity ActivityMocked = mock(Activity.class);
        MainConfiguration MainConfigurationMocked = mock(MainConfiguration.class);
        Window WindowMock = mock(Window.class);
        View   ViewMock   = mock(View.class);

        Intent IntentMocked = mock(Intent.class);

        when(MainConfigurationMocked.hasOwnBackButton()).thenReturn(false);
        when(ActivityMocked.getWindow()).thenReturn(WindowMock);
        when(WindowMock.getDecorView()).thenReturn(ViewMock);

        NavigationBar.show(ActivityMocked, MainConfigurationMocked, IntentMocked);
        verify(ActivityMocked, times(0)).startService(IntentMocked);
        verify(ViewMock, times(1)).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}