package com.sagiadinos.garlic.launcher.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.BuildConfig;
import com.sagiadinos.garlic.launcher.R;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class InfoLine  extends ConnectivityManager.NetworkCallback
{
    private VersionInformation MyVersionInformation;
    private MainConfiguration MyMainConfiguration;
    private DiscSpace         MyDiscSpace;
    private TextView          tvInformation;
    private Activity          MyActivity;
    private Runnable          runnable;

    public InfoLine(Activity a, VersionInformation myVersionInformation, MainConfiguration myMainConfiguration, DiscSpace myDiscSpace, TextView tvInformation)
    {
        MyVersionInformation = myVersionInformation;
        MyMainConfiguration = myMainConfiguration;
        MyDiscSpace         = myDiscSpace;
        MyActivity          = a;
        this.tvInformation = tvInformation;
    }

    @Override
    public void onAvailable(@NotNull Network network)
    {
        Handler handler = new Handler();
        displayPartialInformation();

        runnable = new Runnable()
        {
            public void run()
            {
                String ip = getLocalIpAddress();
                if (ip == null)
                    handler.postDelayed(runnable, 2000);
                else
                    setInformationText(tvInformation, getFullInformation());
            }

        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onLost(@NotNull Network network)
    {
        displayPartialInformation();
    }

    public String getFullInformation()
    {
        return getPartialInformation() + getIP();
    }

    public String getPartialInformation()
    {
        return getVersionInformation() + getFreeDiscSpaceInPercent();
    }

    public void displayPartialInformation()
    {
        setInformationText(tvInformation, getPartialInformation());
    }


    public String getVersionInformation()
    {
        return "Launcher: " +
                MyVersionInformation.forLauncher() +
                " | Player: " + MyVersionInformation.forPlayer() +
                " | UUUID: " + MyMainConfiguration.getUUID();
    }

    public String getIP()
    {
        return " | IP: " + getLocalIpAddress();
    }


    public String getFreeDiscSpaceInPercent()
    {
        return " | Free: " + MyDiscSpace.getFreePercent()  +" %";
    }

    private void setInformationText(final TextView text, final String value)
    {
        MyActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}


