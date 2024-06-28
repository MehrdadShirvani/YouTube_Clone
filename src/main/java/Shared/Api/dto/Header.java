package Shared.Api.dto;

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
}
