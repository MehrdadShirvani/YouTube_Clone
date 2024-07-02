package Shared.Api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
        String endpointPattern = ".*/api/videos/search.*";
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
}
