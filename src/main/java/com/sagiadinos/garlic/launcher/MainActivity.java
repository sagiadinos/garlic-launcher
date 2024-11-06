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
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
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
import android.util.Log;
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
import com.sagiadinos.garlic.launcher.helper.LauncherCountDownTimer;
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
import com.sagiadinos.garlic.launcher.helper.VersionInformation;
import com.sagiadinos.garlic.launcher.receiver.AdminReceiver;
import com.sagiadinos.garlic.launcher.receiver.ReceiverManager;
import com.sagiadinos.garlic.launcher.services.HUD;
import com.sagiadinos.garlic.launcher.services.WatchDogService;

import java.io.File;
import java.util.Objects;

public class MainActivity extends Activity
{
    public enum PlayerState {
        STOPPED,
        WAITING,
        PLAYING
    }
    private PlayerState current_player_state = PlayerState.STOPPED;

    private boolean        has_second_app_started = false;
    private Button         btToggleServiceMode = null;
    private Button         btStartPlayer = null;
    private TextView       tvInformation   = null;
    private LauncherCountDownTimer PlayerCountDown        = null;
    private DeviceOwner         MyDeviceOwner          = null;
    private MainConfiguration   MyMainConfiguration = null;
    private AppPermissions      MyAppPermissions;
    private KioskManager        MyKiosk               = null;
    private ReceiverManager     MyReceiverManager = null;
   // private TaskExecutionReport MyTaskExecutionReport;
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
        tvInformation       = findViewById(R.id.tvInformation);
        MyMainConfiguration = new MainConfiguration(new SharedPreferencesModel(this));

        if (MyMainConfiguration.isFirstStart())
            MyMainConfiguration.firstStart(new RootChecker());

        MyMainConfiguration.convertValues();

        // init screen area
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        MyScreen = new Screen(displayMetrics);
        MyActivityManager = (ActivityManager)  this.getSystemService(Context.ACTIVITY_SERVICE);
        NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));

        MyDeviceOwner = new DeviceOwner((DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE),
                new ComponentName(this, AdminReceiver.class),
                new ComponentName(this, MainActivity.class),
                new IntentFilter(Intent.ACTION_MAIN)
        );
        // if not Device Ownder
        if (!MyDeviceOwner.isDeviceOwner() && MyMainConfiguration.isDeviceRooted())
        {
            displayInformationText(getString(R.string.root_found_set_device_owner));
            if (!MyDeviceOwner.makeDeviceOwner(new ShellExecute(Runtime.getRuntime())))
                displayInformationText("Device is rooted, but set device owner failed. If you created a Google account, delete it. Otherwise contact support.");
        }

        if (!MyDeviceOwner.isDeviceOwner())
        {
            displayInformationText(getString(R.string.no_device_owner));
            return;
        }

        // at this point we are Device Owner, so, check permissions
        MyAppPermissions = new AppPermissions(this, MyMainConfiguration, new ShellExecute(Runtime.getRuntime()));
        if (!AppPermissions.hasAllPermissions(this))
            MyAppPermissions.handleAllPermissions();

        // ATTENTION!
        // Do not insert the rows below in a onStart -method, cause it will slow down an back to app
        // respectively a restart dramatically! e.g. when you close a player regular
        // continue only when permissions are granted

// temporary getPackageManager().canRequestPackageInstalls() in AppPermissions.hasAllPermissions() always
// returns false as we target Minimum API 25. Even in Android >= 8. With minimum API 26 in build.gradle it works as expected
// Google make a big shit hole of Android

