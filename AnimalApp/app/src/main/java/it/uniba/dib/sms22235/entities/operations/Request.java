package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.UUID;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * THis class represent a request
 * */
public class Request implements Serializable {

    private String id;
    private String requestTitle;
    private String requestBody;
    private String requestType;
    private String userEmail;
    private boolean isCompleted;
    private String animal;

    public Request(String requestTitle, String requestBody, String requestType) {
        this.requestTitle = requestTitle;
        this.requestBody = requestBody;
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
                (String) document.get(KeysNamesUtils.RequestFields.REQUEST_TITLE),
                (String) document.get(KeysNamesUtils.RequestFields.REQUEST_BODY),
                (String) document.get(KeysNamesUtils.RequestFields.REQUEST_TYPE));

        request.setUserEmail((String) document.get(KeysNamesUtils.RequestFields.USER_EMAIL));
        request.setAnimal((String) document.get(KeysNamesUtils.RequestFields.REQUEST_ANIMAL));
        request.setId((String) document.get(KeysNamesUtils.RequestFields.REQUEST_ID));
        request.setIsCompleted(Boolean.TRUE.equals(document.getBoolean(KeysNamesUtils.RequestFields.REQUEST_COMPLETED)));

        return request;
    }


}
