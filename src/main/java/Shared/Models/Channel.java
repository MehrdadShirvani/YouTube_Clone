package Shared.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ChannelId")
    private Long channelId;

    @Column(name = "Name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "Description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "Location", nullable = false,  length = 50)
    private String location;
    @Column(name = "Picture", nullable = false, columnDefinition = "TEXT")
    private String picture;
    @Column(name = "CreatedDateTime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdDateTime;

    public Channel()
    {

    }
    public Channel(String name, String description, String location, String picture) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.picture = picture;
        this.createdDateTime = new Timestamp(System.currentTimeMillis());
    }

    public Long getChannelId() {
        return channelId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

}
