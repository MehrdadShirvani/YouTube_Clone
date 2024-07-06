package Shared.Models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.sql.Timestamp;

public class VideoDeserializer extends JsonDeserializer<Video> {

    @Override
    public Video deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Long videoId = node.has("videoId") ? node.get("videoId").asLong() : null;
        String name = node.has("name") ? node.get("name").asText() : null;
        String description = node.has("description") ? node.get("description").asText() : null;
        String thumbnail = node.has("thumbnail") ? node.get("thumbnail").asText() : null;
        Long channelId = node.has("channelId") ? node.get("channelId").asLong() : null;
        Timestamp createdDateTime = node.has("createdDateTime") ? Timestamp.valueOf(node.get("createdDateTime").asText()) : null;
        Boolean isPrivate = node.has("isPrivate") ? node.get("isPrivate").asBoolean() : null;
        Boolean isAgeRestricted = node.has("isAgeRestricted") ? node.get("isAgeRestricted").asBoolean() : null;
        Integer duration = node.has("duration") ? node.get("duration").asInt() : null;
        Integer videoTypeId = node.has("videoTypeId") ? node.get("videoTypeId").asInt() : null;

        Video video = new Video();
        video.setVideoId(videoId);
        video.setName(name);
        video.setDescription(description);
        video.setThumbnail(thumbnail);
        video.setChannelId(channelId);
        video.setCreatedDateTime(createdDateTime);
        video.setPrivate(isPrivate);
        video.setAgeRestricted(isAgeRestricted);
        video.setDuration(duration);
        video.setVideoTypeId(videoTypeId);

        return video;
    }
}
