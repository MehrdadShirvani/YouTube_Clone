package Api.dto;

public class Header {
    private final String method;
    private final String endpoint;

    public Header(String method , String endpoint) {
        this.method = method;
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
