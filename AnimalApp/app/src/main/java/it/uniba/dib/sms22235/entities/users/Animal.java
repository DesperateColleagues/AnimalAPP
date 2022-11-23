package it.uniba.dib.sms22235.entities.users;

public class Animal {
    private String name;
    private String animalSpecies;
    private String race;
    private String microchipCode;
    private String birthDate;

    public Animal(){}

    public Animal(String name, String animalSpecies, String race, String microchipCode, String birthDate) {
        this.name = name;
        this.race = race;
        this.animalSpecies = animalSpecies;
        this.microchipCode = microchipCode;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public String getAnimalSpecies() {
        return animalSpecies;
    }

    public String getRace() {
        return race;
    }

    public String getMicrochipCode() {
        return microchipCode;
    }

    public String getBirthDate() {
        return birthDate;
    }
}
