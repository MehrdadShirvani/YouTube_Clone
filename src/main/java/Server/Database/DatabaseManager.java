package Server.Database;

import Shared.Models.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.List;

public class DatabaseManager {
    static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mainPersistentUnit");
    //region Channels
    public static Channel addChannel(Channel channel) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(channel);
            transaction.commit();

            Channel savedChannel = entityManager.find(Channel.class, channel.getChannelId());

            entityManager.close();
            return savedChannel;
        }
    }
    public static Channel editChannel(Channel updatedChannel) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Channel mergedChannel = entityManager.merge(updatedChannel);

            transaction.commit();
            entityManager.close();
            return mergedChannel;
        }
    }

    public static Channel getChannel(Long channelId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Channel channel = entityManager.find(Channel.class, channelId);

            entityManager.close();
            return channel;
        }
    }
    public static List<Channel> getChannels() {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            entityManager.getTransaction().begin();
            List<Channel> channels = entityManager.createQuery(
                            "SELECT c FROM Channels c", Channel.class).getResultList();
            entityManager.getTransaction().commit();
            return channels;
        }
    }


    public static List<Channel> getSubscribedChannels(Long channelId)
    {
        Channel channel = getChannel(channelId);
        if(channel == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Channel> cq = cb.createQuery(Channel.class).distinct(true);
            Root<Channel> channelRoot = cq.from(Channel.class);
            Join<Channel, Subscription> subscriptions = channelRoot.join("subscriptions", JoinType.INNER);

            cq.select(channelRoot)
                    .where(cb.equal(subscriptions.get("SubscriberChannelId"), channelId));

            TypedQuery<Channel> query = entityManager.createQuery(cq);
            return query.getResultList();
        }
    }
    public static List<Channel> getSubscriberChannels(Long channelId) {
        Channel channel = getChannel(channelId);
        if(channel == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Channel> cq = cb.createQuery(Channel.class).distinct(true);
            Root<Channel> channelRoot = cq.from(Channel.class);
            Join<Channel, Subscription> subscriptions = channelRoot.join("SubscribedChannelId", JoinType.INNER);

            cq.select(channelRoot)
                    .where(cb.equal(subscriptions.get("channelId"), channelId));

            TypedQuery<Channel> query = entityManager.createQuery(cq);
            return query.getResultList();
        }
    }
    public static boolean isChannelNameUnique(String name)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Channel> query = entityManager.createQuery(
                    "SELECT c FROM Channels c WHERE c.Name = :name", Channel.class);
            query.setParameter("name", name);

            try
            {
                query.getSingleResult();
                return false;
            }
            catch (NoResultException e)
            {
                return true;
            }
        }
    }


    public static Subscription addSubscription(Long subscriberChannelId ,Long subscribedChannelId)
    {
        if(getChannel(subscriberChannelId) == null || getChannel(subscribedChannelId) == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Subscription> query = entityManager.createQuery(
                    "SELECT s FROM subscriptions s WHERE s.SubscriberChannelId = :SubscriberChannelId AND s.SubscribedChannelId = :SubscribedChannelId", Subscription.class);
            query.setParameter("SubscriberChannelId", subscriberChannelId);
            query.setParameter("SubscribedChannelId", subscribedChannelId);
            Subscription subscription = new Subscription(subscriberChannelId,subscribedChannelId);

            try
            {
                query.getSingleResult();
            }
            catch (NoResultException e){
                entityManager.persist(subscription);
                transaction.commit();
            }

            entityManager.close();
            return subscription;
        }
    }

    public static void deleteSubscription(Long subscriberChannelId ,Long subscribedChannelId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Subscription> query = entityManager.createQuery(
                    "SELECT s FROM subscriptions s WHERE s.SubscriberChannelId = :SubscriberChannelId AND s.SubscribedChannelId = :SubscribedChannelId", Subscription.class);
            query.setParameter("SubscriberChannelId", subscriberChannelId);
            query.setParameter("SubscribedChannelId", subscribedChannelId);

            try
            {
                Subscription subscription = query.getSingleResult();
                entityManager.remove(subscription);
                transaction.commit();
            }
            catch (NoResultException e){
            }
        }
    }
    //endregion

    //region Reactions
    public static Reaction addReaction(Reaction reaction)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(reaction);
            transaction.commit();

            Reaction savedReaction = entityManager.find(Reaction.class, reaction.getReactionId());
            entityManager.close();
            return savedReaction;
        }
    }
    public static Reaction editReaction(Reaction reaction)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Reaction mergedReaction = entityManager.merge(reaction);

            transaction.commit();
            entityManager.close();

            return mergedReaction;
        }
    }
    public static Reaction getReaction(Long channelId, Long videoId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Reaction> query = entityManager.createQuery(
                    "SELECT r FROM Reactions r WHERE r.channelId = :channelId AND r.videoId = :videoId", Reaction.class);
            query.setParameter("videoId", videoId);
            query.setParameter("channelId", channelId);

            try
            {
                return query.getSingleResult();
            }
            catch (NoResultException e)
            {
                return null;
            }
        }
    }
    public static void deleteReaction(Long reactionId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Reaction reaction = entityManager.find(Reaction.class, reactionId);

            if (reaction != null) {
                entityManager.remove(reaction);
            }

            transaction.commit();
        }
    }
    //endregion

    //region Comments
    public static Comment getComment(Long commentId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Comment comment = entityManager.find(Comment.class, commentId);

            entityManager.close();
            return comment;
        }
    }
    public static Comment addComment(Comment comment)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(comment);
            transaction.commit();

            Comment savedComment = entityManager.find(Comment.class, comment.getCommentId());

            entityManager.close();
            return savedComment;
        }
    }
    public static Comment editComment(Comment comment) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Comment mergedComment = entityManager.merge(comment);

            transaction.commit();
            entityManager.close();

            return mergedComment;
        }
    }
    public static void deleteComment(Long commentId)
    {

        //Delete CommentReactions
        List<CommentReaction> commentReactions = getCommentReactionsOfComment(commentId);
        for(CommentReaction commentReaction : commentReactions)
        {
            deleteCommentReaction(commentReaction.getCommentReactionId());
        }
        //Delete RepliedComments
        List<Comment> comments = getCommentsRepliedToComment(commentId);
        for(Comment comment : comments)
        {
            deleteComment(comment.getCommentId());
        }


        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Comment comment = entityManager.find(Comment.class, commentId);

            if (comment != null) {
                entityManager.remove(comment);
            }

            transaction.commit();
            entityManager.close();
        }
    }

    private static List<Comment> getCommentsRepliedToComment(Long commentId)
    {
        Comment comment = getComment(commentId);
        if(comment == null) {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Comment> query = entityManager.createQuery(
                    "SELECT c FROM Comments c WHERE c.RepliedCommentId = :RepliedCommentId AND r.videoId = :videoId", Comment.class);
            query.setParameter("RepliedCommentId", commentId);
            return  query.getResultList();
        }
    }
    //endregion

    //region CommentReactions
    public static CommentReaction addCommentReaction(CommentReaction commentReaction) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(commentReaction);
        transaction.commit();

        CommentReaction savedCommentReaction = entityManager.find(CommentReaction.class, commentReaction.getCommentId());

        entityManager.close();
        return savedCommentReaction;
        }
    }
    public static CommentReaction editCommentReaction(CommentReaction commentReaction) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            CommentReaction mergedCommentReaction = entityManager.merge(commentReaction);

            transaction.commit();
            entityManager.close();

            return mergedCommentReaction;
        }
    }
    public static void deleteCommentReaction(Long commentReactionId) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            CommentReaction commentReaction = entityManager.find(CommentReaction.class, commentReactionId);

            if (commentReaction != null) {
                entityManager.remove(commentReaction);
            }

            transaction.commit();
            entityManager.close();
        }
    }
    public static List<CommentReaction> getCommentReactionsOfComment(Long commentId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<CommentReaction> query = entityManager.createQuery(
                    "SELECT cr FROM CommentReactions cr WHERE cr.commentId = :commentId", CommentReaction.class);
            query.setParameter("commentId", commentId);
            return  query.getResultList();
        }
    }
    public static CommentReaction getCommentReaction(Long channelId, Long commentId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<CommentReaction> query = entityManager.createQuery(
                    "SELECT cr FROM CommentReactions cr WHERE cr.channelId = :channelId AND cr.commentId = :commentId", CommentReaction.class);
            query.setParameter("channelId", channelId);
            query.setParameter("commentId", commentId);
            try
            {
                return query.getSingleResult();
            }catch (NoResultException e)
            {
                return null;
            }
        }
    }
    //endregion

    //region Playlists
    public static Playlist addPlaylist(Playlist playlist)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(playlist);
            transaction.commit();

            Playlist savedPlaylist = entityManager.find(Playlist.class, playlist.getPlaylistId());

            entityManager.close();
            return savedPlaylist;
        }
    }
    public static Playlist editPlaylist(Playlist playlist) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Playlist mergePlaylist = entityManager.merge(playlist);

            transaction.commit();
            entityManager.close();

            return  mergePlaylist;
        }
    }
    public static Playlist getPlaylist(Long playlistId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Playlist playlist = entityManager.find(Playlist.class, playlistId);

            entityManager.close();
            return playlist;
        }
    }

    public static List<Video> getVideosOfPlaylist(Long playlistId)
    {
        Playlist playlist = getPlaylist(playlistId);
        if(playlist == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Video> cq = cb.createQuery(Video.class).distinct(true);
            Root<Video> videoRoot = cq.from(Video.class);
            Join<Video, VideoPlaylist> vidoePlaylists = videoRoot.join("Video_Playlist", JoinType.INNER);

            cq.select(videoRoot)
                    .where(cb.equal(vidoePlaylists.get("playlistId"), playlistId));

            TypedQuery<Video> query = entityManager.createQuery(cq);
            return query.getResultList();
        }
    }

    public static List<VideoPlaylist> getVideoPlaylists(Long videoId) {
        if(getVideo(videoId) == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<VideoPlaylist> query = entityManager.createQuery(
                    "SELECT vc FROM Video_Playlist vc WHERE vc.videoId = :videoId", VideoPlaylist.class);
            query.setParameter("videoId", videoId);
            return query.getResultList();
        }
    }
    public static List<Channel> getChannelsOfPlaylist(Long playlistId)
    {
        Playlist playlist = getPlaylist(playlistId);
        if(playlist == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Channel> cq = cb.createQuery(Channel.class).distinct(true);
            Root<Channel> channelRoot = cq.from(Channel.class);
            Join<Channel, ChannelPlaylist> channelPlaylists = channelRoot.join("Channel_Playlist", JoinType.INNER);

            cq.select(channelRoot)
                    .where(cb.equal(channelPlaylists.get("playlistId"), playlistId));

            TypedQuery<Channel> query = entityManager.createQuery(cq);
            return query.getResultList();
        }
    }

    public static VideoPlaylist addVideoPlaylist(Long videoId, Long playlistId)
    {
        if(getVideo(videoId) == null || getPlaylist(playlistId) == null) {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<VideoPlaylist> query = entityManager.createQuery(
                    "SELECT v FROM video_playlist v WHERE v.VideoId = :VideoId AND s.PlaylistId = :PlaylistId", VideoPlaylist.class);
            query.setParameter("VideoId", videoId);
            query.setParameter("PlaylistId", playlistId);
            VideoPlaylist videoPlaylist = new VideoPlaylist(videoId,playlistId);

            try
            {
                return query.getSingleResult();
            }
            catch (NoResultException e){
                entityManager.persist(videoPlaylist);
                transaction.commit();
            }

            entityManager.close();
            return videoPlaylist;
        }
    }
    public static void deleteVideoPlaylist(VideoPlaylist videoPlaylist)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            if (videoPlaylist != null) {
                entityManager.remove(videoPlaylist);
            }

            transaction.commit();
        }
    }

    public static void deleteVideoPlaylist(Long videoId, Long playlistId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<VideoPlaylist> query = entityManager.createQuery(
                    "SELECT v FROM video_playlist v WHERE v.VideoId = :VideoId AND s.PlaylistId = :PlaylistId", VideoPlaylist.class);
            query.setParameter("VideoId", videoId);
            query.setParameter("PlaylistId", playlistId);


            try
            {
                VideoPlaylist videoPlaylist = query.getSingleResult();
                entityManager.remove(videoPlaylist);
                transaction.commit();
            }
            catch (NoResultException e){
            }
        }
    }

    public static ChannelPlaylist addChannelPlaylist(Long channelId, Long playlistId)
    {
        if(getChannel(channelId) == null || getPlaylist(playlistId) == null) {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<ChannelPlaylist> query = entityManager.createQuery(
                    "SELECT v FROM channel_playlist v WHERE v.ChannelId = :ChannelId AND s.PlaylistId = :PlaylistId", ChannelPlaylist.class);
            query.setParameter("ChannelId", channelId);
            query.setParameter("PlaylistId", playlistId);
            ChannelPlaylist channelPlaylist = new ChannelPlaylist(channelId,playlistId);

            try
            {
                return query.getSingleResult();
            }
            catch (NoResultException e){
                entityManager.persist(channelPlaylist);
                transaction.commit();
            }

            entityManager.close();
            return channelPlaylist;
        }
    }
    public static void deleteChannelPlaylist(Long channelId, Long playlistId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<ChannelPlaylist> query = entityManager.createQuery(
                    "SELECT v FROM channel_playlist v WHERE v.ChannelId = :ChannelId AND s.PlaylistId = :PlaylistId", ChannelPlaylist.class);
            query.setParameter("ChannelId", channelId);
            query.setParameter("PlaylistId", playlistId);


            try
            {
                ChannelPlaylist channelPlaylist = query.getSingleResult();
                entityManager.remove(channelPlaylist);
                transaction.commit();
            }
            catch (NoResultException ignored){
            }
        }
    }
    //endregion

    //region Accounts
    public static Account getAccount(Long accountId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Account account = entityManager.find(Account.class, accountId);

            entityManager.close();
            return account;
        }
    }
    public static Account getAccount(String username, String password)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {

            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Accounts a WHERE a.username = :username AND a.password = :password", Account.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            try
            {
                return query.getSingleResult();
            }catch (NoResultException e)
            {
                return null;
            }
        }
    }
    public static Account addAccount(Account account)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(account);
            transaction.commit();

            Account savedAccount = entityManager.find(Account.class, account.getAccountId());

            entityManager.close();
            return savedAccount;
        }
    }
    public static Account editAccount(Account account)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Account mergeAccount = entityManager.merge(account);

            transaction.commit();
            entityManager.close();

            return mergeAccount;
        }
    }

    public static boolean isUsernameUnique(String username)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Accounts a WHERE a.username = :username", Account.class);
            query.setParameter("username", username);

            try
            {
                query.getSingleResult();
                return false;
            }
            catch (NoResultException e)
            {
                return true;
            }
        }
    }
    public static boolean isEmailUnique(String email)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Accounts a WHERE a.email = :email", Account.class);
            query.setParameter("email", email);

            try
            {
                query.getSingleResult();
                return false;
            }
            catch (NoResultException e)
            {
                return true;
            }
        }
    }
    //endregion

    //region Videos
    public static Video addVideo(Video video) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(video);
            transaction.commit();

            Video savedVideo = entityManager.find(Video.class, video.getVideoId());

            entityManager.close();
            return savedVideo;
        }
    }
    public static Video editVideo(Video video)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Video mergedVideo = entityManager.merge(video);

            transaction.commit();
            entityManager.close();

            return mergedVideo;
        }
    }
    public static Video getVideo(Long videoId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Video video = entityManager.find(Video.class, videoId);

            entityManager.close();
            return video;
        }

    }
    public static void deleteVideo(Long videoId)
    {
        //TODO
        //Delete VideoViews
        //Delete Comments
        //Delete CommentReactions
        //Delete RepliedComment
        //Delete Video_Category
        //Delete Video_Playlist

    }

    public static List<Category> getVideoCategories(Long videoId) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Category> cq = cb.createQuery(Category.class).distinct(true);
            Root<Category> categoryRoot = cq.from(Category.class);
            Join<Category, VideoCategory> videoViewJoin = categoryRoot.join("Video_Category", JoinType.INNER);

            cq.select(categoryRoot)
                    .where(cb.equal(videoViewJoin.get("videoId"), videoId));

            TypedQuery<Category> query = entityManager.createQuery(cq);
            return query.getResultList();
        }
    }
    public static List<VideoCategory> getVideoCategories(Long videoId) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<VideoCategory> query = entityManager.createQuery(
                    "SELECT vc FROM Video_Category vc WHERE vc.videoId = :videoId", VideoCategory.class);
            query.setParameter("videoId", videoId);
            return query.getResultList();
        }
    }
    public static VideoCategory addVideoCategory(Long videoId, int categoryId) {
        if(getVideo(videoId) == null || getCategory(categoryId) == null)
        {
            return null;
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<VideoCategory> query = entityManager.createQuery(
                    "SELECT vc FROM Video_Category vc WHERE vc.videoId = :videoId AND vc.categoryId = :categoryId", VideoCategory.class);
            query.setParameter("videoId", videoId);
            query.setParameter("categoryId", categoryId);
            VideoCategory videoCategory = new VideoCategory(videoId, categoryId);

            try
            {
                query.getSingleResult();
            }
            catch (NoResultException e){
                entityManager.persist(videoCategory);
                transaction.commit();
            }

            entityManager.close();
            return videoCategory;
        }
    }
    public static void deleteVideoCategory(VideoCategory videoCategory) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            if (videoCategory != null) {
                entityManager.remove(videoCategory);
            }

            transaction.commit();
        }
    }
    public static void deleteVideoCategory(Long videoId, Integer categoryId) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<VideoCategory> query = entityManager.createQuery(
                    "SELECT v FROM video_category v WHERE v.VideoId = :VideoId AND s.CategoryId = :CategoryId", VideoCategory.class);
            query.setParameter("VideoId", videoId);
            query.setParameter("CategoryId", categoryId);


            try
            {
                VideoCategory videoCategory = query.getSingleResult();
                entityManager.remove(videoCategory);
                transaction.commit();
            }
            catch (NoResultException ignored){
            }
        }
    }

    public static List<VideoView> getVideoViewsOfVideo(Long videoId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            entityManager.getTransaction().begin();
            TypedQuery<VideoView> query = entityManager.createQuery("SELECT v FROM VideoViews v WHERE v.videoId = :videoId", VideoView.class);
            query.setParameter("videoId",videoId);
            return query.getResultList();
        }
    }
    public static List<Reaction> getVideoReactions(Long videoId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            entityManager.getTransaction().begin();
            TypedQuery<Reaction> query = entityManager.createQuery("SELECT r FROM Reactions r WHERE r.videoId = :videoId", Reaction.class);
            query.setParameter("videoId",videoId);
            return query.getResultList();
        }
    }
    public static List<Comment> getVideoComments(Long videoId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            entityManager.getTransaction().begin();
            TypedQuery<Comment> query = entityManager.createQuery("SELECT c FROM Comments c WHERE c.videoId = :videoId", Comment.class);
            query.setParameter("videoId",videoId);
            return query.getResultList();
        }
    }
    public static VideoView addVideoView(VideoView videoView)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.persist(videoView);
            transaction.commit();

            VideoView savedVideoView = entityManager.find(VideoView.class, videoView.getVideoViewId());

            entityManager.close();
            return savedVideoView;
        }
    }
    public static List<Video> getWatchHistory(Long channelId) {

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Video> cq = cb.createQuery(Video.class).distinct(true);
            Root<Video> videoRoot = cq.from(Video.class);
            Join<Video, VideoView> videoViewJoin = videoRoot.join("videoViews", JoinType.INNER);

            cq.select(videoRoot)
                    .where(cb.equal(videoViewJoin.get("channelId"), channelId))
                    .orderBy(cb.desc(videoViewJoin.get("ViewDateTime")));

            TypedQuery<Video> query = entityManager.createQuery(cq);
            query.setMaxResults(100);

            return query.getResultList();
        }
    }
    //endregion

    //region Categories
    public static List<Category> getCategories()
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            entityManager.getTransaction().begin();
            List<Category> categories = entityManager.createQuery(
                            "SELECT c FROM Categories c", Category.class)
                    .getResultList();
            entityManager.getTransaction().commit();
            return categories;
        }
    }
    //endregion
}