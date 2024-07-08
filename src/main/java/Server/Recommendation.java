package Server;

import Server.Database.DatabaseManager;
import Shared.Models.Category;
import Shared.Models.Comment;
import Shared.Models.Reaction;
import Shared.Models.Video;

import java.net.SecureCacheResponse;
import java.util.*;

public class Recommendation {
    private Long channelId;

    public Recommendation(Long channelId) {
        this.channelId = channelId;
    }


    public HashMap<Category , Double> interestedCategories() {
        try {
            Date endDate = new Date();

            Date startDate24HoursAgo = calculateStartDate(endDate, Calendar.HOUR, -24);
            HashMap<Category , Long> daily = DatabaseManager.dataAnalysis(this.channelId , startDate24HoursAgo , endDate);
            HashMap<Category , Double> normalizedDailyValue = dataConversion(daily , 0.5);

            Date startDate7DaysAgo = calculateStartDate(endDate, Calendar.DAY_OF_MONTH, -7);
            HashMap<Category , Long> weekly = DatabaseManager.dataAnalysis(this.channelId , startDate7DaysAgo , endDate);
            HashMap<Category , Double> normalizedWeeklyValue = dataConversion(weekly , 0.2);

            Date startDate1MonthAgo = calculateStartDate(endDate, Calendar.MONTH, -1);
            HashMap<Category , Long> monthly = DatabaseManager.dataAnalysis(this.channelId , startDate1MonthAgo , endDate);
            HashMap<Category, Double> normalizedMonthlyValue = dataConversion(monthly , 0.2);

            HashMap<Category , Long> allTime = DatabaseManager.dataAnalysis(this.channelId , null , null);
            HashMap<Category , Double> normalizedAllTimeValue = dataConversion(allTime, 0.1);

            HashMap<Category , Double> result = new HashMap<>();

            for (Map.Entry<Category, Double> entry : normalizedAllTimeValue.entrySet()) {
                Category category = entry.getKey();
                double sum = entry.getValue() + normalizedDailyValue.get(category) + normalizedWeeklyValue.get(category) + normalizedMonthlyValue.get(category);
                result.put(entry.getKey() , sum);
            }

            return result;

        } catch (Exception e) {
            String errorLog = "Error : while converting data in videoSuggest !";
            System.err.println(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private List<Video> trendedVideos() {
        List<Video> videos = DatabaseManager.searchVideo(this.channelId , null , "" , 40 , 1);
        HashMap<Video , Double> trendingRate = new HashMap<>();

        for (Video video : videos) {
            Long videoId = video.getVideoId();
            Long viewCount = DatabaseManager.getNumberOfViews(videoId);
            List<Reaction> videoReactions = DatabaseManager.getVideoReactions(videoId);
            List<Comment> comments = DatabaseManager.getVideoComments(videoId);
            Long numberOfLikes = (videoReactions != null) ? videoReactions.stream().filter(videoReaction -> videoReaction.getReactionTypeId() == 1).count() : 0;
            Long numberOfDislikes = (videoReactions != null) ? videoReactions.stream().filter(videoReaction -> videoReaction.getReactionTypeId() == -1).count() : 0;
            Long numberOfComments = (comments != null) ? (long) comments.size() : 0;

            Double rating = calculateRating(numberOfLikes , numberOfDislikes , numberOfComments , viewCount);
            trendingRate.put(video , rating);
        }

        List<Map.Entry<Video , Double>> trendingRateSet = new ArrayList<>(trendingRate.entrySet());
        Collections.sort(trendingRateSet , Map.Entry.comparingByValue());
        List<Video> result = trendingRateSet.reversed().stream().map(Map.Entry::getKey).toList();

        return result;
    }


    private double getEngagementRate(Long numberOfLikes , Long numberOfDislikes , Long numberOfComments , Long viewCount) {
        Long totalInteractions = numberOfLikes + numberOfDislikes + numberOfComments;
        return (double) totalInteractions / viewCount;
    }

    private double calculateRating(Long numberOfLikes , Long numberOfDislikes , Long numberOfComments , Long viewCount) {
        double viewWeight = 0.4;
        double engagementRateWeight = 0.6;

        double viewScore = viewCount;
        double engagementRateScore = getEngagementRate(numberOfLikes , numberOfDislikes , numberOfComments , viewCount);

        return (viewScore * viewWeight) + (engagementRateScore * engagementRateWeight);
    }


    private HashMap<Category , Double> dataConversion(HashMap<Category , Long>  data, double percentage) throws Exception {
        HashMap<Category , Double> result = new HashMap<>();
        Long sumOfValues = data.values().stream().mapToLong(i -> i).sum();

        for (Map.Entry<Category , Long> set : data.entrySet()) {
            result.put(set.getKey(), ((set.getValue() / (double) sumOfValues) * percentage));

        }
        return result;
    }

    private Date calculateStartDate(Date endDate, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(field, amount);
        return calendar.getTime();
    }
}
