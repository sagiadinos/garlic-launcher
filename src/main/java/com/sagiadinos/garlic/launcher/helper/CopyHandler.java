/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2021 Nikolaos Sagiadinos <ns@smil-control.com>
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
package com.sagiadinos.garlic.launcher.helper;

import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class CopyHandler
{
    public CopyHandler()
    {
    }

    public void copy(File source, File destination) throws IOException
    {
        long source_size      = getSizeof(source);
        long destination_size = getFreeSpace(destination.getAbsolutePath().replace("/SMIL", ""));

        if (source_size > destination_size)
            throw new IOException(destination.getAbsolutePath() + " has not enough space.");

        if (source.isDirectory())
        {
            copyDirectory(source, destination);
        }
        else
        {
            copyFile(source, destination);
        }
    }

    public void removeRecursively(File file) throws IOException
    {
        if (!file.exists())
            return;

        if (file.isDirectory())
        {
            for (File f : Objects.requireNonNull(file.listFiles()))
            {
                removeRecursively(f);
            }
        }
        if (!file.delete())
            throw new IOException(file.getAbsolutePath() + " could not be deleted.");
    }

    private void copyDirectory(File source, File destination) throws IOException
    {
        if (!destination.exists() && !destination.mkdir())
        {
            throw new IOException(destination.getAbsolutePath() + " could not be created.");
        }

        for (String f : Objects.requireNonNull(source.list()))
        {
            File dir_file = new File(source, f);
            if (dir_file.isDirectory())
            {
                copyDirectory(dir_file, new File(destination, f));
            }
            else
            {
                copyFile(dir_file, new File(destination, f));
            }
        }
    }

    private long getSizeof(File source)
    {
        long size = 0;
        if (source.isDirectory())
            size = getFolderSize(source);
        else
            size += source.length();

        return size;
    }

    private long getFolderSize(File dir)
    {
        long size = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile()) {
                System.out.println(file.getName() + " " + file.length());
                size += file.length();
            }
            else
                size += getFolderSize(file);
        }
        return size;
    }

    private long getFreeSpace(String path)
    {
        StatFs stat = new StatFs(path);
        return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    }

    private void copyFile(File source, File destination) throws IOException
    {
        InputStream in   = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
    }
}
