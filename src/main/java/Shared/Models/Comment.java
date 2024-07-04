package Shared.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentId")
    private Long commentId;

    @Column(name = "Text", nullable = false, length = 500)
    private String text;

    @Column(name = "VideoId", nullable = false)
    private Long videoId;

    @Column(name = "ChannelId", nullable = false)
    private Long channelId;

    @Column(name = "CreatedDateTime", nullable = false)
    private Timestamp createdDateTime;

    @Column(name = "RepliedCommentId")
    private Long repliedCommentId;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "ChannelId", insertable = false, updatable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "VideoId", insertable = false, updatable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "RepliedCommentId", referencedColumnName = "CommentId", insertable = false, updatable = false)
    private Comment repliedComment;

    public Long getCommentId() {
        return commentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Long getRepliedCommentId() {
        return repliedCommentId;
    }

    public void setRepliedCommentId(Long repliedCommentId) {
        this.repliedCommentId = repliedCommentId;
    }

    public Channel getChannel() {
        return channel;
    }

    public Video getVideo() {
        return video;
    }

    public Comment getRepliedComment() {
        return repliedComment;
    }

    public Comment()
    {

    }
    public Comment(String text, Long videoId,  Long channelId, Long repliedCommentId)
    {
        this.text = text;
        this.videoId = videoId;
        this.channelId = channelId;
        this.createdDateTime = new Timestamp(System.currentTimeMillis());
        this.repliedCommentId = repliedCommentId;
    }
}