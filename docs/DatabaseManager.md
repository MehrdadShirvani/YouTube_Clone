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

- `Reaction addReaction(Reaction reaction)`
- `Reaction editReaction(Reaction reaction)`
- `Reaction getReaction(Long channelId, Long videoId)`: finds the reaction that the channel has shown to a video if it exists; if not, null will be returned 
- `void deleteReaction(Long reactionId)`: deletes reaction if found. Does nothing if the channelId does not exist  

- `Comment getComment(Long commentId)`: finds and returns comment using commentId. returns null if it doesn't exist
- `Comment addComment(Comment comment)`
- `Comment editComment(Comment comment) `
- `void deleteComment(Long commentId)`

- `CommentReaction addCommentReaction(CommentReaction commentReaction)`
- `CommentReaction editCommentReaction(CommentReaction commentReaction)`
- `void deleteCommentReaction(Long commentReactionId) `
- `List<CommentReaction> getCommentReactionsOfComment(Long commentId)`
- `CommentReaction getCommentReaction(Long channelId, Long commentId)`: finds the comment reaction that the channel has shown to a video if it exists; if not, null will be returned

- `Playlist addPlaylist(Playlist playlist)`
- `Playlist editPlaylist(Playlist playlist)`
- `List<Vidoe> getPlaylistVideos(Long playlistId)`: returns list of videos that are in the playlist
- `List<Channel> getPlaylistChannels(Long playlistId)`: returns list of channels that contain the playlist

- `Account getAccount(Long accountId)`
- `Account getAccount(String username, String password)`: inputs username and password, returns user if exists, returns null if it does not exist 
- `Account addAccount(Account account)`
- `Account editAccount(Account account)`

- `Video addVideo(Video video)`
- `Video editVideo(Video video)`
- `void deleteVideo(Long videoId)`

- `List<VideoCategory> getVideoCategories(Long videoId)`: returns the assigned categories related to a video 
- `VideoCategory addVideoCategory(Long videoId, int categoryId)`: assigns a category to a video. Does nothing if this has already been done 

- `List<VideoView> getVideoViewsOfVideo(Long videoId)`: returns all VideoViews of the video
- `List<Reaction> getVideoReactions(Long videoId)`: returns reactions of the video
- `List<Comment> getVideoComments(Long videoId)`: returns comments of the video
- `VideoView addVideoView(VideoView videoView)`
- `List<Video> getWatchHistory(Long channelId)`: returns the 100 recent videos that the user watched
- `List<Category> getCategories()`

### Security Risks