package Shared.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class VideoCategory {
    @Id
    @Column(name = "VideoId", nullable = false)
    private Long videoId;

    @Id
    @Column(name = "CategoryId", nullable = false)
    private Integer categoryId;

    @ManyToOne
    @JoinColumn(name = "VideoId", referencedColumnName = "VideoId", insertable = false, updatable = false)
    private Video video;

    @ManyToOne
    @JoinColumn(name = "CategoryId", referencedColumnName = "CategoryId", insertable = false, updatable = false)
    private Category category;


    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public VideoCategory()
    {

    }
    public VideoCategory(Long videoId, int categoryId)
    {
        this.videoId = videoId;
        this.categoryId = categoryId;
    }

}
