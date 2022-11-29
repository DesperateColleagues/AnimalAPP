package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Veterinarian extends AbstractPersonUser implements Owner, Serializable {
    private String clinicName, phoneNumber;

    public Veterinarian() {}

    /**
     * @param fullName       name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param clinicName the name of the clinic
     * @param phoneNumber the phone number of the veterinary used as contact
     */
    public Veterinarian(String fullName, String email, String clinicName, String phoneNumber) {
        super(fullName, email);
        this.clinicName = clinicName;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String OwnerName() {
        return fullName;
    }

    public String getClinicName() {
        return clinicName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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
    public static Veterinarian loadVeterinarian(@NonNull DocumentSnapshot document){
        return new Veterinarian(
                (String) document.get(KeysNamesUtils.ActorFields.FULL_NAME),
                (String) document.get(KeysNamesUtils.ActorFields.FULL_NAME),
                (String) document.get(KeysNamesUtils.ActorFields.CLINIC_NAME),
                (String) document.get(KeysNamesUtils.ActorFields.PHONE_NUMBER)
        );
    }
}
