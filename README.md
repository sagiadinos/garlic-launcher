# Garlic-Launcher - Android Launcher for Digital Signage

The Garlic-Launcher is useful to create digital signage media player on Android-hardware in combination with the [garlic-player](https://garlic-player.com).

**A rooted Android is not necessary!**

**You can transfer nearly every consumer hardware to a robust Digital Signage system!**

If you want to build a Digital Signage Media Player Hardware or an interactive Kiosk System which should run 24/7, it is necessary to take some precautions.

For example:
- Security: You do not want the user can close or uninstall your Kiosk-App or jump into the operating system
- It must be ensured that the player-app is running
- The player-app must be able to restart in case of an error or crash.
- When the media player gets his content only by network, it must be able to get administrated, updated, and rebooted after a remote request
- Manual configuration must be possible

One solution is to root Android, but rooted devices are a potential security riscs. So garlic-launcher use the [Device Owner Mode](https://developer.android.com/reference/android/app/admin/DevicePolicyManager)

## Features
- Works as device owner, so no root is needed
- The app will be start pinned and as system launcher (Kiosk mode)
- Watchdog service checks if garlic-player is running
- OS reboots
- Support remote software updates with garlic player
- Software updates via USB-Stick/SD-Card
- Configuration via USB-Stick/SD-Card
- A second app can be opened
- Tiny and resource efficient. The signed apk is under 50KB
- automatic player download when network is active
- Custom back button as overlay for some rooted images (explanation down)
- Strict Kiosk Mode. (explanation down)
- Password secured "Service Mode" for administrating systems in "Strict Kiosk Mode" manually

### Why you could need a custom back button?

Some (mostly chinese) manufacturers builds rooted Android images (AOSP) on customer request, which have additional options in their settings. Amongst other things often there is an opportunity to disable the navigation bar totally in these Unfortunately every manufacturer provides his own API for that. So instead of supporting dozens of different manufacturers APIs for hide/show I decide to create a custom Back-Button which can be activated in the case it should needed. For example when starting a second app, a browser, do some maintance etc...

Personally I prefer to use regulary supported features like the so called "immersive mode", but there are customers which do not want a visible navigation bar in their kiosk systems under any circumstances. So there is a back button overlay possible.

### What is the Strict Kiosk Mode?

The "Normal Kiosk Mode" means that garlic-launcher is pinned, so it is not possibility to exit into the known Android-UI. However, you are still able to install/uninstall apps or configurate your device manually or remotely from a CMS. That is the common use case for the most requirements.

In some high-security environments this is a no-go. To cover this you can activate the so called "Strict Kiosk Mode". This decativates the config buttons in garlic-launcher and prevent the installing/uninstalling of apps, the modifying of accounts and some more things. So even on a application crash it is not possible to manipulate apps or user. If a maintenance technician needs physical access to the device, there is a passsword secured "Service Mode"-Button which temporary deactivates the restrictions.

The password for activating the "Service-Mode" must be set personally and is not recoverable. For security reasons, the password is stored hashed with an individual salt that is new created each time. So if the password is choosen wisely it cannnot be attacked via brute-force or with [rainbow tables](https://en.wikipedia.org/wiki/Rainbow_table).

## Requirements
 - Android >= 7.1.1 
 - Garlic-launcher must set as Device Owner

Garlic-launcher is tested also with Android 9 on a non-rooted consumer Tablet.

## Download & Installation

You can download latest signed build from our CI [here](https://garlic-player.com/downloads/ci-builds/latest_android_launcher.apk)

## Installation (read carefully first)

You need unprovisioned Android Hardware. This means a device which has not configured an playstore account.

For example: You could do a factory reset first, but although I assume that you know what you are doing:

**Don't do this on productive media device, tablets or smartphone, cause a factory reset will delete all your data!**

Activate the "Developer options" then enter:

`adb shell install "/YOUR_PATH_TO_APK/latest_android.apk"`

## Set Garlic-Launcher as Device Owner

Garlic-launcher is able to detect a rooted device. In this case the app can set the Device Owner mode by itself.

If the device is not rooted, you can set the Device Owner with following adb command:

`adb shell dpm set-device-owner com.sagiadinos.garlic.launcher/.receiver.AdminReceiver`

Currently my Company prepares a web process in our SmilControl-CMS to provision Android Devices much easier via a simple qr-code.

Device Owner is needed, cause it makes all the magic like silent installation, configurations, reboot... etc possible.

## Finally Installation of a Digital Signage Media Player 
 
Garlic-launcher will start to download the latest [Digital Signage Media Player](https://github.com/sagiadinos/garlic-player) automatically after it is set as Device owner and has access to a network.
