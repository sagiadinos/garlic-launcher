/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
 This file is part of the garlic-launcher source code

 This program is free software: you can redistribute it and/or  modify
 it under the terms of the GNU Affero General Public License, version 3,
 as published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sagiadinos.garlic.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;


import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.helper.Installer;
import com.sagiadinos.garlic.launcher.helper.NavigationBar;
import com.sagiadinos.garlic.launcher.configuration.PasswordHasher;
import com.sagiadinos.garlic.launcher.helper.PlayerDownloader;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.HomeLauncherManager;
import com.sagiadinos.garlic.launcher.helper.KioskManager;
import com.sagiadinos.garlic.launcher.helper.LockTaskManager;
import com.sagiadinos.garlic.launcher.helper.AppPermissions;
import com.sagiadinos.garlic.launcher.helper.TaskExecutionReport;
import com.sagiadinos.garlic.launcher.receiver.AdminReceiver;
import com.sagiadinos.garlic.launcher.receiver.ReceiverManager;
import com.sagiadinos.garlic.launcher.services.HUD;
import com.sagiadinos.garlic.launcher.services.WatchDogService;

public class MainActivity extends Activity
{
    private boolean        has_second_app_started = false;
    private boolean        has_player_started     = false;
    private boolean        is_countdown_running   = false;

    private Button         btToggleLock = null;
    private Button         btToggleLauncher = null;
    private Button         btToggleServiceMode = null;
    private Button         btStartPlayer = null;
    private TextView       tvInformation   = null;

    private CountDownTimer      PlayerCountDown        = null;
    private DeviceOwner         MyDeviceOwner          = null;
    private MainConfiguration   MyMainConfiguration = null;
    private KioskManager        MyKiosk               = null;

    private TaskExecutionReport MyTaskExecutionReport;

