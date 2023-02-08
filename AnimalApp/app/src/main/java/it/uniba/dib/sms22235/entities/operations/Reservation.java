package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Reservation implements Serializable, Cloneable {

    private String owner;
    private String animal;
    private String veterinarian;
    private String date;
    private String time;

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnimal() {
        return animal;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Reservation(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public Reservation(String date, String time, String veterinarian, String animal, String owner) {
        this.date = date;
        this.time = time;
        this.veterinarian = veterinarian;
        this.animal = animal;
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setVeterinarian(String veterinarian) { this.veterinarian = veterinarian; }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getDate() {
        return date;
    }

    public String getTime() { return time; }

    public String getVeterinarian() { return veterinarian; }

    @Override
    public String toString() {
        return "" + owner + " " + veterinarian + " " + date + " " + time;
    }

    /**
     * This method is used to create a reservation object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with reservation data
     * @return the new instance of the reservation
     * */
    @NonNull
    public static Reservation loadReservation (@NonNull DocumentSnapshot document) {
        return new Reservation(
                (String) document.get(KeysNamesUtils.ReservationFields.DATE),
                (String) document.get(KeysNamesUtils.ReservationFields.TIME),
                (String) document.get(KeysNamesUtils.ReservationFields.VETERINARIAN),
                (String) document.get(KeysNamesUtils.ReservationFields.ANIMAL),
                (String) document.get(KeysNamesUtils.ReservationFields.OWNER));
    }

    @NonNull
    @Override
    public Object clone() {
        Object o;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return o;
    }
}
