package cn.coolbhu.snailgo.lastfmapi.callbacks;

import cn.coolbhu.snailgo.lastfmapi.models.LastfmAlbum;

public interface AlbuminfoListener {

    void albumInfoSucess(LastfmAlbum album);

    void albumInfoFailed();

}
