package com.sagiadinos.garlic.launcher.helper;

import android.app.ActivityManager;
import android.content.Context;

import com.sagiadinos.garlic.launcher.MainActivity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LockTaskManagerTest
{
    @Mock
    MainActivity MainActivityMocked;

    @AfterEach
    void tearDown()
    {
        MainActivityMocked = null;
    }


    @Test
    void toggleLockTaskTrue()
    {
        ActivityManager ActivityManagerMocked = mock(ActivityManager.class);
        LockTaskManager MyTestClass = createClass();

        when(MainActivityMocked.getSystemService(Context.ACTIVITY_SERVICE)).thenReturn(ActivityManagerMocked);
        when(ActivityManagerMocked.getLockTaskModeState()).thenReturn(ActivityManager.LOCK_TASK_MODE_NONE);

        assertTrue(MyTestClass.toggleLockTask());
        verify(MainActivityMocked, times(1)).startLockTask();
        verify(MainActivityMocked, times(0)).stopLockTask();
        verify(ActivityManagerMocked, times(1)).getLockTaskModeState();
    }

    @Test
    void toggleLockTaskTrieWithActivityManagerNull()
    {
        ActivityManager ActivityManagerMocked = mock(ActivityManager.class);
        LockTaskManager MyTestClass = createClass();

        when(MainActivityMocked.getSystemService(Context.ACTIVITY_SERVICE)).thenReturn(null);

        assertTrue(MyTestClass.toggleLockTask());
        verify(MainActivityMocked, times(1)).startLockTask();
        verify(MainActivityMocked, times(0)).stopLockTask();
        verify(ActivityManagerMocked, times(0)).getLockTaskModeState();
    }

    @Test
    void toggleLockTaskFalse()
    {
        ActivityManager ActivityManagerMocked = mock(ActivityManager.class);
        LockTaskManager MyTestClass = createClass();

        when(MainActivityMocked.getSystemService(Context.ACTIVITY_SERVICE)).thenReturn(ActivityManagerMocked);
        when(ActivityManagerMocked.getLockTaskModeState()).thenReturn(ActivityManager.LOCK_TASK_MODE_PINNED);

        assertFalse(MyTestClass.toggleLockTask());
        verify(MainActivityMocked, times(0)).startLockTask();
        verify(MainActivityMocked, times(1)).stopLockTask();
        verify(ActivityManagerMocked, times(1)).getLockTaskModeState();
    }

    @Test
    void startLockTask()
    {
        LockTaskManager MyTestClass = createClass();
        MyTestClass.startLockTask();
        verify(MainActivityMocked, times(1)).startLockTask();
    }

    LockTaskManager createClass()
    {
        MainActivityMocked = mock(MainActivity.class);
        return new LockTaskManager(MainActivityMocked);
    }

}