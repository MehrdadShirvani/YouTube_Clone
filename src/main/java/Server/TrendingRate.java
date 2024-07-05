package Server;

import Server.Database.DatabaseManager;
import Shared.Models.Comment;
import Shared.Models.Reaction;
import Shared.Models.Video;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


public class TrendingRate{
    private Long videoId;
    private Long viewCount;
    private Long numberOfLikes;
    private Long numberOfDislikes;
    private Long numberOfComments;

    public TrendingRate(Video video) {
        List<Reaction> videoReactions = DatabaseManager.getVideoReactions(videoId);
        List<Comment> comments = DatabaseManager.getVideoComments(videoId);

        this.videoId = video.getVideoId();
        this.viewCount = DatabaseManager.getNumberOfViews(this.videoId);
        this.numberOfLikes = (videoReactions != null) ? videoReactions.stream().filter(videoReaction -> videoReaction.getReactionTypeId() == 1).count() : 0;
        this.numberOfDislikes = (videoReactions != null) ? videoReactions.stream().filter(videoReaction -> videoReaction.getReactionTypeId() == -1).count() : 0;
        this.numberOfComments = (comments != null) ? comments.stream().count() : 0;
    }

    private double getEngagementRate() {
        Long totalInteractions = numberOfLikes + numberOfDislikes + numberOfComments;
        return (double) totalInteractions / viewCount;
    }

    public double calculateRating() {
        double viewWeight = 0.4;
        double engagementRateWeight = 0.6;

        double viewScore = viewCount;
        double engagementRateScore = getEngagementRate();

        return (viewScore * viewWeight) + (engagementRateScore * engagementRateWeight);
    }
}


