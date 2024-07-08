package Shared.Api.dto;


import Shared.Models.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Body {
    private Boolean success;
    private String message;

    private String username;
    private Boolean isUsernameUnique;
    private String password;
    private String emailAddress;
    private Boolean isEmailUnique;
    private Long accountId;
    private Long channelId;
    private Long reactionId;
    private Long commentId;
    private Long commentReactionId;
    private Long subscriberChannelId;
    private Long subscribedChannelId;
    private Long videoId;
    private Long playlistId;
    private Account account;
    private Channel channel;
    private Category category;
    private Comment comment;
    private CommentReaction commentReaction;
    private Playlist playlist;
    private Reaction reaction;
    private Video video;
    private List<Video> homepageVideos;
    private VideoCategory videoCategory;
    private VideoView videoView;
    private List<Channel> subscriberChannels;
    private Subscription subscription;
    private List<Comment> comments;
    private ArrayList<String> searchHistory;
    private List<Channel> subscriptions;
    private List<Category> categories;
    private List<Video> searchVideos;
    private List<Video> watchHistoryVideos;
    private List<Date> watchHistoryDates;
    private List<Video> playlistVideos;
    private List<Channel> playlistChannels;
    private Boolean isSubscribedToChannel;
    private HashMap<Boolean , Short> isVideoLiked;
    private HashMap<Boolean , Short> isCommentLiked;
    private Long numberOfViews;
    private Long numberOfLikes;
    private Long numberOfCommentLikes;
    private Long numberOfDislikes;
    private Long numberOfCommentDislikes;
    private Integer perPage;
    private Integer pageNumber;
    private Boolean isChannelNameUnique;
    private VideoPlaylist videoPlaylist;
    private ChannelPlaylist channelPlaylist;
    private Integer categoryId;
    private String channelName;
    private String searchTerms;
    private List<VideoCategory> videoCategories;
    private List<Integer> categoryIds;
    private List<VideoPlaylist> videoPlaylists;
    private List<Playlist> playlists;
    private List<Long> playlistIds;
    private Boolean isSelf;
    private List<Video> videosOfChannel;
    private Long numberOfVideos;
    private String recipientsEmail;
    private String token;
    private Integer twoFactorDigit;
    private Boolean isVerified;
    private HashMap<String , String> twoFactorData;
    private Integer code;


    public Body() {
        //TODO
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getUsernameUnique() {
        return isUsernameUnique;
    }

    public void setUsernameUnique(Boolean usernameUnique) {
        isUsernameUnique = usernameUnique;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getEmailUnique() {
        return isEmailUnique;
    }

    public void setEmailUnique(Boolean emailUnique) {
        isEmailUnique = emailUnique;
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

    public Long getSubscriberChannelId() {
        return subscriberChannelId;
    }

    public void setSubscriberChannelId(Long subscriberChannelId) {
        this.subscriberChannelId = subscriberChannelId;
    }

    public Long getSubscribedChannelId() {
        return subscribedChannelId;
    }

    public void setSubscribedChannelId(Long subscribedChannelId) {
        this.subscribedChannelId = subscribedChannelId;
    }

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

    public List<Video> getHomepageVideos() {
        return homepageVideos;
    }

    public void setHomepageVideos(List<Video> homepageVideos) {
        this.homepageVideos = homepageVideos;
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

    public List<Channel> getSubscriberChannels() {
        return subscriberChannels;
    }

    public void setSubscriberChannels(List<Channel> subscriberChannels) {
        this.subscriberChannels = subscriberChannels;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(ArrayList<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public List<Channel> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Channel> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Video> getSearchVideos() {
        return searchVideos;
    }

    public void setSearchVideos(List<Video> searchVideos) {
        this.searchVideos = searchVideos;
    }

    public List<Video> getWatchHistoryVideos() {
        return watchHistoryVideos;
    }

    public void setWatchHistoryVideos(List<Video> watchHistoryVideos) {
        this.watchHistoryVideos = watchHistoryVideos;
    }

    public List<Date> getWatchHistoryDates() {
        return watchHistoryDates;
    }

    public void setWatchHistoryDates(List<Date> watchHistoryDates) {
        this.watchHistoryDates = watchHistoryDates;
    }

    public List<Video> getPlaylistVideos() {
        return playlistVideos;
    }

    public void setPlaylistVideos(List<Video> playlistVideos) {
        this.playlistVideos = playlistVideos;
    }

    public List<Channel> getPlaylistChannels() {
        return playlistChannels;
    }

    public void setPlaylistChannels(List<Channel> playlistChannels) {
        this.playlistChannels = playlistChannels;
    }

    public Boolean getSubscribedToChannel() {
        return isSubscribedToChannel;
    }

    public void setSubscribedToChannel(Boolean subscribedToChannel) {
        isSubscribedToChannel = subscribedToChannel;
    }

    public HashMap<Boolean, Short> getIsVideoLiked() {
        return isVideoLiked;
    }

    public void setIsVideoLiked(HashMap<Boolean, Short> isVideoLiked) {
        this.isVideoLiked = isVideoLiked;
    }

    public HashMap<Boolean, Short> getIsCommentLiked() {
        return isCommentLiked;
    }

    public void setIsCommentLiked(HashMap<Boolean, Short> isCommentLiked) {
        this.isCommentLiked = isCommentLiked;
    }

    public Long getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(Long numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public Long getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(Long numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public Long getNumberOfCommentLikes() {
        return numberOfCommentLikes;
    }

    public void setNumberOfCommentLikes(Long numberOfCommentLikes) {
        this.numberOfCommentLikes = numberOfCommentLikes;
    }

    public Long getNumberOfDislikes() {
        return numberOfDislikes;
    }

    public void setNumberOfDislikes(Long numberOfDislikes) {
        this.numberOfDislikes = numberOfDislikes;
    }

    public Long getNumberOfCommentDislikes() {
        return numberOfCommentDislikes;
    }

    public void setNumberOfCommentDislikes(Long numberOfCommentDislikes) {
        this.numberOfCommentDislikes = numberOfCommentDislikes;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Boolean getChannelNameUnique() {
        return isChannelNameUnique;
    }

    public void setChannelNameUnique(Boolean channelNameUnique) {
        isChannelNameUnique = channelNameUnique;
    }

    public VideoPlaylist getVideoPlaylist() {
        return videoPlaylist;
    }

    public void setVideoPlaylist(VideoPlaylist videoPlaylist) {
        this.videoPlaylist = videoPlaylist;
    }

    public ChannelPlaylist getChannelPlaylist() {
        return channelPlaylist;
    }

    public void setChannelPlaylist(ChannelPlaylist channelPlaylist) {
        this.channelPlaylist = channelPlaylist;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public List<VideoCategory> getVideoCategories() {
        return videoCategories;
    }

    public void setVideoCategories(List<VideoCategory> videoCategories) {
        this.videoCategories = videoCategories;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<VideoPlaylist> getVideoPlaylists() {
        return videoPlaylists;
    }

    public void setVideoPlaylists(List<VideoPlaylist> videoPlaylists) {
        this.videoPlaylists = videoPlaylists;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public List<Long> getPlaylistIds() {
        return playlistIds;
    }

    public void setPlaylistIds(List<Long> playlistIds) {
        this.playlistIds = playlistIds;
    }

    public Boolean getSelf() {
        return isSelf;
    }

    public void setSelf(Boolean self) {
        isSelf = self;
    }

    public List<Video> getVideosOfChannel() {
        return videosOfChannel;
    }

    public void setVideosOfChannel(List<Video> videosOfChannel) {
        this.videosOfChannel = videosOfChannel;
    }

    public Long getNumberOfVideos() {
        return numberOfVideos;
    }

    public void setNumberOfVideos(Long numberOfVideos) {
        this.numberOfVideos = numberOfVideos;
    }

    public String getRecipientsEmail() {
        return recipientsEmail;
    }

    public void setRecipientsEmail(String recipientsEmail) {
        this.recipientsEmail = recipientsEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getTwoFactorDigit() {
        return twoFactorDigit;
    }

    public void setTwoFactorDigit(Integer twoFactorDigit) {
        this.twoFactorDigit = twoFactorDigit;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public HashMap<String, String> getTwoFactorData() {
        return twoFactorData;
    }

    public void setTwoFactorData(HashMap<String, String> twoFactorData) {
        this.twoFactorData = twoFactorData;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
