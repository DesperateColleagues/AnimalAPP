package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Backbench {
    private String description = "";
    private String owner;
    private String downloadableImage = "";

    public Backbench(String owner) {
        this.owner = owner;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDownloadableImage(String downloadableImage) {
        this.downloadableImage = downloadableImage;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getDownloadableImage() {
        return downloadableImage;
    }

    @NonNull
    public static Backbench loadBackbench(@NonNull DocumentSnapshot document) {
        Backbench backbench = new Backbench((String) document.get(KeysNamesUtils.BackbenchFields.OWNER));
        backbench.setDescription((String) document.get(KeysNamesUtils.BackbenchFields.DESCRIPTION));
        backbench.setDownloadableImage((String) document.get(KeysNamesUtils.BackbenchFields.DOWNLOADABLE_IMAGE));

        return backbench;
    }

}
