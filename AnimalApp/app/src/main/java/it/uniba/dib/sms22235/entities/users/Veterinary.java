package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

public class Veterinary extends AbstractPersonUser implements Owner {
    private String clinicName;
    private String CF;

    public Veterinary() {}

    /**
     * @param name       name of the person
     * @param secondName second name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param birthDate  birthday date
     * @param clinicName the name of the clinic
     * @param CF fiscal code
     */
    public Veterinary(String name, String secondName, String email, String birthDate,
                      String clinicName, String CF) {
        super(name, secondName, email, birthDate);
        this.clinicName = clinicName;
        this.CF = CF;
    }

    @Override
    public String OwnerName() {
        return name + " " + secondName + " " + CF;
    }

    public String getCF() {
        return CF;
    }

    public String getClinicName() {
        return clinicName;
    }

    /**
     * This method is used to create a veterinary object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with user data
     * @return the new instance of the user
     * */
    @NonNull
    @Contract("_ -> new")
    public static Veterinary loadVeterinary(@NonNull DocumentSnapshot document){
        return new Veterinary(
                (String) document.get("name"),
                (String) document.get("secondName"),
                (String) document.get("email"),
                (String) document.get("birthDate"),
                (String) document.get("clinicName"),
                (String) document.get("CF")
        );
    }
}
