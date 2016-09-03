/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.fat246.cybercar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fat246.cybercar.application.MyApplication;
import com.fat246.cybercar.beans.User;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;

public final class PreferencesUtility {

    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";
    public static final String ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    private static final String NOW_PLAYING_SELECTOR = "now_paying_selector";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String TOGGLE_ARTIST_GRID = "toggle_artist_grid";
    private static final String TOGGLE_ALBUM_GRID = "toggle_album_grid";
    private static final String TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause";
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String START_PAGE_INDEX = "start_page_index";
    private static final String START_PAGE_PREFERENCE_LASTOPENED = "start_page_preference_latopened";
    private static final String NOW_PLAYNG_THEME_VALUE = "now_playing_theme_value";
    private static final String TOGGLE_XPOSED_TRACKSELECTOR = "toggle_xposed_trackselector";


    private static final String SETTINGS_KEY_USER_STRAIGHT = "settings_key_user_straight";
    private static final String SETTINGS_KEY_REGULATION_LOCATION = "settings_key_regulation_location";
    private static final String SETTINGS_KEY_MUSIC_AUTO = "settings_key_music_auto";
    private static final String SETTINGS_KEY_MUSIC_CONTINUE = "settings_key_music_continue";
    private static final String SETTINGS_KEY_CAR_PUSH = "settings_key_car_push";

    /**
     * 有关用户信息的缓存
     */
    public static final String USER_TEL = "User_Tel";
    public static final String USER_PASSWORD = "User_Password";
    public static final String USER_NICKNAME = "User_NickName";
    public static final String USER_SEX = "User_Sex";
    public static final String USER_BIRTHDAY = "User_Birthday";
    public static final String USER_AVATOR_PATH = "User_Avator";

    //是否是第一次登录
    public static final String IS_FIRST_LOAD = "is_first_load";

    //跳过的版本号
    public static final String JUMP_VERSION_CODE = "jump_version_code";

    private static PreferencesUtility sInstance;

    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean setIsFirstLoad() {

        return mPreferences.getBoolean(IS_FIRST_LOAD, true);
    }

    public void setNotFirstLoad() {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putBoolean(IS_FIRST_LOAD, false);

        editor.apply();
    }

    //获得跳过的版本号
    public int getJumpVersionCode() {

        //如果没有设置跳过的版本号的话，就返回一个比较小的版本号，方便比较
        return mPreferences.getInt(JUMP_VERSION_CODE, -1);
    }

    //设置跳过当前最新的版本号
    public void setJumpVersionCode(int code) {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putInt(JUMP_VERSION_CODE, code);

        editor.apply();
    }

    //保存
    public void saveIsSavePassAndAutoLogin(String tel, boolean isSavePass, boolean isAutoLogin) {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putBoolean(tel + "isSavePassowrd", isSavePass);

        editor.putBoolean(tel + "isAutoLogin", isAutoLogin);

        editor.apply();
    }

    //进入App是否需要自动播放音乐
    public boolean isSettingsMusicAuto() {

        return mPreferences.getBoolean(SETTINGS_KEY_MUSIC_AUTO, false);
    }

    //是否推送消息
    public boolean isSettingsCarPush() {

        return mPreferences.getBoolean(SETTINGS_KEY_CAR_PUSH, true);
    }

    //退出App是否继续播放音乐
    public boolean isSettingsMusicContinue() {

        return mPreferences.getBoolean(SETTINGS_KEY_MUSIC_CONTINUE, false);
    }

    //自定义查询违章信息是否需要定位
    public boolean isSettingsRegulationLocation() {

        return mPreferences.getBoolean(SETTINGS_KEY_REGULATION_LOCATION, true);
    }

    //启动过后去哪个界面
    public boolean isSettingsUserStraight() throws Exception {

        Log.e("getPre", mPreferences.getBoolean(SETTINGS_KEY_USER_STRAIGHT, false) + "");

        return mPreferences.getBoolean(SETTINGS_KEY_USER_STRAIGHT, true);
    }

    //是否保存密码
    public boolean isSavePassword(String User_Tel) {

        return mPreferences.getBoolean(User_Tel + "isSavePassowrd", false);
    }

