package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

public class Veterinary extends AbstractPersonUser implements Owner {
    private String clinicName, phonNumber;

    public Veterinary() {}

    /**
     * @param name       name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param clinicName the name of the clinic
     * @param phonNumber the phone number of the veterinary used as contact
     */
    public Veterinary(String name, String email, String clinicName, String phonNumber) {
        super(name, email);
        this.clinicName = clinicName;
        this.phonNumber = phonNumber;
    }

    @Override
    public String OwnerName() {
        return fullName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getPhonNumber() {
        return phonNumber;
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
                (String) document.get("email"),
                (String) document.get("clinicName"),
                (String) document.get("phoneNumber")
        );
    }
}
