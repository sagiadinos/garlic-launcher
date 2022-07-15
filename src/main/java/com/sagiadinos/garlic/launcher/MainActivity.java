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
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;


import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.helper.CleanUp;
import com.sagiadinos.garlic.launcher.helper.DiscSpace;
import com.sagiadinos.garlic.launcher.helper.InfoLine;
import com.sagiadinos.garlic.launcher.helper.Installer;
import com.sagiadinos.garlic.launcher.helper.NavigationBar;
import com.sagiadinos.garlic.launcher.configuration.PasswordHasher;
import com.sagiadinos.garlic.launcher.helper.PlayerDownloader;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import com.sagiadinos.garlic.launcher.helper.DeviceOwner;
import com.sagiadinos.garlic.launcher.helper.HomeLauncherManager;
import com.sagiadinos.garlic.launcher.helper.KioskManager;
import com.sagiadinos.garlic.launcher.helper.AppPermissions;
import com.sagiadinos.garlic.launcher.helper.RootChecker;
import com.sagiadinos.garlic.launcher.helper.Screen;
import com.sagiadinos.garlic.launcher.helper.ShellExecute;
import com.sagiadinos.garlic.launcher.helper.TaskExecutionReport;
import com.sagiadinos.garlic.launcher.helper.VersionInformation;
import com.sagiadinos.garlic.launcher.receiver.AdminReceiver;
import com.sagiadinos.garlic.launcher.receiver.ReceiverManager;
import com.sagiadinos.garlic.launcher.services.HUD;
import com.sagiadinos.garlic.launcher.services.WatchDogService;

import java.io.File;
import java.util.Objects;

public class MainActivity extends Activity
{
    private boolean        has_second_app_started = false;
    private boolean        has_player_started     = false;
    private boolean        is_countdown_running   = false;

    private Button         btToggleServiceMode = null;
    private Button         btStartPlayer = null;
    private TextView       tvInformation   = null;
    private TextView       tvAppVersion    = null;
    private TextView       tvFreeDiscSpace = null;
    private TextView       tvIP    = null;

    private CountDownTimer      PlayerCountDown        = null;
    private DeviceOwner         MyDeviceOwner          = null;
    private MainConfiguration   MyMainConfiguration = null;
    private KioskManager        MyKiosk               = null;
    private ReceiverManager     MyReceiverManager = null;
    private TaskExecutionReport MyTaskExecutionReport;
    private AppPermissions      MyAppPermissions;
    private Screen              MyScreen;
    private ActivityManager     MyActivityManager;
    private DiscSpace           MyDiscSpace  = null;
    private InfoLine            MyInfoLine   = null;

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
        tvInformation    = findViewById(R.id.tvInformation);
        MyMainConfiguration = new MainConfiguration(new SharedPreferencesModel(this));
        if (MyMainConfiguration.isFirstStart())
        {
            MyMainConfiguration.firstStart(new RootChecker());
        }

        // init screen area
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        MyScreen = new Screen(displayMetrics);
        MyActivityManager = (ActivityManager)  this.getSystemService(Context.ACTIVITY_SERVICE);

        NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));

        MyAppPermissions = new AppPermissions(this, MyMainConfiguration);
        if (!AppPermissions.hasImportantPermissions(this))
        {
            MyAppPermissions.handlePermissions(new ShellExecute(Runtime.getRuntime()));
        }
        boolean is_player_installed = Installer.isMediaPlayerInstalled(this);
        MyMainConfiguration.togglePlayerInstalled(is_player_installed);
        if (is_player_installed && !Installer.hasPlayerPermissions(this)
                  && MyAppPermissions.grantPlayerPermissions(new ShellExecute(Runtime.getRuntime())))
        {
            DeviceOwner.reboot(
                    (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE),
                new ComponentName(this, AdminReceiver.class)
          );
        }
         // ATTENTION!
        // Do not insert the rows below in a onStart -method, cause it will slow down an back to app
        // respectivetely a restart dramatically! e.g. when you close a player regulary
        // continue only when permissions are granted
        if (!AppPermissions.hasImportantPermissions(this))
        {
            return;
        }
        cleanUp(Environment.getExternalStorageDirectory().getAbsolutePath());
        cleanUp(Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath().replace("garlic.launcher","garlic.player"));

         MyDeviceOwner = new DeviceOwner((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE),
                new ComponentName(this, AdminReceiver.class),
                new ComponentName(this, MainActivity.class),
                new IntentFilter(Intent.ACTION_MAIN)
         );
         MyTaskExecutionReport = new TaskExecutionReport(getExternalFilesDir("logs").getAbsolutePath());
         MyKiosk               = new KioskManager(MyDeviceOwner,
                                                  new HomeLauncherManager(this, new Intent(Intent.ACTION_MAIN)),
                                                  this,
                                                  MyMainConfiguration
        );
        if (MyDeviceOwner.isDeviceOwner())
        {
            startLockTask();

            MyDeviceOwner.determinePermittedLockTaskPackages("");
            hideInformationText();
            MyReceiverManager = new ReceiverManager(this);
            MyReceiverManager.registerAllReceiver();
            initButtonViews();
            startService(new Intent(this, WatchDogService.class)); // this is ok no nesting or leaks
            checkForInstalledPlayer();
        }
        else
        {
            if (MyMainConfiguration.isDeviceRooted())
            {
                displayInformationText(getString(R.string.root_found_set_device_owner));
                if (!MyDeviceOwner.makeDeviceOwner(new ShellExecute(Runtime.getRuntime())))
                    displayInformationText("Device is rooted, but set device owner failed. If you created a Google account, delete it. Otherwise contact support.");
            }
            else
            {
                displayInformationText(getString(R.string.no_device_owner));
            }
        }
        checkForNetWork();
    }

      @Override
    protected void onResume()
    {
      //  handleDailyReboot();
        // Attention: MyDeviceOwner and dependent classes like MyKiosk can be null when access rights are denied
        if (MyActivityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE)
        startLockTask();

        NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));
        if (MyInfoLine != null)
            MyInfoLine.refreshFreeDiscSpace();

        super.onResume();
    }

    @Override
    public void onRestart()
    {
        // Attention: MyDeviceOwner and dependent classes like MyKiosk can be null when access rights are denied
        toogleServiceModeVisibility();
        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        // Attention: MyDeviceOwner and dependent classes like MyReceiverManager can be null when access rights are denied
        if (MyDeviceOwner != null && MyDeviceOwner.isDeviceOwner())
        {
            MyReceiverManager.unregisterAllReceiver();
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!MyKiosk.isStrictKioskModeActive() &&
                event.getActionMasked() == MotionEvent.ACTION_DOWN &&
                MyScreen.isEventInPermitUIArea((int)event.getX(), (int)event.getY()))
        {
            stopPlayerRestart();
            stopLockTask();

            // start other Launcher Activity
            ResolveInfo resolveInfo = Installer.determineOtherLauncherPackagename(getPackageManager());

            assert resolveInfo != null;
            ComponentName name=new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            Intent i=new Intent(Intent.ACTION_MAIN);

            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            startActivity(i);
        }

        return super.onTouchEvent(event);
    }

    private void checkForNetWork()
    {
        tvAppVersion     = findViewById(R.id.tvAppVersion);
        tvFreeDiscSpace  = findViewById(R.id.tvFreeDiscSpace);
        tvIP             = findViewById(R.id.tvIP);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        VersionInformation MyVersionInformation = new VersionInformation(this);
        MyInfoLine = new InfoLine(this, MyVersionInformation, MyMainConfiguration, MyDiscSpace, tvAppVersion, tvFreeDiscSpace, tvIP);
        MyInfoLine.displayAppInformation();
        MyInfoLine.refreshFreeDiscSpace();
        assert connectivityManager != null;
        connectivityManager.registerDefaultNetworkCallback(MyInfoLine);

    }

    private void checkForPlayerDownload()
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
        if (Installer.hasPlayerPermissions(this) && MyMainConfiguration.isPlayerInstalled())
        {
            startGarlicPlayer();
            return;
        }

        checkForPlayerDownload();
        displayInformationText(getString(R.string.no_garlic_no_network));
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
        MyKiosk.becomeHomeActivity();
    }

    public boolean hasSecondAppStarted()
    {
        return has_second_app_started;
    }

    public boolean hasPlayerStarted()
    {
        return has_player_started;
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
                if (value.isEmpty())
                {
                    startGarlicPlayerDelayed();
                    return;
                }

                // we need temporary for testing an alternative for those one who forget passwords
                // so we get maybe the device UUID via
                // String alt_password = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                // or set something own
                // String alt_password = "heidewitzka";

                if (MyMainConfiguration.compareServicePassword(value, new PasswordHasher())/* || value.equals(alt_password)*/)
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
         if (MyMainConfiguration.hasNoPlayerStartDelayAfterBoot() && MyMainConfiguration.isJustBooted())
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

    public void openAndroidSettings(View view)
    {
        stopPlayerRestart();
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    public void startGarlicPlayerInstantly(View view)
    {
        has_second_app_started = false;
        has_player_started     = true;
        MyMainConfiguration.toggleJustBooted(false);
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

    private void cleanUp(String path)
    {
        File f = new File(path);
        if (f.exists())
        {
            if (MyDiscSpace == null) // because we need it only one time
            {
                MyDiscSpace  = new DiscSpace(new StatFs(path), path);
            }
            CleanUp MyCleanUp = new CleanUp(path, MyDiscSpace);
            MyCleanUp.removeAll();
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
