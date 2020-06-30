package com.vakhnenko.dto.playlist;

import com.vakhnenko.dto.EntityResponse;
import com.vakhnenko.entity.Playlist;
import com.vakhnenko.entity.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PlaylistResponse extends EntityResponse {
    private final Long playlistId;
    private final String playlistName;
    //private final UserResponse author;
    //private final Set<Song> songs;

    public PlaylistResponse(Playlist playlist) {
        super(playlist);
        this.playlistId = playlist.getPlaylistId();
        this.playlistName = playlist.getPlaylistName();
        //this.author = new UserResponse(playlist.getAuthor());
        //this.songs = playlist.getSongs();
    }
}
