package cn.coolbhu.snailgo.listeners;

/**
 * Listens for playback changes to send the the fragments bound to this activity
 */
public interface MusicStateListener {

    /**
     * Called when {@link cn.coolbhu.snailgo.services.MusicService#REFRESH} is invoked
     */
    void restartLoader();

    /**
     * Called when {@link cn.coolbhu.snailgo.services.MusicService#PLAYLIST_CHANGED} is invoked
     */
    void onPlaylistChanged();

    /**
     * Called when {@link cn.coolbhu.snailgo.services.MusicService#META_CHANGED} is invoked
     */
    void onMetaChanged();

}
