/*
 garlic-launcher: Android Launcher for the Digital Signage Software garlic-player

 Copyright (C) 2020 Nikolaos Sagiadinos <ns@smil-control.com>
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
package com.sagiadinos.garlic.launcher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.sagiadinos.garlic.launcher.configuration.SharedPreferencesModel;
import com.sagiadinos.garlic.launcher.configuration.PasswordHasher;
import com.sagiadinos.garlic.launcher.configuration.MainConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class SettingsProvider extends ContentProvider
{
    private static final String PROVIDER_NAME = "com.sagiadinos.garlic.launcher.SettingsProvider";

    private static final int SMIL_CONTENT_URL = 1;
    private static final int SERVICE_PASSWORD = 2;
    private static final int LAUNCHER_UUID    = 3;
    private static final UriMatcher uriMatcher = getUriMatcher();

    private MainConfiguration MyMainConfiguration = null;

    @Override
    public boolean onCreate()
    {
        MyMainConfiguration = new MainConfiguration(new SharedPreferencesModel(Objects.requireNonNull(getContext())));
        return true;
    }

    @Override
    public int delete(@NotNull Uri uri, String selection, String[] selectionArgs)
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public String getType(@NotNull Uri uri)
    {
        return "vnd.android.cursor.item/vnd.com.sagiadinos.garlic.launcher.SettingsProvider.provider.string";
    }

    @Override
    public Uri insert(@NotNull Uri uri, ContentValues values)
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public Cursor query(@NotNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        MatrixCursor cursor                = new MatrixCursor(new String[] {uri.getPathSegments().get(0)});
        MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
        switch (uriMatcher.match(uri))
        {
            case SMIL_CONTENT_URL:
                rowBuilder.add(MyMainConfiguration.getSmilIndex());
                break;
            case SERVICE_PASSWORD:
                rowBuilder.add(validatePassword(selection));
                break;
            case LAUNCHER_UUID:
                rowBuilder.add(MyMainConfiguration.getUUID());
                break;
            default:
                throw new IllegalArgumentException("Unsupported uri " + uri);
        }
        return cursor;
    }

    @Override
    public int update(@NotNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        throw new UnsupportedOperationException("Not supported!");
    }

    private static UriMatcher getUriMatcher()
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "smil_content_url", SMIL_CONTENT_URL);
        uriMatcher.addURI(PROVIDER_NAME, "service_password", SERVICE_PASSWORD);
        uriMatcher.addURI(PROVIDER_NAME, "uuid", LAUNCHER_UUID);
        return uriMatcher;
    }

    private String validatePassword(String cleartext)
    {
        if (MyMainConfiguration.compareServicePassword(cleartext, new PasswordHasher()))
        {
            return "valid";
        }
        else
        {
            return "invalid";
        }

    }

}
