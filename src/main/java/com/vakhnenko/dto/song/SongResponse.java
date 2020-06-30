package com.vakhnenko.dto.song;

import com.vakhnenko.dto.EntityResponse;
import com.vakhnenko.dto.album.AlbumResponse;
import com.vakhnenko.entity.Song;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SongResponse extends EntityResponse {
    private final Long songId;
    private final String songName;
    private final Double songLength;
    private final AlbumResponse album;
    //private final Set<Playlist> playlists;

    public SongResponse(Song song) {
        super(song);
        this.songId = song.getSongId();
        this.songName = song.getSongName();
        this.songLength = song.getSongLength();
        this.album = new AlbumResponse(song.getAlbum());
        //this.playlists = song.getPlaylists();
    }
}
