package com.sagiadinos.garlic.launcher.helper;

import java.io.File;
import java.util.LinkedList;

public class CleanUp
{
    LinkedList<String> founded_file_paths;
    String             pattern           = null;

    public CleanUp()
    {
        founded_file_paths = new LinkedList<>();
    }

    public void removeAll(String external_directory)
    {
        removePlayerApks(external_directory);
        removeXMLFiles(external_directory);
    }

    public void removePlayerApks(String external_directory)
    {
        pattern          = "garlic-player.apk";
        removeFiles(external_directory);
    }

    public void removeXMLFiles(String external_directory)
    {
        pattern          = ".xml";
        removeFiles(external_directory);
    }

    private void removeFiles(String external_directory)
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

}
