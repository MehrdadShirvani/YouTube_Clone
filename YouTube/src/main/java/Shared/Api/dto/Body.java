package Shared.Api.dto;


import Shared.Models.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Body {
    private boolean success;
    private String message;

    private String username;
    private String password;
    private Long accountId;
    private Long channelId;
    private Long reactionId;
    private Long commentId;
    private Long commentReactionId;
    private Account account;
    private Channel channel;
    private Category category;
    private Comment comment;
    private CommentReaction commentReaction;
    private Playlist playlist;
    private Reaction reaction;
    private Video video;
    private VideoCategory videoCategory;
    private VideoView videoView;
    private List<Channel> subscriberChannels;



    public Body() {
        //TODO
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public CommentReaction getCommentReaction() {
        return commentReaction;
    }

    public void setCommentReaction(CommentReaction commentReaction) {
        this.commentReaction = commentReaction;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public VideoCategory getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(VideoCategory videoCategory) {
        this.videoCategory = videoCategory;
    }

    public VideoView getVideoView() {
        return videoView;
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public List<Channel> getSubscriberChannels() {
        return subscriberChannels;
    }

    public void setSubscriberChannels(List<Channel> subscriberChannels) {
        this.subscriberChannels = subscriberChannels;
    }

    public Long getReactionId() {
        return reactionId;
    }

    public void setReactionId(Long reactionId) {
        this.reactionId = reactionId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getCommentReactionId() {
        return commentReactionId;
    }

    public void setCommentReactionId(Long commentReactionId) {
        this.commentReactionId = commentReactionId;
    }
}