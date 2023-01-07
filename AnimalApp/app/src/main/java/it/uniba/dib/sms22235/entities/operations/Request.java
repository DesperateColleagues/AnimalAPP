package it.uniba.dib.sms22235.entities.operations;

import java.io.Serializable;

public class Request implements Serializable {

    private String id;
    private String requestTitle;
    private String requestBody;
    private String type;

    public Request(String id, String requestTitle, String requestBody, String type) {
        this.id = id;
        this.requestTitle = requestTitle;
        this.requestBody = requestBody;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