/*       if (AppPermissions.hasAllPermissions(this))
        {
 */           cleanUp(Environment.getExternalStorageDirectory().getAbsolutePath());
            cleanUp(Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath().replace("garlic.launcher","garlic.player"));
 /*       }
        else
        {
            displayInformationText(getString(R.string.no_basic_permissions));
            return;
        }
*/
        boolean is_player_installed = Installer.isMediaPlayerInstalled(this);
        MyMainConfiguration.togglePlayerInstalled(is_player_installed);
        if (is_player_installed && !Installer.hasPlayerPermissions(this)
                && MyAppPermissions.grantPlayerPermissions())
        {
         /*   DeviceOwner.reboot(
                    (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE),
                    new ComponentName(this, AdminReceiver.class)
            );
        */}

        // MyTaskExecutionReport = new TaskExecutionReport(Objects.requireNonNull(getExternalFilesDir("logs")).getAbsolutePath());
        MyKiosk = new KioskManager(MyDeviceOwner,
                new HomeLauncherManager(this, new Intent(Intent.ACTION_MAIN)),
                this,
                MyMainConfiguration
        );
        PlayerCountDown = new LauncherCountDownTimer(this);

        MyKiosk.pin();
        MyDeviceOwner.determinePermittedLockTaskPackages("");
        hideInformationText();
        MyReceiverManager = new ReceiverManager(this);
        MyReceiverManager.registerAllReceiver();
        initButtonViews();
        checkForInstalledPlayer();
        checkForNetwork();
    }

      @Override
    protected void onResume()
    {
        if (MyDeviceOwner.isDeviceOwner() && AppPermissions.hasAllPermissions(this))
        {
            if (MyActivityManager.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE)
                MyKiosk.pin();

            NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));
            if (MyInfoLine != null)
                MyInfoLine.refreshFreeDiscSpace();
        }
        super.onResume();
    }

    @Override
    public void onRestart()
    {
        if (MyDeviceOwner.isDeviceOwner() && AppPermissions.hasAllPermissions(this))
            toogleServiceModeVisibility();

        super.onRestart();
    }

    @Override
    protected void onDestroy()
    {
        // Attention: MyDeviceOwner and dependent classes like MyReceiverManager can be null when access rights are denied
        if (MyDeviceOwner != null && MyDeviceOwner.isDeviceOwner() && AppPermissions.hasAllPermissions(this))
        {
            MyReceiverManager.unregisterAllReceiver();
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (shouldIgnoreTouchEvent())
            return super.onTouchEvent(event);

        if (isTouchInPermitArea(event))
        {
            stopPlayerAndExitKioskMode();
            launchOtherLauncher();
        }

        return super.onTouchEvent(event);
    }

    private boolean shouldIgnoreTouchEvent()
    {
        return !MyDeviceOwner.isDeviceOwner() ||
                (MyKiosk != null && MyKiosk.isStrictKioskModeActive());
    }

    private boolean isTouchInPermitArea(MotionEvent event)
    {
        return event.getActionMasked() == MotionEvent.ACTION_DOWN &&
                MyScreen.isEventInPermitUIArea((int) event.getX(), (int) event.getY());
    }

    private void stopPlayerAndExitKioskMode()
    {
        stopPlayerRestart();
        stopLockTask();
    }

    private void launchOtherLauncher()
    {
        ResolveInfo resolveInfo = Installer.determineOtherLauncherPackagename(getPackageManager());
        if (resolveInfo == null)
        {
            Log.e("Launcher", "Other launcher package not found");
            return;
        }

        ComponentName name = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(name);

        try
        {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Log.e("Launcher", "Launcher activity not found", e);
        }

    }

    private void checkForNetwork()
    {
        initUIElements();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            Log.e("NetworkCheck", "ConnectivityManager not available");
            return;
        }

        initInfoLine();
        connectivityManager.registerDefaultNetworkCallback(MyInfoLine);
    }

    private void initUIElements() {
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        TextView tvFreeDiscSpace = findViewById(R.id.tvFreeDiscSpace);
        TextView tvIP = findViewById(R.id.tvIP);

        tvIP.setVisibility(View.VISIBLE);
        tvFreeDiscSpace.setVisibility(View.VISIBLE);
    }

    private void initInfoLine()
    {
        TextView tvAppVersion = findViewById(R.id.tvAppVersion);
        TextView tvFreeDiscSpace = findViewById(R.id.tvFreeDiscSpace);
        TextView tvIP = findViewById(R.id.tvIP);

        VersionInformation myVersionInformation = new VersionInformation(this);
        MyInfoLine = new InfoLine(this, myVersionInformation, MyMainConfiguration, MyDiscSpace, tvAppVersion, tvFreeDiscSpace, tvIP);
        MyInfoLine.displayAppInformation();
        MyInfoLine.refreshFreeDiscSpace();
    }


    private void checkForPlayerDownload()
    {
        if (!BuildConfig.DEBUG) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            ProgressBar downloadProgressBar = findViewById(R.id.progressDownload);
            downloadProgressBar.setVisibility(View.VISIBLE);
            downloadProgressBar.setProgress(0);

            if (connectivityManager != null)
                connectivityManager.registerDefaultNetworkCallback(new PlayerDownloader(this, downloadProgressBar, tvInformation));
            else
                displayInformationText(getString(R.string.no_network));
        }
    }

    private void checkForInstalledPlayer()
    {
        if (Installer.hasPlayerPermissions(this) && MyMainConfiguration.isPlayerInstalled())
        {
            startGarlicPlayer();
            return;
        }

        checkForPlayerDownload();
        displayInformationText(getString(R.string.no_garlic));
   }

    private void initButtonViews()
    {
        btToggleServiceMode  = findViewById(R.id.btToggleServiceMode);
        btStartPlayer        = findViewById(R.id.btStartPlayer);

        if (BuildConfig.DEBUG)
        {
            Button btForTest = findViewById(R.id.btForTest);
            btForTest.setVisibility(View.VISIBLE);
        }

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

    public void setPlayerStatus(PlayerState state)
    {
        current_player_state = state;
    }

    public boolean isPlayerPlaying()
    {
        return current_player_state == PlayerState.PLAYING;
    }

    public void toggleServiceMode(View view)
    {
        stopPlayerRestart(); // otherwise we start the countdown multiple times when recreate

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Service Login");
        alert.setMessage("Enter your password");
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", (dialog, whichButton) ->
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

    public void setBtStartPlayerText(String s)
    {
        btStartPlayer.setText(s);
    }

    public void startGarlicPlayerDelayed()
    {
        if (MyMainConfiguration.getSmilIndex() == null)
        {
            btStartPlayer.setText(R.string.play);
            return;
        }

        current_player_state = PlayerState.WAITING;
        PlayerCountDown.setCountDown(MyMainConfiguration.getPlayerStartDelay() * 1000L, 1000);
        PlayerCountDown.start();

    }

    public void handleTestButton(View view)
    {
        Intent i = new Intent("com.sagiadinos.garlic.launcher.receiver.CommandReceiver");
        i.putExtra("command", "screen_off");
        i.putExtra("task_id", "app_installed");
        sendBroadcast(i);
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
        PlayerCountDown.cancel();
        has_second_app_started = false;
        MyMainConfiguration.toggleJustBooted(false);
        NavigationBar.hide(this, MyMainConfiguration, new Intent(this, HUD.class));
        startService(new Intent(this, WatchDogService.class)); // this is ok no nesting or leaks
        current_player_state   = PlayerState.PLAYING;
        startApp(DeviceOwner.PLAYER_PACKAGE_NAME);
    }

    public void startSecondApp(String package_name)
    {
        has_second_app_started = true;
        current_player_state   = PlayerState.STOPPED;
        NavigationBar.show(this, MyMainConfiguration, new Intent(this, HUD.class));
        MyDeviceOwner.determinePermittedLockTaskPackages(package_name);
        stopService(new Intent(this, WatchDogService.class)); // this is ok no nesting or leaks
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
        current_player_state   = PlayerState.STOPPED;
        stopService(new Intent(this, WatchDogService.class));
        PlayerCountDown.cancel();

        if (btStartPlayer != null) // prevent crash if exit launcher without rights
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
