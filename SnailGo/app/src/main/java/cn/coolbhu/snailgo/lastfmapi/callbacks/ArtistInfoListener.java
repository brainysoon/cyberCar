package cn.coolbhu.snailgo.lastfmapi.callbacks;

import cn.coolbhu.snailgo.lastfmapi.models.LastfmArtist;

public interface ArtistInfoListener {

    void artistInfoSucess(LastfmArtist artist);

    void artistInfoFailed();

}
