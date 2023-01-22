package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Exam implements Serializable {

    private String id;
    private String animal;
    private String type;
    private String description;
    private String outcome; //result

    public Exam(String animal, String type, String description) {
        this.animal = animal;
        this.type = type;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public static Exam loadExam(@NonNull DocumentSnapshot document) {
        Exam exam = new Exam(
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_ANIMAL),
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_TYPE),
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_DESCRIPTION)
        );
        exam.setOutcome((String) document.get(KeysNamesUtils.ExamsFields.EXAM_OUTCOME));

        return exam;
    }
}