    //是否需要自动登陆
    public boolean isAutoLogin(String User_Tel) {

        return mPreferences.getBoolean(User_Tel + "isAutoLogin", false);
    }

    //从配置文件里面的到用户信息
    public User getUserInfo() {


        String User_Tel = mPreferences.getString(USER_TEL, "");
        String User_Password = mPreferences.getString(USER_PASSWORD, "");
        String User_NickName = mPreferences.getString(USER_NICKNAME, "");
        Boolean User_Sex = mPreferences.getBoolean(USER_SEX, true);
        String User_Birthday = mPreferences.getString(USER_BIRTHDAY, "");
        String User_Avator_Path = mPreferences.getString(USER_AVATOR_PATH, "");

        File Avator_File = new File(User_Avator_Path);

        BmobFile User_Avator;
        if (!Avator_File.exists()) {

            User_Avator = BmobFile.createEmptyFile();
        } else {

            User_Avator = new BmobFile(Avator_File);
        }


        return new User(User_Tel, User_Password, User_NickName,
                User_Sex, User_Birthday, User_Avator);
    }

    //将用户信息保存到配置里面去
    public void saveUserInfo(User mUser) {

        SharedPreferences.Editor mEditor = mPreferences.edit();

        mEditor.putString(USER_TEL, mUser.getUser_Tel());
        mEditor.putString(USER_PASSWORD, mUser.getUser_Password());
        mEditor.putString(USER_NICKNAME, mUser.getUser_NickName());
        mEditor.putBoolean(USER_SEX, mUser.getUser_Sex());
        mEditor.putString(USER_BIRTHDAY, mUser.getUser_Birthday());
        mEditor.putString(USER_AVATOR_PATH, MyApplication.USER_AVATOR_DIRCTORY +
                mUser.getUser_Avator().getFilename());

        mEditor.apply();

    }

    public void setOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        mPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public boolean getAnimations() {
        return mPreferences.getBoolean(TOGGLE_ANIMATIONS, true);
    }

    public boolean getSystemAnimations() {
        return mPreferences.getBoolean(TOGGLE_SYSTEM_ANIMATIONS, true);
    }

    public boolean isArtistsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ARTIST_GRID, true);
    }

    public void setArtistsInGrid(final boolean b) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(TOGGLE_ARTIST_GRID, b);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(TOGGLE_ALBUM_GRID, b);
                editor.apply();
                return null;
            }
        }.execute();

    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true);
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }

    public int getStartPageIndex() {
        return mPreferences.getInt(START_PAGE_INDEX, 0);
    }

    public void setStartPageIndex(final int index) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(START_PAGE_INDEX, index);
                editor.apply();
                return null;
            }
        }.execute();
    }

    public void setLastOpenedAsStartPagePreference(boolean preference) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(START_PAGE_PREFERENCE_LASTOPENED, preference);
        editor.apply();
    }

    public boolean lastOpenedIsStartPagePreference() {
        return mPreferences.getBoolean(START_PAGE_PREFERENCE_LASTOPENED, true);
    }

    private void setSortOrder(final String key, final String value) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                final SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(key, value);
                editor.apply();

                return null;
            }
        }.execute();
    }

    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public void setArtistSongSortOrder(final String value) {
        setSortOrder(ARTIST_SONG_SORT_ORDER, value);
    }

    public final String getArtistAlbumSortOrder() {
        return mPreferences.getString(ARTIST_ALBUM_SORT_ORDER,
                SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z);
    }

    public void setArtistAlbumSortOrder(final String value) {
        setSortOrder(ARTIST_ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
    }

    public final boolean didNowplayingThemeChanged() {
        return mPreferences.getBoolean(NOW_PLAYNG_THEME_VALUE, false);
    }

    public void setNowPlayingThemeChanged(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(NOW_PLAYNG_THEME_VALUE, value);
        editor.apply();
    }

    public boolean getXPosedTrackselectorEnabled() {
        return mPreferences.getBoolean(TOGGLE_XPOSED_TRACKSELECTOR, false);
    }
}