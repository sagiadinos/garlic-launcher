# Garlic-Launcher - Android Launcher for Digital Signage

The Garlic-Launcher is useful to create digital signage media player on Android-hardware in combination with the [garlic-player](https://garlic-player.com).

** A rooted Android is not necessary! **

** You can transfer nearly every consumer hardware to a robust Digital Signage system! **

If you want to build a digital signage mediaplayer hardware or an interactive kiosk system which should run 24/7, it is necessary to take some precautions.

For example:
- You do not want the user can close or uninstall your Kiosk-App or jump into the operating system
- It must be ensured that the player-app is running
- The player-app must be able to restart in case of an error or crash
- When the player gets his content via network it must be able to get administrated, updated, and rebooted remotly
- Manual configuration must be as easy as possible
- Security

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
- Tiny and resource efficient signed apk is < 50KB
- automatic player download when network is active
- Custom back button, for some rooted AOSP images which deactivate navigation bar totally via settings
- Strict Kiosk
- Password secured Service/Maintance mode to administrate strict systems manually

## Requirements
 - Android >= 7.1.1 
 - Garlic-launcher must set as Device Owner

Garlic-launcher is tested also with Android 9 on a non-rooted consumer Tablet.
