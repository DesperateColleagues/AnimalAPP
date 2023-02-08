package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class AnimalResidence {
    private String animal;
    private String startDate;
    private String endDate;
    private String residenceOwner;
    private String isTemp;

    public AnimalResidence(String startDate, String endDate, String residenceOwner, String isTemp) {
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

    public String isTemp() {
        return isTemp;
    }

    public void setTemp(String temp) {
        isTemp = temp;
    }

    public static AnimalResidence loadResidence(@NonNull DocumentSnapshot document) {
        AnimalResidence a = new AnimalResidence(
                (String) document.get(KeysNamesUtils.ResidenceFields.START_DATE),
                (String) document.get(KeysNamesUtils.ResidenceFields.END_DATE),
                (String) document.get(KeysNamesUtils.ResidenceFields.RESIDENCE_OWNER),
                (String) document.get(KeysNamesUtils.ResidenceFields.IS_TEMP)
        );
        a.setAnimal((String) document.get(KeysNamesUtils.ResidenceFields.ANIMAL));
        return a;
    }
}
