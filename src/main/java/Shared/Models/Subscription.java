package Shared.Models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
@IdClass(Subscription.SubscriptionId.class)
public class Subscription {

    @Id
    @Column(name = "SubscriberChannelId", nullable = false)
    private Long subscriberChannelId;

    @Id
    @Column(name = "SubscribedChannelId", nullable = false)
    private Long subscribedChannelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubscriberChannelId", referencedColumnName = "ChannelId", insertable = false, updatable = false)
    private Channel subscriberChannel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SubscribedChannelId", referencedColumnName = "ChannelId", insertable = false, updatable = false)
    private Channel subscribedChannel;

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

    public Channel getSubscriberChannel() {
        return subscriberChannel;
    }

    public Channel getSubscribedChannel() {
        return subscribedChannel;
    }

    public Subscription()
    {

    }
    public Subscription(Long subscriberChannelId, Long subscribedChannelId)
    {
        this.subscriberChannelId = subscriberChannelId;
        this.subscribedChannelId = subscribedChannelId;
    }
    public static class SubscriptionId implements Serializable {
        private Long subscriberChannelId;
        private Long subscribedChannelId;
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Subscription.SubscriptionId that = (Subscription.SubscriptionId) o;
            return Objects.equals(subscribedChannelId, that.subscribedChannelId) && Objects.equals(subscriberChannelId, that.subscriberChannelId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(subscribedChannelId, subscriberChannelId);
        }

    }
}