    @Override
    public void onRequestPermissionsResult(int request_code, @NonNull String[] permissions, @NonNull int[] grant_results)
    {
        AppPermissions.onRequestPermissionsResult(this, request_code, permissions, grant_results);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

         super.onCreate(savedInstanceState);

         setContentView(R.layout.main);
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
         tvInformation = findViewById(R.id.textViewInformation);
         initDebugButtons();
         MyDeviceOwner = new DeviceOwner((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE),
                         new ComponentName(this, AdminReceiver.class),
                         new ComponentName(this, MainActivity.class),
                         new IntentFilter(Intent.ACTION_MAIN)
         );
         AppPermissions.verifyStandardPermissions(this);

         MyTaskExecutionReport = new TaskExecutionReport(Environment.getExternalStorageDirectory() + "/garlic-player/logs/");
         MyMainConfiguration = new MainConfiguration(new SharedPreferencesModel(this));
         MyMainConfiguration.checkForUUID();
         MyMainConfiguration.setIsDeviceRooted(AppPermissions.isDeviceRooted());
         MyMainConfiguration.togglePlayerInstalled(Installer.isGarlicPlayerInstalled(this));


         MyKiosk               = new KioskManager(MyDeviceOwner,
                                                new HomeLauncherManager(MyDeviceOwner, this, new Intent(Intent.ACTION_MAIN)),
                                                new LockTaskManager(this),
                                                MyMainConfiguration
        );

        if (!AppPermissions.hasStandardPermissions(this))
        {
            displayInformationText("Launcher needs read/write permissions for storage");
            return;
        }
        if (MyDeviceOwner.isDeviceOwner())
        {
            MyDeviceOwner.determinePermittedLockTaskPackages("");
            hideInformationText();
            ReceiverManager.registerAllReceiver(this);
            initButtonViews();
            startService(new Intent(this, WatchDogService.class)); // this is ok no nesting or leaks
            checkForInstalledPlayer();
        }
        else
        {
            if (MyMainConfiguration.isDeviceRooted())
            {
                displayInformationText(getString(R.string.root_found_set_device_owner));
                MyDeviceOwner.makeDeviceOwner(Runtime.getRuntime());
            }
            else
            {
                displayInformationText(getString(R.string.no_device_owner));
            }
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        toogleServiceModeVisibility();
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

    private void checkForNetwork()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        ProgressBar DownloadProgressBar = findViewById(R.id.progressDownload);
        DownloadProgressBar.setVisibility(View.VISIBLE);
        DownloadProgressBar.setProgress(0);

        assert connectivityManager != null;
        connectivityManager.registerDefaultNetworkCallback(new PlayerDownloader(this, DownloadProgressBar, tvInformation));
    }

    private void checkForInstalledPlayer()
    {
        if (MyMainConfiguration.isPlayerInstalled())
        {
            startGarlicPlayer();
            return;
        }
        if (!BuildConfig.DEBUG)
        {
            checkForNetwork();
            displayInformationText(getString(R.string.no_garlic_no_network));
        }
        else
        {
            displayInformationText("debug mode: no player");
        }
   }

    private void initButtonViews()
    {
        btToggleServiceMode  = findViewById(R.id.btToggleServiceMode);
        btStartPlayer        = findViewById(R.id.btStartPlayer);
        Button btAdminConfiguration = findViewById(R.id.btAdminConfiguration);
        Button btAndroidSettings    = findViewById(R.id.btAndroidSettings);

        if (MyMainConfiguration.isPlayerInstalled())
        {
            btStartPlayer.setVisibility(View.VISIBLE);
            hideInformationText();
        }
        else
        {
            btStartPlayer.setVisibility(View.INVISIBLE);
        }

        toogleServiceModeVisibility();

        if (MyKiosk.isStrictKioskModeActive())
        {
            btStartPlayer.setEnabled(false);
            btToggleServiceMode.setText(R.string.enter_service_mode);
            btAdminConfiguration.setVisibility(View.GONE);
            btAndroidSettings.setVisibility(View.GONE);
        }
        else
        {
            btAdminConfiguration.setVisibility(View.VISIBLE);
            btStartPlayer.setEnabled(true);
            btToggleServiceMode.setText(R.string.enter_strict_mode);
            btAndroidSettings.setVisibility(View.VISIBLE);
        }

        if (MyKiosk.startKioskMode() && btToggleLock != null) // Pin this app and set it as Launcher
        {
            btToggleLock.setText(R.string.unpin_app);
            btToggleLauncher.setText(R.string.restore_old_launcher);
        }
        NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));
    }


    public void initDebugButtons()
    {
        if (BuildConfig.DEBUG)
        {
            btToggleLock        = findViewById(R.id.btToggleLockTask);
            btToggleLauncher    = findViewById(R.id.btToggleLauncher);
            findViewById(R.id.layoutDebug).setVisibility(View.VISIBLE);
            btToggleLock.setVisibility(View.VISIBLE);
            btToggleLauncher.setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.layoutDebug).setVisibility(View.GONE);
            findViewById(R.id.btToggleLockTask).setVisibility(View.GONE);
            findViewById(R.id.btToggleLauncher).setVisibility(View.GONE);
        }
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
            btToggleLock.setText(R.string.unpin_app);
        }
        else
        {
            btToggleLock.setText(R.string.pin_app);
       }
    }

    public void toggleLauncher(View view)
    {
        if (MyKiosk.toggleHomeActivity())
        {
            btToggleLauncher.setText(R.string.restore_old_launcher);
        }
        else
        {
            btToggleLauncher.setText(R.string.become_launcher);
        }
    }



    public void toggleServiceMode(View view)
    {
        stopPlayerRestart(); // otherwise we start the countdown multiple times when recreate

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Service Login");
        alert.setMessage("Enter your password");
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String value = input.getText().toString();

                // we need temporary for testing an alternative for those one who forget passwords
                // so we get maybe the device UUID via
                // String alt_password = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                // or set something own
                String alt_password = "heidewitzka";

                if (MyMainConfiguration.compareServicePassword(value, new PasswordHasher()) || value.equals(alt_password))
                {
                    if (MyKiosk.isStrictKioskModeActive())
                    {
                        MyKiosk.toggleServiceMode(true);
                        btToggleServiceMode.setText(R.string.enter_strict_mode);
                        MyDeviceOwner.deactivateRestrictions();
                    }
                    else
                    {
                        MyKiosk.toggleServiceMode(false);
                        btToggleServiceMode.setText(R.string.enter_service_mode);
                        MyDeviceOwner.activateRestrictions();
                    }
                    recreate();
                }
                else
                {
                    startGarlicPlayerDelayed();
                }
            }
        });
        alert.show();
    }

    public void startGarlicPlayer()
    {
        if (MyMainConfiguration.hasNoPlayerStartDelayAfterBoot())
        {
            startGarlicPlayerInstantly(null);
        }
        else
        {
            startGarlicPlayerDelayed();
        }

    }

    public void startGarlicPlayerDelayed()
    {
        has_second_app_started = false;
        has_player_started     = false;

        if (MyMainConfiguration.getSmilIndex() == null)
        {
            btStartPlayer.setText(R.string.play);
            return;
        }
        if (is_countdown_running)
        {
            return;
        }

        int start_delay = MyMainConfiguration.getPlayerStartDelay();
        PlayerCountDown      = new CountDownTimer(start_delay * 1000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                btStartPlayer.setText(getString(R.string.count_down, String.valueOf(millisUntilFinished / 1000)));
                is_countdown_running = true;
            }

            public void onFinish()
            {
                btStartPlayer.setText(R.string.play);
                is_countdown_running = false;

                startGarlicPlayerInstantly(null);
            }

        }.start();
    }

    public void handleGarlicPlayerStartTimer(View view)
    {
        if (!btStartPlayer.getText().equals(getResources().getString(R.string.play)))
        {
            if (PlayerCountDown != null)
            {
                stopPlayerRestart();
            }
        }
        else
        {
            startGarlicPlayerInstantly(view);
        }
    }

    public void configAdmin(View view)
    {
        stopPlayerRestart();
        startActivity(new Intent(this, ActivityConfigAdmin.class));
    }

    public void configWiFi(View view)
    {
        stopPlayerRestart();
        startActivityForResult(new Intent(android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK), 0);
    }

    public void openAndroidSettings(View view)
    {
        stopPlayerRestart();
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    public void startGarlicPlayerInstantly(View view)
    {
        has_second_app_started = false;
        has_player_started     = true;
        NavigationBar.hide(this, MyMainConfiguration, new Intent(this, HUD.class));
        startApp(DeviceOwner.PLAYER_PACKAGE_NAME);
    }

    public void startSecondApp(String package_name)
    {
        has_second_app_started = true;
        has_player_started     = false;
        NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));
        MyDeviceOwner.determinePermittedLockTaskPackages(package_name);
        startApp(package_name);
    }

    private void startApp(String package_name)
    {
        Intent intent = getPackageManager().getLaunchIntentForPackage(package_name);
        if (intent == null)
        {
            displayInformationText(package_name + " do not exist");
            return;
        }
        startActivity(intent);
    }

    private void stopPlayerRestart()
    {
        has_second_app_started = false;
        has_player_started     = false;
        is_countdown_running   = false;
        if (PlayerCountDown != null)
        {
            PlayerCountDown.cancel();
        }
        btStartPlayer.setText(R.string.play);
    }

    private void toogleServiceModeVisibility()
    {
        if (MyMainConfiguration.hasActiveServicePassword())
        {
            btToggleServiceMode.setVisibility(View.VISIBLE);
        }
        else
        {
            btToggleServiceMode.setVisibility(View.GONE);
        }
    }

    private void displayInformationText(String error_text)
    {
        tvInformation.setText(error_text);
        tvInformation.setVisibility(View.VISIBLE);
    }

    private void hideInformationText()
    {
        tvInformation.setText("");
        tvInformation.setVisibility(View.INVISIBLE);
    }
}
