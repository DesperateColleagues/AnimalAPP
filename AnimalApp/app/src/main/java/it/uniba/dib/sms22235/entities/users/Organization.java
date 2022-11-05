package it.uniba.dib.sms22235.entities.users;

import it.uniba.dib.sms22235.entities.users.Owner;

public class Organization implements Owner {
    private String organizationName;

    public Organization() {}

    public Organization(String organizationName) {
        this.organizationName = organizationName;

    }

    @Override
    public String OwnerName() {
        return organizationName;
    }
}
