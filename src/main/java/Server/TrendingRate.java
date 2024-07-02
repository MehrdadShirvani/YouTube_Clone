package Server;

import java.time.Duration;
import java.time.LocalDateTime;


public class TrendingRate{
    private int viewCount;
    private int likes;
    private int dislikes;
    private int comments;
    private LocalDateTime lastCheckedDate;
    private int previousViewCount;
    private int previousLikes;
    private int previousComments;

    public TrendingRate(int viewCount, int likes, int dislikes, int comments , LocalDateTime lastCheckedDate, int previousViewCount, int previousLikes, int previousComments, int previousShares) {
        this.viewCount = viewCount;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.lastCheckedDate = lastCheckedDate;
        this.previousViewCount = previousViewCount;
        this.previousLikes = previousLikes;
        this.previousComments = previousComments;
    }

    public double getEngagementRate() {
        int totalInteractions = likes + dislikes + comments;
        return (double) totalInteractions / viewCount;
    }

    public double getGrowthRate() {
        Duration duration = Duration.between(lastCheckedDate, LocalDateTime.now());
        double days = duration.toHours() / 24.0;
        double viewGrowthRate = (double) (viewCount - previousViewCount) / previousViewCount / days;
        double likeGrowthRate = (double) (likes - previousLikes) / previousLikes / days;
        double commentGrowthRate = (double) (comments - previousComments) / previousComments / days;

        return (viewGrowthRate + likeGrowthRate + commentGrowthRate) / 4;
    }

    public double calculateRating() {
        double viewWeight = 0.4;
        double engagementRateWeight = 0.3;
        double growthRateWeight = 0.3;

        double viewScore = viewCount;
        double engagementRateScore = getEngagementRate();
        double growthRateScore = getGrowthRate();

        return (viewScore * viewWeight) + (engagementRateScore * engagementRateWeight) + (growthRateScore * growthRateWeight);
    }
}


