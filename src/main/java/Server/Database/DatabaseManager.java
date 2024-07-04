package Server.Database;
import Shared.Models.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DatabaseManager {
    static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mainPersistentUnit");
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // Custom thread pool
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
                            "SELECT c FROM Channel c", Channel.class).getResultList();
            entityManager.getTransaction().commit();
            return channels;
        }
    }


    public static List<Channel> getSubscribedChannels(Long channelId) {
        Channel channel = getChannel(channelId);
        if (channel == null) {
            return null;
        }

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            StringBuilder jpql = new StringBuilder("SELECT c ")
                    .append("FROM Channel c ")
                    .append("JOIN Subscription s ON c.channelId = s.subscribedChannelId ")
                    .append("WHERE s.subscriberChannelId = :channelId");

            TypedQuery<Channel> query = entityManager.createQuery(jpql.toString(), Channel.class);
            query.setParameter("channelId", channelId);

            List<Channel> results = query.getResultList();
            return results;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public static List<Channel> getSubscriberChannels(Long channelId) {
        Channel channel = getChannel(channelId);
        if (channel == null) {
            return null;
        }

        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            StringBuilder jpql = new StringBuilder("SELECT c ")
                    .append("FROM Channel c ")
                    .append("JOIN Subscription s ON c.channelId = s.subscriberChannelId ")
                    .append("WHERE s.subscribedChannelId = :channelId");

            TypedQuery<Channel> query = entityManager.createQuery(jpql.toString(), Channel.class);
            query.setParameter("channelId", channelId);

            List<Channel> results = query.getResultList();
            return results;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public static boolean isSubscribedToChannel(long subscriberChannelId, long targetChannelId)
    {
        List<Channel> list = getSubscribedChannels(subscriberChannelId);
        for(Channel channel : list)
        {
            if(channel.getChannelId() == targetChannelId)
            {
                return true;
            }
        }
        return false;
    }

    public static long getNumberOfViews(long videoId) {
        try {
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                EntityManager entityManager = null;
                try {
                    entityManager = entityManagerFactory.createEntityManager();
                    TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(v) FROM VideoView v WHERE v.videoId = :videoId", Long.class);
                    query.setParameter("videoId", videoId);
                    return query.getSingleResult();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    if (entityManager != null) {
                        entityManager.close();
                    }
                }
            }, executorService);

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isChannelNameUnique(String name)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Channel> query = entityManager.createQuery(
                    "SELECT c FROM Channel c WHERE c.Name = :name", Channel.class);
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
                    "SELECT s FROM Subscription s WHERE s.SubscriberChannelId = :SubscriberChannelId AND s.SubscribedChannelId = :SubscribedChannelId", Subscription.class);
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
                    "SELECT s FROM Subscription s WHERE s.SubscriberChannelId = :SubscriberChannelId AND s.SubscribedChannelId = :SubscribedChannelId", Subscription.class);
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
                    "SELECT r FROM Reaction r WHERE r.channelId = :channelId AND r.videoId = :videoId", Reaction.class);
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

    public static List<Comment> getCommentsRepliedToComment(Long commentId)
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
                    "SELECT c FROM Comment c WHERE c.RepliedCommentId = :RepliedCommentId AND r.videoId = :videoId", Comment.class);
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
                    "SELECT cr FROM CommentReaction cr WHERE cr.commentId = :commentId", CommentReaction.class);
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
                    "SELECT cr FROM CommentReaction cr WHERE cr.channelId = :channelId AND cr.commentId = :commentId", CommentReaction.class);
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
            Join<Video, VideoPlaylist> vidoePlaylists = videoRoot.join("VideoPlaylist", JoinType.INNER);

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
                    "SELECT vc FROM VideoPlaylist vc WHERE vc.videoId = :videoId", VideoPlaylist.class);
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
            Join<Channel, ChannelPlaylist> channelPlaylists = channelRoot.join("ChannelPlaylist", JoinType.INNER);

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
                    "SELECT v FROM VideoPlaylist v WHERE v.VideoId = :VideoId AND s.PlaylistId = :PlaylistId", VideoPlaylist.class);
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
                    "SELECT v FROM VideoPlaylist v WHERE v.VideoId = :VideoId AND s.PlaylistId = :PlaylistId", VideoPlaylist.class);
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
                    "SELECT v FROM ChannelPlaylist v WHERE v.ChannelId = :ChannelId AND s.PlaylistId = :PlaylistId", ChannelPlaylist.class);
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
                    "SELECT v FROM ChannelPlaylist v WHERE v.ChannelId = :ChannelId AND s.PlaylistId = :PlaylistId", ChannelPlaylist.class);
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
    public static Account getAccount(String usernameOrEmail, String password)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {

            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Account a WHERE (a.username = :username OR a.email = :email) AND a.password = :password", Account.class);
            query.setParameter("username", usernameOrEmail);
            query.setParameter("email", usernameOrEmail);
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

            Channel channel = addChannel(new Channel(account.getUsername(),"Welcome to " + account.getUsername() +"'s Channel!", ""));
            savedAccount.setChannelId(channel.getChannelId());
            editAccount(savedAccount);

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
                    "SELECT a FROM Account a WHERE a.username = :username", Account.class);
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
                    "SELECT a FROM Account a WHERE a.email = :email", Account.class);
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

    public static List<Category> getMostViewedCategoriesOfUsers(long channelId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            String jpql = "SELECT vc.categoryId, COUNT(vv.videoId) AS viewCount " +
                    "FROM VideoView vv " +
                    "JOIN VideoCategory vc ON vv.videoId = vc.videoId " +
                    "WHERE vv.channelId = :channelId " +
                    "GROUP BY vc.categoryId " +
                    "ORDER BY viewCount DESC";

            Query query = entityManager.createQuery(jpql);
            query.setMaxResults(5);
            query.setParameter("channelId", channelId);

            List<Object[]> result = query.getResultList();
            entityManager.close();
            List<Category> categories = new ArrayList<>();
            for (Object[] item : result)
            {
                categories.add(getCategory(Integer.parseInt(item[0].toString())));
            }

            return categories;
        }
    }
    public static List<Video> searchVideo(long channelId, List<Category> categories, String searchTerms, int perPage, int pageNumber) {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();

            StringBuilder jpql = new StringBuilder("SELECT v, COUNT(vv.videoId) AS viewCount, COUNT(vc.categoryId) AS categoryCount ")
                    .append("FROM Video v ")
                    .append("JOIN VideoCategory vc ON v.videoId = vc.videoId ")
                    .append("LEFT JOIN VideoView vv ON v.videoId = vv.videoId ")
                    .append("WHERE v.channelId != :channelId ");

            if (categories != null && !categories.isEmpty()) {
                jpql.append("AND vc.categoryId IN :categoryIds ");
            }

            List<String> searchTermsList = Arrays.asList(searchTerms.split("\\s+"));
            if (!searchTerms.isEmpty()) {
                jpql.append("AND (");
                jpql.append(searchTermsList.stream()
                        .map(term -> "LOWER(v.name) LIKE LOWER(:term" + searchTermsList.indexOf(term) + ")")
                        .collect(Collectors.joining(" OR ")));
                jpql.append(") ");
            }

            jpql.append("GROUP BY v.videoId, v.name ")
                    .append("ORDER BY categoryCount DESC, viewCount DESC");

            TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);
            query.setParameter("channelId", channelId);

            if (categories != null && !categories.isEmpty()) {
                List<Integer> categoryIds = categories.stream()
                        .map(Category::getCategoryId)
                        .collect(Collectors.toList());
                query.setParameter("categoryIds", categoryIds);
            }

            if (!searchTerms.isEmpty()) {
                for (int i = 0; i < searchTermsList.size(); i++) {
                    query.setParameter("term" + i, "%" + searchTermsList.get(i) + "%");
                }
            }

            query.setFirstResult((pageNumber - 1) * perPage);
            query.setMaxResults(perPage);

            List<Object[]> results = query.getResultList();
            List<Video> videos = results.stream()
                    .map(result -> (Video) result[0])
                    .collect(Collectors.toList());

            return videos;
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
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
        if(getVideo(videoId) == null)
        {
            return;
        }

        List<VideoView> videoViews = getVideoViewsOfVideo(videoId);
        for(VideoView videoView : videoViews)
        {
            deleteVideoView(videoView.getVideoViewId());
        }

        List<Reaction> reactions = getVideoReactions(videoId);
        for(Reaction reaction : reactions)
        {
            deleteReaction(reaction.getReactionId());
        }

        List<Comment> comments = getVideoComments(videoId);
        for(Comment comment : comments)
        {
            deleteComment(comment.getCommentId());
        }

        List<VideoCategory> videoCategories = getVideoCategories(videoId);
        for(VideoCategory videoCategory : videoCategories)
        {
            deleteVideoCategory(videoCategory);
        }

        List<VideoPlaylist> videoPlaylists = getVideoPlaylists(videoId);
        for(VideoPlaylist videoPlaylist : videoPlaylists)
        {
            deleteVideoPlaylist(videoPlaylist);
        }

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Video video = entityManager.find(Video.class, videoId);

            if (video != null) {
                entityManager.remove(video);
            }

            transaction.commit();
        }
    }
    public static List<Category> getCategoriesOfVideo(Long videoId) {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Category> cq = cb.createQuery(Category.class).distinct(true);
            Root<Category> categoryRoot = cq.from(Category.class);
            Join<Category, VideoCategory> videoViewJoin = categoryRoot.join("VideoCategory", JoinType.INNER);

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
                    "SELECT vc FROM VideoCategory vc WHERE vc.videoId = :videoId", VideoCategory.class);
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
                    "SELECT vc FROM VideoCategory vc WHERE vc.videoId = :videoId AND vc.categoryId = :categoryId", VideoCategory.class);
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
                    "SELECT v FROM VideoCategory v WHERE v.VideoId = :VideoId AND s.CategoryId = :CategoryId", VideoCategory.class);
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
            TypedQuery<VideoView> query = entityManager.createQuery("SELECT v FROM VideoView v WHERE v.videoId = :videoId", VideoView.class);
            query.setParameter("videoId",videoId);
            return query.getResultList();
        }
    }
    public static List<Reaction> getVideoReactions(Long videoId)
    {
        try {
            CompletableFuture<List<Reaction>> future = CompletableFuture.supplyAsync(() -> {
                try(EntityManager entityManager = entityManagerFactory.createEntityManager())
                {
                    entityManager.getTransaction().begin();
                    TypedQuery<Reaction> query = entityManager.createQuery("SELECT r FROM Reaction r WHERE r.videoId = :videoId", Reaction.class);
                    query.setParameter("videoId",videoId);
                    return query.getResultList();
                }catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static List<Comment> getVideoComments(Long videoId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            entityManager.getTransaction().begin();
            TypedQuery<Comment> query = entityManager.createQuery("SELECT c FROM Comment c WHERE c.videoId = :videoId AND c.repliedCommentId <= 0", Comment.class);
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

    public static void deleteVideoView(Long videoViewId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            VideoView videoView = entityManager.find(VideoView.class, videoViewId);

            if (videoView != null) {
                entityManager.remove(videoView);
            }

            transaction.commit();
        }
    }
    public static List<Video> getWatchHistory(Long channelId) {

        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Video> cq = cb.createQuery(Video.class).distinct(true);
            Root<Video> videoRoot = cq.from(Video.class);
            Join<Video, VideoView> videoViewJoin = videoRoot.join("VideoView", JoinType.INNER);

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
                            "SELECT c FROM Category c", Category.class)
                    .getResultList();
            entityManager.getTransaction().commit();
            return categories;
        }
    }
    public static Category getCategory(Integer categoryId)
    {
        try(EntityManager entityManager = entityManagerFactory.createEntityManager())
        {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            Category category = entityManager.find(Category.class, categoryId);

            entityManager.close();
            return category;
        }
    }
    //endregion
}