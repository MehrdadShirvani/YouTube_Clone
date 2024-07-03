package Shared.Utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormats {
    public static String toRelativeTime(Date date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + " seconds ago";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minutes ago";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + " hours ago";
        } else if (seconds < 604800) {
            long days = seconds / 86400;
            return days + " days ago";
        } else if (seconds < 2592000) {
            long weeks = seconds / 604800;
            return weeks + " weeks ago";
        } else if (seconds < 31536000) {
            long months = seconds / 2592000;
            return months + " months ago";
        } else {
            long years = seconds / 31536000;
            return years + " years ago";
        }
    }
    public static String formatTimestamp(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        LocalDateTime date = timestamp.toLocalDateTime();
        return date.format(formatter);
    }
}
