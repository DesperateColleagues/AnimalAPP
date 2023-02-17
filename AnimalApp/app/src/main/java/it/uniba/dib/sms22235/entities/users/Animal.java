package it.uniba.dib.sms22235.entities.users;


import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Objects;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * A class that represent an animal
 * */
public class Animal implements Serializable, Cloneable {
    private String name;
    private String animalSpecies;
    private String race;
    private String microchipCode;
    private String birthDate;
    private String owner;
    private String veterinarian;
    private String nature;

    private double weight;
    private double height;

    public Animal(){}

    /**
     * @param name the name of the animal
     * @param animalSpecies the specie of the animal
     * @param race the race of the animal
     * @param microchipCode the microchip of the animal
     * @param birthDate the birth date of the animal
     * */
    public Animal(String name, String animalSpecies, String race, String microchipCode,
                  String birthDate) {
        this.name = name;
        this.race = race;
        this.animalSpecies = animalSpecies;
        this.microchipCode = microchipCode;
        this.birthDate = birthDate;
    }

    public String getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(String veterinarian) {
        this.veterinarian = veterinarian;
    }

    public String getNature() {
        return nature;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * This method is used to create an animal object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with animal data
     * @return the new instance of the animal
     * */
    @NonNull
    public static Animal loadAnimal (@NonNull DocumentSnapshot document) {
        Animal animal = new Animal(
                (String) document.get(KeysNamesUtils.AnimalFields.NAME),
                (String) document.get(KeysNamesUtils.AnimalFields.ANIMAL_SPECIES),
                (String) document.get(KeysNamesUtils.AnimalFields.RACE),
                (String) document.get(KeysNamesUtils.AnimalFields.MICROCHIP_CODE),
                (String) document.get(KeysNamesUtils.AnimalFields.BIRTH_DATE));

        animal.setVeterinarian((String) document.get(KeysNamesUtils.AnimalFields.VETERINARIAN));
        animal.setOwner((String) document.get(KeysNamesUtils.AnimalFields.OWNER));

        Double height = document.getDouble(KeysNamesUtils.AnimalFields.HEIGHT);
        Double weight = document.getDouble(KeysNamesUtils.AnimalFields.WEIGHT);
        String nature = document.getString(KeysNamesUtils.AnimalFields.NATURE);

        if (height != null) {
            animal.setHeight(height);
        }

        if (weight != null) {
            animal.setWeight(weight);
        }

        if (nature != null) {
            animal.setNature(nature);
        }

        return animal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return microchipCode.equals(animal.microchipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(microchipCode);
    }

    @NonNull
    @Override
    public String toString() {
        return name + " - " + microchipCode;
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
