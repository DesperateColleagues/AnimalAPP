package it.uniba.dib.sms22235.entities.operations;

import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.User;

public class Purchase {
    private User owner;
    private Animal animal;
    private String itemName;
    private String date;
    private float cost;
    private int amount;

    public Purchase() {}

    public Purchase(User owner, Animal animal, String itemName, String date, float cost, int amount) {
        this.owner = owner;
        this.animal = animal;
        this.itemName = itemName;
        this.cost = cost;
        this.amount = amount;
        this.date = date;
    }

    public User getOwner() {
        return owner;
    }

    public Animal getAnimal() {
        return animal;
    }

    public String getItemName() {
        return itemName;
    }

    public float getCost() {
        return cost;
    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
