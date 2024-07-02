# DatabaseManager Class

## Short Descriptive Summary
The `DatabaseManager` class provides methods for making connection to database to perform CRUD operation

## Why?


## Drawbacks


## Architecture

### Methods
- `Channel addChannel(Channel channel)()`: 
- `Channel editChannel(Channel updatedChannel)`:
- `Channel getChannel(Long channelId)`: finds and returns channel using channelId. returns null if channel doesn't exist
- `List<Channel> getChannels()`: returns a list of all channels
- `List<Channel> getSubscribedChannels(Long channelId)`: returns channels that are subscribed to the channel 
- `List<Channel> getSubscriberChannels(Long channelId)`: returns channels that are the channel is subscribed to
- `boolean isChannelNameUnique(String name)`: returns the existence of channel name in database
- 
- `Subscription addSubscription(Long subscriberChannelId ,Long subscribedChannelId)`: creates subscription. If the subscriberChannelId or subscribedChannelId is not valid, it will return null.
- `void deleteSubscription(Long subscriberChannelId ,Long subscribedChannelId)`: deletes subscription. If the subscriberChannelId or subscribedChannelId is not valid, nothing will happen.

- `Reaction addReaction(Reaction reaction)`
- `Reaction editReaction(Reaction reaction)`
- `Reaction getReaction(Long channelId, Long videoId)`: finds the reaction that the channel has shown to a video if it exists; if not, null will be returned 
- `void deleteReaction(Long reactionId)`: deletes reaction if found. Does nothing if the channelId does not exist  

- `Comment getComment(Long commentId)`: finds and returns comment using commentId. returns null if it doesn't exist
- `Comment addComment(Comment comment)`
- `Comment editComment(Comment comment) `
- `void deleteComment(Long commentId)`: Note: all the related comment reactions and replied comments will be removed as well
- `List<Comment> getCommentsRepliedToComment(Long commentId)`: returns all comments that were created as a reply to the comment. returns null if commentId is not valid 

- `CommentReaction addCommentReaction(CommentReaction commentReaction)`
- `CommentReaction editCommentReaction(CommentReaction commentReaction)`
- `void deleteCommentReaction(Long commentReactionId) `
- `List<CommentReaction> getCommentReactionsOfComment(Long commentId)`
- `CommentReaction getCommentReaction(Long channelId, Long commentId)`: finds the comment reaction that the channel has shown to a video if it exists; if not, null will be returned

- `Playlist addPlaylist(Playlist playlist)`
- `Playlist editPlaylist(Playlist playlist)`
- `Playlist getPlaylist(Long playlistId)`: finds and returns playlist using playlistId. returns null if playlist doesn't exist
- `Playlist editPlaylist(Playlist playlist)`
- `List<Vidoe> getVideosOfPlaylist(Long playlistId)`: returns list of videos that are in the playlist
- `List<VidoePlaylist> getVideoPlaylists(Long videoId)`: returns VideoPlaylists related to the video. returns null if video does not exist
- `List<Channel> getChannelsOfPlaylist(Long playlistId)`: returns list of channels that contain the playlist
- `VideoPlaylist addVideoPlaylist(Long videoId, Long playlistId)`: creates VideoPlaylist. If videoId or playlistId is not valid, it will return null. If it already exists, nothing will happen.
- `void deleteVideoPlaylist(Long videoId, Long playlistId)` 
- `ChannelPlaylist addChannelPlaylist(Long channelId, Long playlistId)`: creates ChannelPlaylist. If channelId or playlistId is not valid, it will return null. If it already exists, nothing will happen.
- `void deleteChannelPlaylist(Long channelId, Long playlistId)`


- `Account getAccount(Long accountId)`
- `Account getAccount(String username, String password)`: inputs username and password, returns user if exists, returns null if it does not exist 
- `Account addAccount(Account account)`
- `Account editAccount(Account account)`
- `boolean isUsernameUnique(String name)`: returns the existence of account username in database
- `boolean isEmailUnique(String name)`: returns the existence of email in database

- `Video addVideo(Video video)`
- `Video editVideo(Video video)`
- `Video getVideo(Long videoId)`
- `void deleteVideo(Long videoId)`: NOTE: All the related VideoViews, Reactions, Comments, VideoCategories, and VideoPlaylists will also be deleted

- `List<Category> getCategoriesOfVideo(Long videoId)`: returns the categories that a video has
- `List<VideoCategory> getVideoCategories(Long videoId)`: returns the VideoCategories related to a video
- `VideoCategory addVideoCategory(Long videoId, int categoryId)`: assigns a category to a video. returns null if videoId or categoryId is not valid. Does nothing if this has already been done.
- `void deleteVideoCategory(Long videoId, Integer categoryId)` 

- `List<VideoView> getVideoViewsOfVideo(Long videoId)`: returns all VideoViews of the video
- `List<Reaction> getVideoReactions(Long videoId)`: returns reactions of the video
- `List<Comment> getVideoComments(Long videoId)`: returns comments of the video
- `VideoView addVideoView(VideoView videoView)`
- `void deleteVideoView(Long videoViewId)`
- `List<Video> getWatchHistory(Long channelId)`: returns the 100 most recent videos that the user watched
- `List<Category> getCategories()`
- `Category getCategory(Integer categoryId)`

- `boolean isSubscribedToChannel(long subscriberChannelId, long targetChannelId)`
- `long getNumberOfViews(long videoId)`
- `List<Category> getMostViewedCategoriesOfUsers(long channelId)`

### Security Risks