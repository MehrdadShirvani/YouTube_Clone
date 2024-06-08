package Shared.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "CommentReactions")
public class CommentReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CommentReactionId")
    private Long commentReactionId;

    @Column(name = "CommentId", nullable = false)
    private Long commentId;

    @Column(name = "ChannelId", nullable = false)
    private Long channelId;

    @Column(name = "CommentReactionTypeId", nullable = false)
    private Short commentReactionTypeId;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "CommentId", insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "ChannelId", insertable = false, updatable = false)
    private Channel channel;

    public Long getCommentReactionId() {
        return commentReactionId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Short getCommentReactionTypeId() {
        return commentReactionTypeId;
    }

    public void setCommentReactionTypeId(Short commentReactionTypeId) {
        this.commentReactionTypeId = commentReactionTypeId;
    }

    public Comment getComment() {
        return comment;
    }

    public Channel getChannel() {
        return channel;
    }
}