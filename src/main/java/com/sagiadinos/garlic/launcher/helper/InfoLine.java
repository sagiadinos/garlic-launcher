package com.sagiadinos.garlic.launcher.helper;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.widget.TextView;

import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;

import org.jetbrains.annotations.NotNull;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class InfoLine extends ConnectivityManager.NetworkCallback
{
    private final VersionInformation MyVersionInformation;
    private MainConfiguration MyMainConfiguration;
    private DiscSpace         MyDiscSpace;
    private TextView tvAppVersion, tvFreeDiscSpace, tvIP;
    private Activity          MyActivity;
    private Runnable          runnable;

    public InfoLine(Activity a,
                    VersionInformation myVersionInformation,
                    MainConfiguration myMainConfiguration,
                    DiscSpace myDiscSpace,
                    TextView tvAppVersion,
                    TextView tvFreeDiscSpace,
                    TextView tvIP)
    {
        MyVersionInformation = myVersionInformation;
        MyMainConfiguration  = myMainConfiguration;
        MyDiscSpace          = myDiscSpace;
        MyActivity           = a;
        this.tvAppVersion    = tvAppVersion;
        this.tvFreeDiscSpace = tvFreeDiscSpace;
        this.tvIP            = tvIP;
    }

    @Override
    public void onAvailable(@NotNull Network network)
    {
        Handler handler = new Handler();
        displayAppInformation();

        runnable = new Runnable()
        {
            public void run()
            {
                String ip = getLocalIpAddress();
                if (ip == null)
                    handler.postDelayed(runnable, 2000);
                else
                    setIPText(ip);
            }

        };
        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onLost(@NotNull Network network)
    {
        setIPText("");
    }

    public void displayAppInformation()
    {
        final String value = "Launcher: " +
                MyVersionInformation.forLauncher() +
                " | Player: " + MyVersionInformation.forPlayer() +
                " | UUUID: " + MyMainConfiguration.getUUID();

        MyActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tvAppVersion.setText(value);
            }
        });
    }

    public void refreshFreeDiscSpace()
    {
        MyDiscSpace.refresh();
        final String value =" | Free: " + MyDiscSpace.getFreePercent()  +" %";

        MyActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                tvFreeDiscSpace.setText(value);
            }
        });
    }



    private void setIPText(final String ip)
    {
        if (ip.isEmpty())
            return;

        final String value =" | IP: " + ip;

        MyActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                tvIP.setText(value);
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


