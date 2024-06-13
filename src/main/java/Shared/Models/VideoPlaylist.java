package Shared.Models;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "video_playlist")
@IdClass(VideoPlaylist.VideoPlaylistId.class)
public class VideoPlaylist {

    @Id
    @Column(name = "VideoId", nullable = false)
    private Long videoId;

    @Id
    @Column(name = "PlaylistId", nullable = false)
    private Long playlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VideoId", referencedColumnName = "VideoId", insertable = false, updatable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlaylistId", referencedColumnName = "PlaylistId", insertable = false, updatable = false)
    private Playlist playlist;

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }
    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }
    public Video getVideo() {
        return video;
    }

    public Playlist getPlaylist() {
        return playlist;
    }


    public VideoPlaylist()
    {

    }
    public VideoPlaylist(Long videoId, Long playlistId)
    {
        this.videoId = videoId;
        this.playlistId = playlistId;
    }

    public static class VideoPlaylistId implements Serializable {
        private Long videoId;
        private Long playlistId;

    }
}
