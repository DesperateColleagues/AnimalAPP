package it.uniba.dib.sms22235.entities.operations;

import java.io.Serializable;

public class Reservation implements Serializable {

    private String owner;
    private String animal;
    private String veterinarian;
    private String date;
    private String time;
    private String diagnosis;

    public Reservation(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public void setVeterinarian(String veterinarian) { this.veterinarian = veterinarian; }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "" + owner + '\'' +
                "" + veterinarian + '\'' +
                "" + date + '\'' +
                "" + time + '\'' +
                '}';
    }
}
