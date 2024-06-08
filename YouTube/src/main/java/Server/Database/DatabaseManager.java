package Server.Database;

import Shared.Models.Channel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

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


}
