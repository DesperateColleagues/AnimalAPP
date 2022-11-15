package it.uniba.dib.sms22235.entities.users;

import com.google.firebase.firestore.DocumentSnapshot;

public class Organization implements Owner {
    private String name;
    private String purpose;
    private String phoneNumber;
    private String email;

    public Organization() {}

    public Organization(String name, String purpose, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.purpose = purpose;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String OwnerName() {
        return name;
    }

    // need this method because of FireBase
    public String getName() {
        return name;
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

    public static Organization loadOrganization(DocumentSnapshot document){
        return new Organization(
                (String) document.get("name"),
                (String) document.get("purpose"),
                (String) document.get("email"),
                (String) document.get("phoneNumber")
        );
    }
}
