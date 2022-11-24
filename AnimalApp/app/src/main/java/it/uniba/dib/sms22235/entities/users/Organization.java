package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Organization implements Owner {
    private String orgName;
    private String purpose;
    private String phoneNumber;
    private String email;

    public Organization() {}

    public Organization(String name, String purpose, String email, String phoneNumber) {
        this.orgName = name;
        this.email = email;
        this.purpose = purpose;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String OwnerName() {
        return orgName;
    }

    // need this method because of FireBase
    public String getOrgName() {
        return orgName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getEmail() {
        return email;
    }

    /**
     * This method is used to create an organization object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with organization data
     * @return the new instance of the organization
     * */
    @NonNull
    @Contract("_ -> new")
    public static Organization loadOrganization(@NonNull DocumentSnapshot document){
        return new Organization(
                (String) document.get(KeysNamesUtils.ActorFields.ORG_NAME),
                (String) document.get(KeysNamesUtils.ActorFields.PURPOSE),
                (String) document.get(KeysNamesUtils.ActorFields.EMAIL),
                (String) document.get(KeysNamesUtils.ActorFields.PHONE_NUMBER)
        );
    }
}
