package Shared.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlaylistId")
    private Long playlistId;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "PlaylistTypeId", nullable = false)
    private Short playlistTypeId;

    @Column(name = "IsPrivate", nullable = false)
    private Boolean isPrivate;

    @Column(name = "CreatedDateTime", nullable = false)
    private Timestamp createdDateTime;

    public Long getPlaylistId() {
        return playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getPlaylistTypeId() {
        return playlistTypeId;
    }

    public void setPlaylistTypeId(Short playlistTypeId) {
        this.playlistTypeId = playlistTypeId;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Playlist()
    {

    }
    public Playlist(String name, Short playlistTypeId,  Boolean isPrivate,Timestamp createdDateTime)
    {
        this.name = name;
        this.playlistTypeId = playlistTypeId;
        this.isPrivate = isPrivate;
        this.createdDateTime = createdDateTime;
    }
}