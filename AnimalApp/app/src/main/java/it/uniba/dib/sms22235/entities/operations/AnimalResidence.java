package it.uniba.dib.sms22235.entities.operations;

import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Owner;

public class AnimalResidence {
    private String animal;
    private String startDate;
    private String endDate;
    private String residenceOwner;
    boolean isTemp;

    public AnimalResidence(String startDate, String endDate, String residenceOwner, boolean isTemp) {
        this.startDate = startDate;
        this.residenceOwner = residenceOwner;
        this.endDate = endDate;
        this.isTemp = isTemp;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getResidenceOwner() {
        return residenceOwner;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }
}
