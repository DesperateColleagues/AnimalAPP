package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Objects;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class Purchase implements Serializable, Cloneable {
    private String owner;
    private String animal;
    private String itemName;
    private String date;
    private String category;
    private float cost;
    private int amount;

    public Purchase() {}

    public Purchase(String animal, String itemName, String date, String category, float cost, int amount) {
        this.animal = animal;
        this.itemName = itemName;
        this.category = category;
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

    public String getCategory() {
        return category;
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

    @NonNull
    public static Purchase loadPurchase(@NonNull DocumentSnapshot document) {
        Purchase purchase = new Purchase(
                (String) document.get(KeysNamesUtils.PurchaseFields.ANIMAL),
                (String) document.get(KeysNamesUtils.PurchaseFields.ITEM_NAME),
                (String) document.get(KeysNamesUtils.PurchaseFields.DATE),
                (String) document.get(KeysNamesUtils.PurchaseFields.CATEGORY),
                Objects.requireNonNull(document.getDouble(KeysNamesUtils.PurchaseFields.COST)).floatValue(),
                Objects.requireNonNull(document.getLong(KeysNamesUtils.PurchaseFields.AMOUNT)).intValue()
        );

        purchase.setOwner((String) document.get(KeysNamesUtils.PurchaseFields.OWNER));
        return purchase;
    }

    @NonNull
    @Override
    public Object clone() {
        Object o = null;

        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }

        return o;
    }
}
