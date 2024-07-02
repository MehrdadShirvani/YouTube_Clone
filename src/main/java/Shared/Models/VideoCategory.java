package Shared.Models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Video_Category")
@IdClass(VideoCategory.VideoCategoryId.class)
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
    public static class VideoCategoryId implements Serializable {
        private Long videoId;
        private Integer categoryId;
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VideoCategory.VideoCategoryId that = (VideoCategory.VideoCategoryId) o;
            return Objects.equals(videoId, that.videoId) && Objects.equals(categoryId, that.categoryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(videoId, categoryId);
        }
    }
}
