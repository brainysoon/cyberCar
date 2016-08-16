package cn.coolbhu.snailgo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.beans.User;

/**
 * Created by ken on 16-7-29.
 */
public class PreferencesUtils {

    private static final String SETTINGS_KEY_REGULATION_LOCATION = "settings_key_regulation_location";
    public static final String SONG_SORT_ORDER = "song_sort_order";
    public static final String ALBUM_SORT_ORDER = "album_sort_order";
    public static final String ARTIST_SORT_ORDER = "artist_sort_order";
    private static final String TOGGLE_HEADPHONE_PAUSE = "toggle_headphone_pause";
    private static final String TOGGLE_XPOSED_TRACKSELECTOR = "toggle_xposed_trackselector";
    private static final String TOGGLE_ANIMATIONS = "toggle_animations";
    private static final String TOGGLE_SYSTEM_ANIMATIONS = "toggle_system_animations";
    private static final String THEME_PREFERNCE = "theme_preference";
    private static final String NOW_PLAYNG_THEME_VALUE = "now_playing_theme_value";
    public static final String ALBUM_SONG_SORT_ORDER = "album_song_sort_order";
    private static final String TOGGLE_ARTIST_GRID = "toggle_artist_grid";
    public static final String ARTIST_SONG_SORT_ORDER = "artist_song_sort_order";

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

    //Instance
    private static PreferencesUtils mInstance;

    //SharedPreferences
    private static SharedPreferences mPreferences;

    //Single
    private PreferencesUtils(final Context context) {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //getInstance
    public static PreferencesUtils getInstance(final Context context) {

        if (mInstance == null) {

            mInstance = new PreferencesUtils(context);
        }

        return mInstance;
    }

    //自定义查询违章信息是否需要定位
    public boolean isSettingsRegulationLocation() {

        return mPreferences.getBoolean(SETTINGS_KEY_REGULATION_LOCATION, true);
    }

    //歌曲的排序
    public final String getSongSortOrder() {
        return mPreferences.getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z);
    }

    public void setSongSortOrder(final String value) {
        setSortOrder(SONG_SORT_ORDER, value);
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

    public boolean getXPosedTrackselectorEnabled() {
        return mPreferences.getBoolean(TOGGLE_XPOSED_TRACKSELECTOR, false);
    }

    public boolean pauseEnabledOnDetach() {
        return mPreferences.getBoolean(TOGGLE_HEADPHONE_PAUSE, true);
    }

    //Album
    public final String getAlbumSortOrder() {
        return mPreferences.getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z);
    }

    public void setAlbumSortOrder(final String value) {
        setSortOrder(ALBUM_SORT_ORDER, value);
    }

    //Artist
    public final String getArtistSortOrder() {
        return mPreferences.getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z);
    }

    public void setArtistSortOrder(final String value) {
        setSortOrder(ARTIST_SORT_ORDER, value);
    }

    public boolean getAnimations() {
        return mPreferences.getBoolean(TOGGLE_ANIMATIONS, true);
    }

    public boolean getSystemAnimations() {
        return mPreferences.getBoolean(TOGGLE_SYSTEM_ANIMATIONS, true);
    }

    public String getTheme() {
        return mPreferences.getString(THEME_PREFERNCE, "light");
    }

    public final boolean didNowplayingThemeChanged() {
        return mPreferences.getBoolean(NOW_PLAYNG_THEME_VALUE, false);
    }

    //设置跳过当前最新的版本号
    public void setJumpVersionCode(int code) {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putInt(JUMP_VERSION_CODE, code);

        editor.apply();
    }

    public void setNowPlayingThemeChanged(final boolean value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(NOW_PLAYNG_THEME_VALUE, value);
        editor.apply();
    }

    public void setAlbumSongSortOrder(final String value) {
        setSortOrder(ALBUM_SONG_SORT_ORDER, value);
    }

    public final String getAlbumSongSortOrder() {
        return mPreferences.getString(ALBUM_SONG_SORT_ORDER,
                SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST);
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

    public final String getArtistSongSortOrder() {
        return mPreferences.getString(ARTIST_SONG_SORT_ORDER,
                SortOrder.ArtistSongSortOrder.SONG_A_Z);
    }

    public void setArtistSongSortOrder(final String value) {
        setSortOrder(ARTIST_SONG_SORT_ORDER, value);
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

    //是否保存密码
    public boolean isSavePassword(String User_Tel) {

        return mPreferences.getBoolean(User_Tel + "isSavePassowrd", false);
    }

    //是否需要自动登陆
    public boolean isAutoLogin(String User_Tel) {

        return mPreferences.getBoolean(User_Tel + "isAutoLogin", false);
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

    //保存
    public void saveIsSavePassAndAutoLogin(String tel, boolean isSavePass, boolean isAutoLogin) {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putBoolean(tel + "isSavePassowrd", isSavePass);

        editor.putBoolean(tel + "isAutoLogin", isAutoLogin);

        editor.apply();
    }

    public boolean setIsFirstLoad() {

        return mPreferences.getBoolean(IS_FIRST_LOAD, true);
    }

    //获得跳过的版本号
    public int getJumpVersionCode() {

        //如果没有设置跳过的版本号的话，就返回一个比较小的版本号，方便比较
        return mPreferences.getInt(JUMP_VERSION_CODE, -1);
    }

    public void setNotFirstLoad() {

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putBoolean(IS_FIRST_LOAD, false);

        editor.apply();
    }

    //退出App是否继续播放音乐
    public boolean isSettingsMusicContinue() {

        return mPreferences.getBoolean(SETTINGS_KEY_MUSIC_CONTINUE, false);
    }

    //进入App是否需要自动播放音乐
    public boolean isSettingsMusicAuto() {

        return mPreferences.getBoolean(SETTINGS_KEY_MUSIC_AUTO, false);
    }
}
