package Server;

import Server.Database.DatabaseManager;
import Shared.Models.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Recommendation {
    private final Long channelId;
    int perPage;
    int pageNumber;

    public Recommendation(Long channelId , int perPage , int pageNumber) {
        this.channelId = channelId;
        this.perPage = perPage;
        this.pageNumber = pageNumber;
    }


    public List<Video> recommend() throws Exception {
        HashMap<Video , Double> scoredVideos = new HashMap<>();

        Double trendingWeight = 0.3;
        Double subbedWeight = 0.2;
        Double interestingWeight = 0.4;
        Double dateOfPublishWeight = -0.1;

        HashMap<Category , Double> interestedCategories = getInterestedCategories();
        HashMap<Video , Double> trendedVideos = getTrendedVideos();
        HashMap<Video , Long> isSubbed = new HashMap<>();
        HashMap<Video , Long> elapsedHoursFromPublish = new HashMap<>();

        List<Long> subscribedChannelIds = DatabaseManager.getSubscribedChannels(this.channelId).stream()
                .map(Channel::getChannelId).toList();
        for (Entry<Video , Double> videoSet : trendedVideos.entrySet()) {
             isSubbed.put(videoSet.getKey() , (subscribedChannelIds.contains(videoSet.getKey().getChannelId()) ? 1L : 0L));
             elapsedHoursFromPublish.put(videoSet.getKey() , getHoursPassed(videoSet.getKey().getCreatedDateTime()));
        }

        HashMap<Video, Double> normalizedSubbedRate = dataConversion(isSubbed , 1.0);
        HashMap<Video , Double> normalizedTrendedVideos = dataConversion(trendedVideos , 1.0);
        HashMap<Video , Double> normalizedElapsedHours = dataConversion(elapsedHoursFromPublish , 1.0);

        for (Entry<Video , Double> trendVideoSet : normalizedTrendedVideos.entrySet()) {
            Video keyVideo = trendVideoSet.getKey();
            Double trendingRate = trendVideoSet.getValue();
            Double subbedRate = normalizedSubbedRate.getOrDefault(keyVideo , 0.0);
            Double elapsedHoursRate = normalizedElapsedHours.getOrDefault(keyVideo , 0.0);
            Double interestingRate = 0.0;

            if (keyVideo.getPrivate() == false) {
                List<Category> categories = DatabaseManager.getCategoriesOfVideo(keyVideo.getVideoId());
                for (Category category : categories) {
                    interestingRate += interestedCategories.getOrDefault(category , 0.0);

                }

                Double score = trendingRate * trendingWeight + subbedRate * subbedWeight + interestingRate * interestingWeight + elapsedHoursRate * dateOfPublishWeight;

                scoredVideos.put(keyVideo , score);
            }
        }

        List<Entry<Video , Double>> scoredVideosEntry = new ArrayList<>(scoredVideos.entrySet());
        scoredVideosEntry.sort(Entry.comparingByValue());
        List<Video> recommendedVideosSorted = scoredVideosEntry.reversed().stream().map(Entry::getKey).toList();

        List<Video> paginatedVideos = recommendedVideosSorted.stream().skip((long) (pageNumber - 1) * perPage).limit(perPage).toList();

        return paginatedVideos;
    }


    private HashMap<Category , Double> getInterestedCategories() {
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

            for (Entry<Category, Double> entry : normalizedAllTimeValue.entrySet()) {
                Category category = entry.getKey();
                double sum = entry.getValue() + normalizedDailyValue.getOrDefault(category , 0.0) + normalizedWeeklyValue.getOrDefault(category , 0.0) + normalizedMonthlyValue.getOrDefault(category , 0.0);
                result.put(entry.getKey() , sum);
            }

            return result;

        } catch (Exception e) {
            String errorLog = "Error : while converting data in getInterestedCategories !";
            System.err.println(errorLog);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private HashMap<Video , Double> getTrendedVideos() {
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

        return trendingRate;
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


    private <T, N extends Number> HashMap<T, Double> dataConversion(HashMap<T, N> data, double percentage) throws Exception {
        double sumOfValues = data.values().stream()
                .mapToDouble(Number::doubleValue)
                .sum();

        double sum = sumOfValues == 0.0 ? 1.0 : sumOfValues;

        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> (entry.getValue().doubleValue() / sum) * percentage,
                        (e1, e2) -> e1,
                        HashMap::new
                ));
    }

    private Date calculateStartDate(Date endDate, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    public Long getHoursPassed(Timestamp pastTimestamp) {
        Instant pastInstant = pastTimestamp.toInstant();
        Instant nowInstant = Instant.now();
        Duration duration = Duration.between(pastInstant, nowInstant);
        return duration.toHours();
    }
}
