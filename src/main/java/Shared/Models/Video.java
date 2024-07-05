package Shared.Models;

import Server.Database.DatabaseManager;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Iterator;

@Entity
@Table(name = "Videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VideoId")
    private Long videoId;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "Thumbnail",  columnDefinition = "TEXT")
    private String thumbnail;

    @Column(name = "ChannelId", nullable = false)
    private Long channelId;

    @Column(name = "CreatedDateTime", nullable = false)
    private Timestamp createdDateTime;

    @Column(name = "IsPrivate", nullable = false)
    private Boolean isPrivate;

    @Column(name = "IsAgeRestricted", nullable = false)
    private Boolean isAgeRestricted;

    @Column(name = "Duration")
    private Integer duration;
    @Column(name = "VideoTypeId")
    private Integer videoTypeId;
    @ManyToOne
    @JoinColumn(name = "ChannelId", insertable = false, updatable = false)
    private Channel channel;

    public Long getVideoId() {
        return videoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getAgeRestricted() {
        return isAgeRestricted;
    }

    public void setAgeRestricted(Boolean ageRestricted) {
        isAgeRestricted = ageRestricted;
    }


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Video()
    {

    }
    public Video(String name, String description, Long channelId, Boolean isPrivate, Boolean isAgeRestricted)
    {
        this.name = name;
        this.description = description;
        this.channelId = channelId;
        this.createdDateTime = new Timestamp(System.currentTimeMillis());;
        this.isPrivate = isPrivate;
        this.isAgeRestricted = isAgeRestricted;
    }

    public void updateVideoDetails(String thumbnail, int duration)
    {
        if(videoId <= 0)
        {
            return;
        }

        this.thumbnail = thumbnail;
        this.duration = duration;
        try
        {
            DatabaseManager.editVideo(this);
        }
        catch(Exception ignore)
        {

        }
    }


    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }
}