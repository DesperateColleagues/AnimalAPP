package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.UUID;

public class Request implements Serializable {

    private String id;
    private String requestTitle;
    private String requestBody;
    private String operationType;
    private String requestType;
    private String userEmail;
    private boolean isCompleted;
    private String animal;

    public Request(String requestTitle, String requestBody, String operationType, String requestType) {
        this.requestTitle = requestTitle;
        this.requestBody = requestBody;
        this.operationType = operationType;
        this.requestType = requestType;
        this.isCompleted = false;
        id = UUID.randomUUID().toString();
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    @NonNull
    public static Request loadRequest(@NonNull DocumentSnapshot document) {
        Request request = new Request(
                (String) document.get("requestTitle"),
                (String) document.get("requestBody"),
                (String) document.get("operationType"),
                (String) document.get("requestType"));

        request.setUserEmail((String) document.get("userEmail"));
        request.setAnimal((String) document.get("animal"));
        request.setId((String) document.get("id"));
        request.setIsCompleted(Boolean.TRUE.equals(document.getBoolean("isCompleted")));

        return request;
    }


}
