package it.uniba.dib.sms22235.entities.users;

public class Veterinary extends AbstractPersonUser implements Owner {
    private String clinicName;
    private String CF;

    public Veterinary() {}

    /**
     * @param name       nome della persona
     * @param secondName cognome della persona
     * @param email      email della persona
     * @param birthDate  data di nascita della persona
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
}
