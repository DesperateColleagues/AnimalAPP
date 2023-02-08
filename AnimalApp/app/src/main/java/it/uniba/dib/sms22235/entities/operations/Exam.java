package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Exam implements Serializable {

    private String id;
    private String animal;
    private String type;
    private String description;
    private String outcome; //result
    private String dateAdded;
    private String timeAdded;

    public Exam(){
        this.id = UUID.randomUUID().toString();
    }

    public Exam(String id, String animal, String type, String description) {
        this.id = id;
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

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    public static Exam loadExam(@NonNull DocumentSnapshot document) {
        Exam exam = new Exam(
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_ID),
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_ANIMAL),
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_TYPE),
                (String) document.get(KeysNamesUtils.ExamsFields.EXAM_DESCRIPTION)
        );
        exam.setOutcome((String) document.get(KeysNamesUtils.ExamsFields.EXAM_OUTCOME));
        exam.setDateAdded((String) document.get(KeysNamesUtils.ExamsFields.DATE_ADDED));
        exam.setTimeAdded((String) document.get(KeysNamesUtils.ExamsFields.TIME_ADDED));
        return exam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return id.equals(exam.id) && animal.equals(exam.animal) && type.equals(exam.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, animal, type);
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id='" + id + '\'' +
                ", animal='" + animal + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", outcome='" + outcome + '\'' +
                ", dateAdded='" + dateAdded + '\'' +
                ", timeAdded='" + timeAdded + '\'' +
                '}';
    }
}
