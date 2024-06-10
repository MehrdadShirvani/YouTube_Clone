# DatabaseManager Class

## Short Descriptive Summary
The `DatabaseManager` class provides methods for making connection to database to perform CRUD operation

## Why?


## Drawbacks


## Architecture

### Methods
- `addChannel(Channel channel)()`:
- `editChannel(Channel updatedChannel)`:
- `getChannel(Long channelId)`: finds channel by id
- `getChannels()`:
- `getSubscribedChannels(Long channelId)`: returns channels that are subscribed to the channel 
- `getSubscriberChannels(Long channelId)`: returns channels that are the channel is subscribed to

- `addReaction(Reaction reaction)`
- `editReaction(Reaction reaction)`
- `getReaction(Long channelId, Long videoId)`: finds the reaction that the channel has shown to a video if it exists; if not, null will be returned 
- `deleteReaction(Long reactionId)`

- `getComment(Long commentId)`
- `addComment(Comment comment)`
- `editComment(Comment comment) `
- `deleteComment(Long commentId)`

- `addCommentReaction(CommentReaction commentReaction)`
- `editCommentReaction(CommentReaction commentReaction)`
- `deleteCommentReaction(Long commentReactionId) `
- `getCommentReactionsOfComment(Long commentId)`
- `getCommentReaction(Long channelId, Long commentId)`: finds the comment reaction that the channel has shown to a video if it exists; if not, null will be returned

- `addPlaylist(Playlist playlist)`
- `editPlaylist(Playlist playlist)`
- `getPlaylistVideos(Long playlistId)`: returns list of videos that are in the playlist
- `getPlaylistChannels(Long playlistId)`: returns list of channels that contain the playlist

- `getAccount(Long accountId)`
- `getAccount(String username, String password)`: inputs username and password, returns user if exists, returns null if it does not exist 
- `addAccount(Account account)`
- `editAccount(Account account)`

- `addVideo(Video video)`
- `editVideo(Video video)`
- `deleteVideo(Long videoId)`

- `getVideoCategories(Long videoId)`
- `addVideoCategory(Long videoId, int categoryId)`:

- `getVideoViewsOfVideo(Long videoId)`: returns all VideoViews of the video
- `getVideoReactions(Long videoId)`: returns reactions of the video
- `getVideoComments(Long videoId)`: returns comments of the video
- `addVideoView(VideoView videoView)`
- `getWatchHistory(Long channelId)`: returns the 100 recent videos that the user watched
- `getCategories()`

### Security Risks
