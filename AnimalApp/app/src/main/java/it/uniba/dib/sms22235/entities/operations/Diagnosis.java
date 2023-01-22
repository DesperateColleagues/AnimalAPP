package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Diagnosis implements Serializable {

    private String id;
    private String description;
    private String path;
    private String animal;

    public Diagnosis(String description, String path) {
        this.description = description;
    }

    public Diagnosis(String id, String description, String path, String animal) {
        this.id = id;
        this.description = description;
        this.path = path;
        this.animal = animal;
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

    public String getAnimal() { return animal; }

    public void setAnimal(String animal) { this.animal = animal; }

    /**
     * This method is used to create a diagnosis object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with diagnosis data
     * @return the new instance of the diagnosis
     * */
    @NonNull
    public static Diagnosis loadDiagnosis (@NonNull DocumentSnapshot document) {
        Diagnosis diagnosis = new Diagnosis(
                (String) document.get(KeysNamesUtils.DiagnosisFields.ID),
                (String) document.get(KeysNamesUtils.DiagnosisFields.DESCRIPTION),
                (String) document.get(KeysNamesUtils.DiagnosisFields.PATH),
                (String) document.get(KeysNamesUtils.DiagnosisFields.ANIMAL)
        );

        return diagnosis;
    }
}
