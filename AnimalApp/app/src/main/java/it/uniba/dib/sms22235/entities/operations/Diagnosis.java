package it.uniba.dib.sms22235.entities.operations;

import java.io.Serializable;

public class Diagnosis implements Serializable {

    private String id;
    private String description;
    private String path;

    public Diagnosis(String description, String path) {
        this.description = description;
    }

    public Diagnosis(String id, String description, String path) {
        this.id = id;
        this.description = description;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
