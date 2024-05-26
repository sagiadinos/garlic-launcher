# Garlic-Launcher - Android Launcher for Digital Signage

The Garlic-Launcher is useful to create a [digital signage player](https://smil-control.com/magazine/what-is-a-digital-signage-player/) on Android hardware in combination with the [garlic-player](https://garlic-player.com).

**A rooted Android is not necessary!**

**You can transfer nearly every consumer hardware with device owner support to a robust Digital Signage system!**

If you want to build a Digital Signage Media Player Hardware or an interactive Kiosk System which should run 24/7, it is necessary to take some precautions.

For example:
- [Security](https://smil-control.com/magazine/digital-signage-security/): You do not want the user can close or uninstall your Kiosk-App or jump into the operating system
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
- set screen off/on
- deep standby for supported devices
- Support remote software updates with garlic player
- Software updates via USB-Stick/SD-Card
- Configuration via USB-Stick/SD-Card
- A second app can be opened
- Tiny and resource efficient. The signed apk is less than 80KB
- automatic player download when network is active
- Custom back button as overlay for some rooted images (explanation down)
- Strict Kiosk Mode. (explanation down)
- Password secured "Service Mode" for administrating systems in "Strict Kiosk Mode" manually

## Requirements
 - Android >= 7.1.1 
 - Garlic-launcher must set as Device Owner

Garlic-launcher is tested also with Android 9 on a non-rooted consumer Tablet.

## Binary Download

You can download latest signed build from our CI [here](https://garlic-player.com/downloads/ci-builds/latest_android_launcher.apk)

## Installation

[Installation-Guide](https://github.com/sagiadinos/garlic-launcher/wiki/Installation)

## Configuration

[Settings-Guide](https://github.com/sagiadinos/garlic-launcher/wiki/Launcher_Settings)
