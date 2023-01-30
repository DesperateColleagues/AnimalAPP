package it.uniba.dib.sms22235.entities.users;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.Contract;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Organization extends AbstractPersonUser implements InfoEntity {
    private final String orgName;
    private final String phoneNumber;
    private final String orgAddress;

    public Organization(String orgName, String email, String phoneNumber, String password,
                        String purpose, String orgAddress) {
        super(null, email, password, purpose);
        this.orgName = orgName;
        this.phoneNumber = phoneNumber;
        this.orgAddress = orgAddress;
    }

    // need this method because of FireBase
    public String getOrgName() {
        return orgName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPurpose() {
        return super.purpose;
    }

    public String getEmail() {
        return email;
    }

    public String getOrgAddress() {
        return orgAddress;
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
                (String) document.get(KeysNamesUtils.ActorFields.EMAIL),
                (String) document.get(KeysNamesUtils.ActorFields.PHONE_NUMBER),
                (String) document.get(KeysNamesUtils.ActorFields.PASSWORD),
                (String) document.get(KeysNamesUtils.ActorFields.PURPOSE),
                (String) document.get(KeysNamesUtils.ActorFields.ORG_ADDRESS)
        );
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
