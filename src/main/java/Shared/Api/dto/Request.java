package Shared.Api.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Request {
    private Header header;
    private Body body;

    public Request(Header header , Body body) {
        this.header = header;
        this.body = body;
    }

    public Request() {

    }


    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
