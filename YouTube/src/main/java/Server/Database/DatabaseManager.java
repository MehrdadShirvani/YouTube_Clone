package Server.Database;

import Shared.Models.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("mainPersistentUnit");
    //region Channels
    public static Channel addChannel(Channel channel) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(channel);
        transaction.commit();

        Channel savedChannel = entityManager.find(Channel.class, channel.getChannelId());

        entityManager.close();
        return savedChannel;
    }
    public static void editChannel(Channel updatedChannel) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Channel mergedChannel = entityManager.merge(updatedChannel);

        transaction.commit();
        entityManager.close();
    }
    public static Channel getChannel(Long channelId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Channel channel = entityManager.find(Channel.class, channelId);

        entityManager.close();
        return channel;
    }
    public static List<Channel> getChannels() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<Channel> channels = entityManager.createQuery(
                            "SELECT c FROM Channels c", Channel.class)
                    .getResultList();
            entityManager.getTransaction().commit();
            return channels;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    public static List<Channel> getSubscribedChannels(Long channelId)
    {
        return new ArrayList<>();
        //TODO
    }
    public static List<Channel> getSubscriberChannels(Long channelId) {
        return new ArrayList<>();
        //TODO
    }
    //endregion

    //region Reactions
    public static Reaction addReaction(Reaction reaction)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(reaction);
        transaction.commit();

        Reaction savedReaction = entityManager.find(Reaction.class, reaction.getReactionId());

        entityManager.close();
        return savedReaction;
    }
    public static void editReaction(Reaction reaction)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Reaction mergedReaction = entityManager.merge(reaction);

        transaction.commit();
        entityManager.close();
    }
    public static Reaction getReaction(Long channelId, Long videoId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
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
        finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
    public static void deleteReaction(Long reactionId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Reaction reaction = entityManager.find(Reaction.class, reactionId);

        if (reaction != null) {
            entityManager.remove(reaction);
        }

        transaction.commit();
        entityManager.close();
    }
    //endregion

    //region Comments
    public static Comment getComment(Long commentId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Comment comment = entityManager.find(Comment.class, commentId);

        entityManager.close();
        return comment;
    }
    public static Comment addComment(Comment comment)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(comment);
        transaction.commit();

        Comment savedComment = entityManager.find(Comment.class, comment.getCommentId());

        entityManager.close();
        return savedComment;
    }
    public static void editComment(Comment comment) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Comment mergedComment = entityManager.merge(comment);

        transaction.commit();
        entityManager.close();
    }
    public static void deleteComment(Long commentId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Comment comment = entityManager.find(Comment.class, commentId);

        if (comment != null) {
            entityManager.remove(comment);
        }

        transaction.commit();
        entityManager.close();
    }
    //endregion

    //region CommentReactions
    public static CommentReaction addCommentReaction(CommentReaction commentReaction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(commentReaction);
        transaction.commit();

        CommentReaction savedCommentReaction = entityManager.find(CommentReaction.class, commentReaction.getCommentId());

        entityManager.close();
        return savedCommentReaction;
    }
    public static void editCommentReaction(CommentReaction commentReaction) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CommentReaction mergedCommentReaction = entityManager.merge(commentReaction);

        transaction.commit();
        entityManager.close();
    }
    public static void deleteCommentReaction(Long commentReactionId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        CommentReaction commentReaction = entityManager.find(CommentReaction.class, commentReactionId);

        if (commentReaction != null) {
            entityManager.remove(commentReaction);
        }

        transaction.commit();
        entityManager.close();
    }
    public static List<CommentReaction> getCommentReactionsOfComment(Long commentId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        TypedQuery<CommentReaction> query = entityManager.createQuery(
                "SELECT cr FROM CommentReactions cr WHERE cr.commentId = :commentId", CommentReaction.class);
        query.setParameter("commentId", commentId);
        return  query.getResultList();
    }
    public static CommentReaction getCommentReaction(Long channelId, Long commentId)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        TypedQuery<CommentReaction> query = entityManager.createQuery(
                "SELECT cr FROM CommentReactions cr WHERE cr.commentId = :commentId", CommentReaction.class);
        query.setParameter("commentId", commentId);
        try
        {
            return query.getSingleResult();
        }catch (NoResultException e)
        {
            return null;
        }
    }
    //endregion

    //region Playlists
    public static Playlist addPlaylist(Playlist playlist)
    {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.persist(playlist);
        transaction.commit();

        Playlist savedPlaylist = entityManager.find(Playlist.class, playlist.getPlaylistId());

        entityManager.close();
        return savedPlaylist;
    }
    public static void editPlaylist(Playlist playlist) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Playlist mergePlaylist = entityManager.merge(playlist);

        transaction.commit();
        entityManager.close();
    }
    public static List<Video> getPlaylistVideos(Long playlistId)
    {
        return new ArrayList<>();
        //TODO
    }
    public static List<Channel> getPlaylistChannels(Long playlistId)
    {
        return new ArrayList<>();
        //TODO
    }
    //endregion
}
