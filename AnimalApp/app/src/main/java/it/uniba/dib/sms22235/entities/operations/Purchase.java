package it.uniba.dib.sms22235.entities.operations;

import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;

public class Purchase {
    private String owner;
    private String animal;
    private String itemName;
    private String date;
    private float cost;
    private int amount;

    public Purchase() {}

    public Purchase(String animal, String itemName, String date, float cost, int amount) {
        this.animal = animal;
        this.itemName = itemName;
        this.cost = cost;
        this.amount = amount;
        this.date = date;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public String getAnimal() {
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
