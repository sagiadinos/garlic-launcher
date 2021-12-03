package com.sagiadinos.garlic.launcher.helper;

import java.io.File;
import java.util.LinkedList;

public class CleanUp
{
    LinkedList<String> founded_file_paths;
    DiscSpace          MyDiscSpace;
    String             pattern           = null;
    String             external_directory;
    final int          min_free_percent  = 15;

    public CleanUp(String ext_dir, DiscSpace ds)
    {
        external_directory = ext_dir;
        MyDiscSpace        = ds;
        founded_file_paths = new LinkedList<>();
    }

    public void removeAll()
    {
        removePlayerApks();
        removeXMLFiles();
        removeCache();
    }

    public void removePlayerApks()
    {
        pattern          = "garlic-player.apk";
        removeFiles();
    }

    public void removeXMLFiles()
    {
        pattern          = ".xml";
        removeFiles();
    }

    public void removeCache()
    {
        if (isMinFreeReached())
        {
            deleteRecursive(new File(external_directory + "/garlic-player/cache/"));
        }
    }

    private void removeFiles()
    {
        founded_file_paths.clear();
        findFilesInDirectory(new File(external_directory + "/garlic-player/cache/"));
        removeFoundedFiles();
    }

    private void findFilesInDirectory(File dir)
    {
        if (!dir.exists())
            return;

        // FileFilter/FilenameFilter also checks every file in Dir so who cares
        File[] FilesList = dir.listFiles();
        if (FilesList == null)
            return;

        for (File file : FilesList)
        {
            if (!file.isDirectory())
            {
                checkPattern(file);
            }
        }
    }

    private void deleteRecursive(File fileOrDirectory)
    {
        if (fileOrDirectory.isDirectory())
        {
            for (File child : fileOrDirectory.listFiles())
            {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    private void removeFoundedFiles()
    {
        for (String file_path : founded_file_paths)
        {
            File file = new File(file_path);
            file.delete();
        }
    }

    private void checkPattern(File file)
    {
        if (file.getName().contains(pattern))
        {
            founded_file_paths.add(file.getAbsolutePath());
        }
    }

    private boolean isMinFreeReached()
    {
          return (MyDiscSpace.getFreePercent() < min_free_percent);
    }
}
