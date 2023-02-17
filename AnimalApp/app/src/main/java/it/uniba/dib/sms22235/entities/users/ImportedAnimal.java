package it.uniba.dib.sms22235.entities.users;

/**
 * Animal imported from json
 * */
public class ImportedAnimal extends Animal {
    private String profilePicPath;
    private String picsFolder;

    public ImportedAnimal(String name, String animalSpecies, String race, String microchipCode, String birthDate, String profilePicPath, String picsFolder) {
        super(name, animalSpecies, race, microchipCode, birthDate);
        this.profilePicPath = profilePicPath;
        this.picsFolder = picsFolder;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }

    public void setProfilePicPath(String profilePicPath) {
        this.profilePicPath = profilePicPath;
    }

    public String getPicsFolder() {
        return picsFolder;
    }

    public void setPicsFolder(String picsFolder) {
        this.picsFolder = picsFolder;
    }

    public Animal getBaseAnimal() {
        Animal a = new Animal(super.getName(), super.getAnimalSpecies(), super.getRace(), super.getMicrochipCode(), super.getBirthDate());
        a.setOwner(super.getOwner());
        a.setHeight(super.getHeight());
        a.setWeight(super.getWeight());
        a.setNature(super.getNature());
        return a;
    }
}
