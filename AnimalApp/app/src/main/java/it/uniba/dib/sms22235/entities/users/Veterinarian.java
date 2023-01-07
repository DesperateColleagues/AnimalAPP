package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Veterinarian extends AbstractPersonUser implements  Serializable, Cloneable {
    private String clinicName, phoneNumber;

    public Veterinarian() {}

    @Override
    public String toString() {
        return fullName + " - " + clinicName  + "\n" + email + " - " + phoneNumber;
    }

    /**
     * @param fullName       name of the person
     * @param email      email of the person used to perform operations like reset password
     * @param clinicName the name of the clinic
     * @param phoneNumber the phone number of the veterinary used as contact
     */
    public Veterinarian(String fullName, String email, String clinicName, String phoneNumber, String password, String purpose) {
        super(fullName, email, password, purpose);
        this.clinicName = clinicName;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String getFullName() {
        return super.getFullName();
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
                (String) document.get(KeysNamesUtils.ActorFields.EMAIL),
                (String) document.get(KeysNamesUtils.ActorFields.CLINIC_NAME),
                (String) document.get(KeysNamesUtils.ActorFields.PHONE_NUMBER),
                (String) document.get(KeysNamesUtils.ActorFields.PASSWORD),
                (String) document.get(KeysNamesUtils.ActorFields.PURPOSE)
        );
    }


    @NonNull
    @Override
    public Object clone() {
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        return o;
    }
}
