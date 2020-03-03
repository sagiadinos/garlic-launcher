/*************************************************************************************
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-player source code

 This program is free software: you can redistribute it and/or  modify
 it under the terms of the GNU Affero General Public License, version 3,
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************************/

package com.sagiadinos.garlic.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sagiadinos.garlic.launcher.helper.Configuration;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.HomeLauncherManager;
import com.sagiadinos.garlic.launcher.helper.Installer;
import com.sagiadinos.garlic.launcher.helper.KioskManager;
import com.sagiadinos.garlic.launcher.helper.LockTaskManager;
import com.sagiadinos.garlic.launcher.helper.Permissions;
import com.sagiadinos.garlic.launcher.helper.WiFi;
import com.sagiadinos.garlic.launcher.receiver.ReceiverManager;

public class MainActivity extends Activity
{
    private boolean        has_second_app_started = false;
    private boolean        has_player_started     = false;
    private Button         button_toggle_lock     = null;
    private Button         button_toggle_launcher = null;
    private Button         button_player          = null;
    private Button         button_content_uri     = null;
    private TextView       text_information       = null;
    private CountDownTimer PlayerCountDown        = null;
    private DeviceOwner    MyDeviceOwner          = null;
    private Configuration  MyConfiguration        = null;
    private KioskManager    MyKiosk               = null;
    private final static String TAG               = "MainActivity";

     @Override
    public void onCreate(Bundle savedInstanceState)
    {
         super.onCreate(savedInstanceState);

         setContentView(R.layout.main);
         text_information = (TextView) findViewById(R.id.textViewInformation);
         MyDeviceOwner    = new DeviceOwner(this);

         if (MyDeviceOwner.isDeviceOwner())
         {
             MyConfiguration = new Configuration(this);
             text_information.setVisibility(View.INVISIBLE);
             Permissions.verifyStoragePermissions(this);
             initButtonViews();
             initHelperClasses();
             startGarlicPlayerDelayed();
         }
         else
         {
             text_information.setVisibility(View.VISIBLE);
             text_information.setText(R.string.no_device_owner);
         }
     }
    private void initHelperClasses()
    {
        MyKiosk       = new KioskManager(MyDeviceOwner,
                                            new HomeLauncherManager(MyDeviceOwner, this),
                                            new LockTaskManager(this),
                                            this
        );

        ReceiverManager.registerAllReceiver(this);
        startService(new Intent(this, WatchDogService.class)); // this is ok no nesting or leaks

        if (MyKiosk.startKioskMode()) // Pin this app and set it as Launcher
        {
            button_toggle_lock.setText(R.string.unpin_app);
            button_toggle_launcher.setText(R.string.restore_old_launcher);
        }

    }

    private void initButtonViews()
    {
        button_toggle_lock     = (Button) findViewById(R.id.buttonToggleLockTask);
        button_toggle_launcher = (Button) findViewById(R.id.buttonToggleLauncher);
        button_player          = (Button) findViewById(R.id.buttonGarlicPlayer);
        button_content_uri     = (Button) findViewById(R.id.buttonSetContentURI);
        if (Installer.isPackageInstalled(MainActivity.this, DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME)
                ||  Installer.isPackageInstalled(MainActivity.this, DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME_ALT)
        )
        {
            button_content_uri.setVisibility(View.VISIBLE);
            button_player.setVisibility(View.VISIBLE);
            text_information.setVisibility(View.INVISIBLE);
        }
        else
        {
            button_content_uri.setVisibility(View.INVISIBLE);
            button_player.setVisibility(View.INVISIBLE);
            text_information.setText(R.string.no_garlic_info);
            text_information.setVisibility(View.VISIBLE);
            return;
        }

        if (BuildConfig.DEBUG)
        {
            button_toggle_lock.setVisibility(View.VISIBLE);
            button_toggle_launcher.setVisibility(View.VISIBLE);
        }
        hideBars();
    }

    @Override
    protected void onDestroy()
    {
        if (MyDeviceOwner.isDeviceOwner())
        {
            ReceiverManager.unregisterAllReceiver(this);
        }
        super.onDestroy();
    }

    public boolean hasSecondAppStarted()
    {
        return has_second_app_started;
    }

    public boolean hasPlayerStarted()
    {
        return has_player_started;
    }

    public void toggleLockTask(View view)
    {
        if (MyKiosk.toggleKioskMode())
        {
            button_toggle_lock.setText(R.string.unpin_app);
        }
        else
        {
            button_toggle_lock.setText(R.string.pin_app);
       }
    }

    public void toggleLauncher(View view)
    {
        if (MyKiosk.toggleHomeActivity())
        {
            button_toggle_launcher.setText(R.string.restore_old_launcher);
        }
        else
        {
            button_toggle_launcher.setText(R.string.become_launcher);
        }
    }

    public void startGarlicPlayerDelayed()
    {
        has_second_app_started = false;
        has_player_started     = false;

        if (MyConfiguration.getSmilIndex("").isEmpty() || !WiFi.isWifiConnected(this))
        {
            button_player.setText(R.string.play);
            return;
        }

        PlayerCountDown      = new CountDownTimer(15000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                button_player.setText(getString(R.string.count_down, String.valueOf(millisUntilFinished / 1000)));
            }

            public void onFinish()
            {
                button_player.setText(R.string.play);
                startGarlicPlayer(null);
            }

        }.start();
    }

    public void handleGarlicPlayerStartTimer(View view)
    {
        String button_text = (String) button_player.getText();
        if (!button_text.equals(getResources().getString(R.string.play)))
        {
            if (PlayerCountDown != null)
            {
                PlayerCountDown.cancel();
                button_player.setText(R.string.play);
            }
        }
        else
        {
            startGarlicPlayer(view);
        }
    }

    public void setContentUrl(View view)
    {
        stopPlayerRestart();

        Intent intent = new Intent(this, ContentUrlActivity.class);
        startActivity(intent);
    }

    public void configWiFi(View view)
    {
        stopPlayerRestart();

        Intent intent = new Intent(this, ConfigWiFiActivity.class);
        startActivity(intent);
    }


    public void rebootOS(View view)
    {
        MyDeviceOwner.reboot();
    }

    public void startGarlicPlayer(View view)
    {
        has_second_app_started = false;
        has_player_started     = true;
        startApp(DeviceOwner.GARLIC_PLAYER_PACKAGE_NAME);
    }

    public void startSecondApp(String package_name)
    {
        has_second_app_started = true;
        has_player_started     = false;
        MyDeviceOwner.determinePermittedLockTaskPackages(package_name);
        startApp(package_name);
    }

    private void startApp(String package_name)
    {
        Intent intent = getPackageManager().getLaunchIntentForPackage(package_name);
        if (intent == null)
        {
            showToast("The app package \"" + package_name + "\" not exist");
            return;
        }
        startActivity(intent);
    }


    private void hideBars()
    {
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.INVISIBLE);

    }

    private void showToast(String text)
    {
        if (BuildConfig.DEBUG)
        {
            Toast.makeText(this, TAG + ": " + text, Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlayerRestart()
    {
        has_second_app_started = false;
        has_player_started     = false;
        if (PlayerCountDown != null)
        {
            PlayerCountDown.cancel();
        }
        button_player.setText(R.string.play);
    }
}
