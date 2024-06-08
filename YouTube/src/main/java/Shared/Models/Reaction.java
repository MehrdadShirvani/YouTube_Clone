package Shared.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "Reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReactionId")
    private Long reactionId;

    @Column(name = "VideoId", nullable = false)
    private Long videoId;

    @Column(name = "ChannelId", nullable = false)
    private Long channelId;

    @Column(name = "ReactionTypeId", nullable = false)
    private Short reactionTypeId;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "VideoId", insertable = false, updatable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "ChannelId", insertable = false, updatable = false)
    private Channel channel;

    public Long getReactionId() {
        return reactionId;
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

    public Short getReactionTypeId() {
        return reactionTypeId;
    }

    public void setReactionTypeId(Short reactionTypeId) {
        this.reactionTypeId = reactionTypeId;
    }

    public Video getVideo() {
        return video;
    }

    public Channel getChannel() {
        return channel;
    }
}