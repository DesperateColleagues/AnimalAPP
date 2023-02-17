package it.uniba.dib.sms22235.entities.operations;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This class represent a purchase of the user
 * */
public class Purchase implements Serializable, Cloneable {
    private String owner;
    private String animal;
    private String itemName;
    private String date;
    private String category;
    private float cost;
    private int amount;
    private String id;

    public Purchase() {}

    /**
     *
     * @param animal the animal of the purchase
     * @param itemName the name of the bought item
     * @param date the date of the purchase
     * @param category the category of the item
     * @param cost the cost of the purchase
     * @param amount the amount of item bought
     * */
    public Purchase(String animal, String itemName, String date, String category, float cost, int amount) {
        this.animal = animal;
        this.itemName = itemName;
        this.category = category;
        this.cost = cost;
        this.amount = amount;
        this.date = date;
        id = UUID.randomUUID().toString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method is used to create a purchase object
     * given the document stored in FirebaseFirestore
     *
     * @param document the document with purchase data
     * @return the new instance of the purchase
     * */
    @NonNull
    public static Purchase loadPurchase(@NonNull DocumentSnapshot document) {
        Purchase purchase = new Purchase(
                (String) document.get(KeysNamesUtils.PurchaseContract.COLUMN_NAME_ANIMAL),
                (String) document.get(KeysNamesUtils.PurchaseContract.COLUMN_NAME_ITEM_NAME),
                (String) document.get(KeysNamesUtils.PurchaseContract.COLUMN_NAME_DATE),
                (String) document.get(KeysNamesUtils.PurchaseContract.COLUMN_NAME_CATEGORY),
                Objects.requireNonNull(document.getDouble(KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST)).floatValue(),
                Objects.requireNonNull(document.getLong(KeysNamesUtils.PurchaseContract.COLUMN_NAME_AMOUNT)).intValue()
        );

        purchase.setId(document.getString(KeysNamesUtils.PurchaseContract.COLUMN_NAME_ID));
        purchase.setOwner((String) document.get(KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER));

        return purchase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return id.equals(purchase.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
