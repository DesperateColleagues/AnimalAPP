package it.uniba.dib.sms22235.entities.users;

public class ImportedAnimal extends Animal {
    private String profilePicPath;
    private String picsFolder;

    public ImportedAnimal(String name, String animalSpecies, String race, String microchipCode, String birthDate, String profilePicPath, String picsFolder) {
        super(name, animalSpecies, race, microchipCode, birthDate);
        this.profilePicPath = profilePicPath;
        this.picsFolder = picsFolder;
    }
}
