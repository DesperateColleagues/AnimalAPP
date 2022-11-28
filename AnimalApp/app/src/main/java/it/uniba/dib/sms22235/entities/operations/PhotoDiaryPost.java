package it.uniba.dib.sms22235.entities.operations;

import java.io.Serializable;
import java.util.Objects;

public class PhotoDiaryPost implements Serializable {
    private String dirName;
    private String fileName;
    private String title;
    private String description;

    public PhotoDiaryPost(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getDirName() {
        return dirName;
    }

    public String getFileName(){
        return  fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhotoDiaryPost that = (PhotoDiaryPost) o;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
