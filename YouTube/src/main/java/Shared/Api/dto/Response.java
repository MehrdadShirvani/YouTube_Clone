package Shared.Api.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Response {
    private Header header;
    private Body body;

    public Response(Header header , Body body) {
        this.header = header;
        this.body = body;
    }


    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }
}
