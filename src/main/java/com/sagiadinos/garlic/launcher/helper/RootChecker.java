package com.sagiadinos.garlic.launcher.helper;

import com.sagiadinos.garlic.launcher.BuildConfig;

import java.io.File;

public class RootChecker
{
    private final String[] binaryPaths= {
            "/data/local/",
            "/data/local/bin/",
            "/data/local/xbin/",
            "/sbin/",
            "/su/bin/",
            "/system/bin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/system/usr/we-need-root/",
            "/system/xbin/",
            "/system/app/Superuser.apk",
            "/cache",
            "/data",
            "/dev"
    };

    private boolean is_rooted = false;
    private boolean is_checked = false;

    public RootChecker()
    {
        is_rooted = false;
        is_checked = false;
    }

    public boolean isDeviceRooted()
    {
        if (!is_checked)
        {
            checkForRoot();
        }

        return is_rooted;
    }

    /**
     *  Look at https://medium.com/@deekshithmoolyakavoor/root-detection-in-android-device-9144b7c2ae07
     *
     */
    private void checkForRoot()
    {
        is_rooted  = (checkForBinary("su") || checkForBinary("busybox"));
        is_checked = true;
    }

    private boolean detectTestKeys()
    {
        // as Android Studio Emulator images have a test-keys - string in kernel
      /*  if (BuildConfig.DEBUG)
            return false;
*/
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkForBinary(String filename)
    {
        for (String path : binaryPaths)
        {
            File f = new File(path, filename);
            boolean fileExists = f.exists();
            if (fileExists)
            {
                return true;
            }
        }
        return false;
    }
}
