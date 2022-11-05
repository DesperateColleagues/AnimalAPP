package it.uniba.dib.sms22235.entities.users;

public class Animal {
    private String name;
    private String race;
    private String diet;
    private String microchipCode;
    private Integer age;

    public Animal(){}

    public Animal(String name, String race, String diet, String microchipCode, Integer age) {
        this.name = name;
        this.race = race;
        this.diet = diet;
        this.microchipCode = microchipCode;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getRace() {
        return race;
    }

    public String getDiet() {
        return diet;
    }

    public String getMicrochipCode() {
        return microchipCode;
    }

    public Integer getAge() {
        return age;
    }
}
