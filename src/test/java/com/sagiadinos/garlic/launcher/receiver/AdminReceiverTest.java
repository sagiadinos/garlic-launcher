package com.sagiadinos.garlic.launcher.receiver;

import android.content.Context;
import android.content.Intent;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminReceiverTest
{
    @Mock
    Context ContextMocked;
    @Mock
    Intent IntentMocked;
    @Mock
    Intent NewIntentMocked;

    @Test
    void onEnabled()
    {
        AdminReceiver MyReceiver = spy(new AdminReceiver());
        ContextMocked            = mock(Context.class);
        IntentMocked             = mock(Intent.class);
        NewIntentMocked          = mock(Intent.class);

        when(MyReceiver.createIntent(ContextMocked)).thenReturn(NewIntentMocked);

        MyReceiver.onEnabled(ContextMocked, IntentMocked);

        verify(NewIntentMocked, times(1)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        verify(ContextMocked, times(1)).startActivity(NewIntentMocked);

    }
}