package it.uniba.dib.sms22235.entities.operations;

import java.io.Serializable;

public class PhotoDiaryPost implements Serializable {
    private String path;
    private String description;

    /**
     * @param path the path where the image of the post is saved
     * @param description the description of the post
     * */
    public PhotoDiaryPost(String path, String description) {
        this.path = path;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getPath() {
        return path;
    }
}
