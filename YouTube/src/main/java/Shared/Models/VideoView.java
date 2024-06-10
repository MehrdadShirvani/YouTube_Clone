package Shared.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "VideoView")
public class VideoView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VideoViewId")
    private Long videoViewId;

    @Column(name = "VideoId", nullable = false)
    private Long videoId;

    @Column(name = "ChannelId", nullable = false)
    private Long channelId;

    @Column(name = "ViewDateTime", nullable = false)
    private Timestamp viewDateTime;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "VideoId", insertable = false, updatable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "ChannelId", insertable = false, updatable = false)
    private Channel channel;


    public Long getVideoViewId() {
        return videoViewId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Timestamp getViewDateTime() {
        return viewDateTime;
    }

    public void setViewDateTime(Timestamp viewDateTime) {
        this.viewDateTime = viewDateTime;
    }

    public Video getVideo() {
        return video;
    }

    public Channel getChannel() {
        return channel;
    }

    public VideoView()
    {

    }

    public VideoView(Long videoId, Long channelId, Timestamp viewDateTime)
    {
        this.videoId = videoId;
        this.channelId = channelId;
        this.viewDateTime = viewDateTime;
    }
}