package Shared.Api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {
    private String method;
    private String endpoint;

    public Header(String method , String endpoint) {
        this.method = method;
        this.endpoint = endpoint;
    }

    public Header() {

    }
    public void setMethod(String method) {
        this.method = method;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String[] endpointParser() {
        return this.endpoint.split("/");
    }

    public boolean isValidSearchQuery() {
        String endpointPattern = ".*/api/video/search.*";
        return this.endpoint.matches(endpointPattern);
    }

    public boolean isValidAccountInfoQuery() {
        String endpointPattern = "^/api/account/\\d+$";
        return this.endpoint.matches(endpointPattern);
    }

    public boolean isValidChannelInfoQuery() {
        String endpointPattern = "^/api/channel/\\d+$";
        return this.endpoint.matches(endpointPattern);
    }

    public boolean isValidSubscribersQuery() {
        String endpointPattern = "^/api/channel/subscribers/\\d+$";
        return this.endpoint.matches(endpointPattern);
    }

    public boolean isValidSubscribedToChannelQuery() {
        String endpointPattern = "^/api/account/is-subscribed\\?channelId=\\d+$";
        return this.endpoint.matches(endpointPattern);
    }

    public boolean isValidVideoLikedQuery() {
        String endpointPattern = "^/api/video/is-liked\\?channelId=\\d+$";
        return this.endpoint.matches(endpointPattern);
    }


    public boolean isValidCommentLikedQuery() {
        String endpointPattern = "^/api/comment/is-liked\\?channelId=\\d+$";
        return this.endpoint.matches(endpointPattern);
    }

    public String parseSearchKeywords() throws UnsupportedEncodingException {
        String regex = "query" + "=([^&]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);
        String searchKeywords;

        if (matcher.find()) {
            searchKeywords = URLDecoder.decode(matcher.group(1), "UTF-8");
            return searchKeywords;
        }

        return null;
    }

    public Long parseAccountId() {
        String regex = "^/api/account/(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);

        if (matcher.matches()) {
            String numberAsStr = matcher.group(1);

            try {
                Long numberAsInt = Long.parseLong(numberAsStr);
                return numberAsInt;

            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Long parseChannelId() {
        String regex = "^/api/channel/(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);

        if (matcher.matches()) {
            String idAsStr = matcher.group(1);

            try {
                Long idAsInt = Long.parseLong(idAsStr);
                return idAsInt;

            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Long parseChannelIdInSubscribers() {
        String regex = "^/api/channel/subscribers/(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);

        if (matcher.matches()) {
            String idAsStr = matcher.group(1);

            try {
                Long idAsInt = Long.parseLong(idAsStr);
                return idAsInt;

            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Long parseIsSubscribedChannelId() {
        String regex = "^/api/account/is-subscribed\\?channelId=(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);

        if (matcher.matches()) {
            String idAsStr = matcher.group(1);

            try {
                Long idAsInt = Long.parseLong(idAsStr);
                return idAsInt;

            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Long parseVideoLikedChannelId() {
        String regex = "^/api/video/is-liked\\?channelId=(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);

        if (matcher.matches()) {
            String idAsStr = matcher.group(1);

            try {
                Long idAsInt = Long.parseLong(idAsStr);
                return idAsInt;

            } catch (Exception ignored) {
            }
        }
        return null;
    }


    public Long parseCommentLikedChannelId() {
        String regex = "^/api/comment/is-liked\\?channelId=(\\d+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.endpoint);

        if (matcher.matches()) {
            String idAsStr = matcher.group(1);

            try {
                Long idAsInt = Long.parseLong(idAsStr);
                return idAsInt;

            } catch (Exception ignored) {
            }
        }
        return null;
    }


    public List<Long> extractIds() throws IllegalArgumentException {
        final String ENDPOINT_PATTERN = "^/api/(video|account|comment|playlist)/[0-9]+(/(categories|most-viewed-categories|delete|category/[0-9]+/delete|watch-history(/(video|timestamp))?|edit|videos|channels|video/[0-9]+/delete|video|channel/[0-9]+/delete))?$";
        final String ID_REGEX = "\\b\\d+\\b";
        Pattern idPattern = Pattern.compile(ID_REGEX);

        if (!this.endpoint.matches(ENDPOINT_PATTERN)) {
            throw new IllegalArgumentException("Invalid endpoint format: " + this.endpoint);
        }

        List<Long> ids = new ArrayList<>();
        Matcher matcher = idPattern.matcher(this.endpoint);

        while (matcher.find()) {
            String idAsStr = matcher.group();


            try {
                Long idAsLong = Long.parseLong(idAsStr);
                ids.add(idAsLong);

            } catch (Exception e) {
                System.out.println("An error while converting id to Long");
            }
        }

        return ids;
    }
}
