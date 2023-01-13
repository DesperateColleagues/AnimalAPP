package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PhotoDiaryPost implements Serializable {
    private String postUri;
    private final String postAnimal;
    private String fileName;

    public PhotoDiaryPost(String postUri, String postAnimal) {
        this.postUri = postUri;
        this.postAnimal = postAnimal;
    }

    public void setPostUri(String postUri) {
        this.postUri = postUri;
    }

    public String getPostUri() {
        return postUri;
    }

    public String getPostAnimal() {
        return postAnimal;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @NonNull
    @Contract("_ -> new")
    public static PhotoDiaryPost loadPhotoDiaryPost(@NonNull DocumentSnapshot document) {
        PhotoDiaryPost post =  new PhotoDiaryPost(
                document.getString(KeysNamesUtils.PhotoDiaryFields.POST_URI),
                document.getString(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL)
        );

        post.setFileName(document.getString(KeysNamesUtils.PhotoDiaryFields.FILE_NAME));

        return post;
    }
}
