package com.sagiadinos.garlic.launcher.helper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CleanUpTest
{
    File work_dir;
    final static String CLEANUP_TEST_PATH = "sampledata/testCleanUp";
    final static String CLEANUP_TEST_CACHE_PATH = "sampledata/testCleanUp/garlic-player/cache";

    @BeforeAll
    static void beforeAll()
    {
        File cleanup_test_dir = new File(CLEANUP_TEST_PATH);
        if (!cleanup_test_dir.exists())
        {
            cleanup_test_dir.mkdir();
        }
    }

    @AfterAll
    static void afterAll()
    {
        File cleanup_test_dir = new File(CLEANUP_TEST_PATH);
        if (cleanup_test_dir.exists())
        {
            deleteFilesRecursively(cleanup_test_dir);
            cleanup_test_dir.delete();
        }
    }

    @BeforeEach
    void setUp()
    {
        work_dir = new File(CLEANUP_TEST_CACHE_PATH);
        if (!work_dir.exists())
        {
            work_dir.mkdirs();
        }
    }

    @AfterEach
    void tearDown()
    {
       if (work_dir.exists())
        {
            deleteFilesRecursively(work_dir);
            work_dir.delete();
        }
   }

    @Test
    void removePlayerApks() throws IOException
    {
        File apk1 = createFile("garlic-player.apk");
        File apk2 = createFile("garlic-player.apk.ready");
        File apk3 = createFile("uiuiui.garlic-player.apk");
        File apk4 = createFile("uiuiui.garlic-player.apk.tztz");
        File apk5 = createFile("marlic-player.apk");
        File apk6 = createFile("garlic-player.");
        File dir1 = createDirectory("garlic-player.apk.uiuiui");
        File dir2 = createDirectory("dir dir dir");

        CleanUp MyTestClass = new CleanUp();

        MyTestClass.removePlayerApks(CLEANUP_TEST_PATH);

        assertFalse(apk1.exists());
        assertFalse(apk2.exists());
        assertFalse(apk3.exists());
        assertFalse(apk4.exists());
        assertTrue(apk5.exists());
        assertTrue(apk6.exists());
        assertTrue(dir1.exists());
        assertTrue(dir2.exists());
   }


    @Test
    void removeXMLFile() throws IOException
    {
        File xml1 = createFile("garlic-player.xml");
        File xml2 = createFile("configuration.xml");
        File xml3 = createFile("task_schedule.xml");
        File xml4 = createFile("uiuiui.garlic-player.xml.tztz");
        File xml5 = createFile("garlic-player.apk");
        File xml6 = createFile("uiuixml.test");
        File dir1 = createDirectory("config_dir.xml");
        File dir2 = createDirectory("adirdirdir");

        CleanUp MyTestClass = new CleanUp();

        MyTestClass.removeXMLFiles(CLEANUP_TEST_PATH);

        assertFalse(xml1.exists());
        assertFalse(xml2.exists());
        assertFalse(xml3.exists());
        assertFalse(xml4.exists());
        assertTrue(xml5.exists());
        assertTrue(xml6.exists());
        assertTrue(dir1.exists());
        assertTrue(dir2.exists());
    }

    @Test
    void removeFromNotExistingDirectory() throws IOException
    {
        File xml1 = createFile("garlic-player.xml");
        CleanUp MyTestClass = new CleanUp();
        MyTestClass.removeXMLFiles("this directory not exists");
        assertTrue(xml1.exists());
    }


    File createFile(String file_name) throws IOException
    {
        String s = work_dir.getAbsoluteFile() + "/" + file_name;
        File file = new File(s);
        assertTrue(file.createNewFile());
        return file;
    }

    File createDirectory(String dir_name) throws IOException
    {
        File dir = new File(work_dir.getAbsoluteFile() + "/" + dir_name);
        assertTrue(dir.mkdirs());
        return dir;
    }

    static void deleteFilesRecursively(File dir)
    {
        File[] FilesList = dir.listFiles();

        if (FilesList == null)
            return;

        for (File file : FilesList)
        {
            if (file.isDirectory())
            {
                deleteFilesRecursively(file);
            }
            file.delete();
        }
    }
}