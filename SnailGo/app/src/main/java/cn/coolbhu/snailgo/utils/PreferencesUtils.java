package cn.coolbhu.snailgo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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
}
