package it.uniba.dib.sms22235.adapters.purchases;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * Adapter to display purchases as a list
 * */
public class ListViewPurchasesAdapter extends ArrayAdapter<Purchase> {
    private ArrayList<Purchase> purchasesList;

    public ListViewPurchasesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        purchasesList = new ArrayList<>();
    }

    public void addPurchase(Purchase purchase){
        purchasesList.add(purchase);
        Log.d("AnimalAPP - Acquisto", "ListViewPurchasesAdapter:34" + purchasesList.size());
    }

    public void setPurchasesList(ArrayList<Purchase> purchasesList) {
        this.purchasesList = purchasesList;
    }

    public ArrayList<Purchase> getPurchasesList() {
        return purchasesList;
    }

    @Override
    public int getCount() {
        return purchasesList.size();
    }

    @Nullable
    @Override
    public Purchase getItem(int position) {
        return purchasesList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_fragment_passionate_purchase, parent, false);
        }

        TextView txtItemName = listView.findViewById(R.id.txtItemName);
        TextView txtCost = listView.findViewById(R.id.txtCost);
        TextView txtAnimalPurchase = listView.findViewById(R.id.txtAnimalPurchase);
        TextView txtDatePurchase = listView.findViewById(R.id.txtDatePurchase);
        TextView txtCostXAmount = listView.findViewById(R.id.txtCostXAmount);

        ImageView categoryImageShow = listView.findViewById(R.id.categoryImageShow);

        Purchase purchase = getItem(position);

        if (purchase != null) {
            switch (purchase.getCategory()) {
                case "Toelettatura":
                    categoryImageShow.setImageResource(KeysNamesUtils.PurchaseCategory.CLEANING);
                    break;
                case "Divertimento":
                    categoryImageShow.setImageResource(KeysNamesUtils.PurchaseCategory.ENJOYMENT);
                    break;
                case "Cibo":
                    categoryImageShow.setImageResource(KeysNamesUtils.PurchaseCategory.FOOD);
                    break;
                case "Spese mediche":
                    categoryImageShow.setImageResource(KeysNamesUtils.PurchaseCategory.MEDIC);
                    break;
            }

            String costAmount = "" + (purchase.getCost()) + " € x " + purchase.getAmount();
            String cost = "" + (purchase.getCost() * purchase.getAmount()) + " €";
            String animal = purchase.getAnimal();
            String date = purchase.getDate();

            txtAnimalPurchase.setText(animal);
            txtDatePurchase.setText(date);
            txtCostXAmount.setText(costAmount);
            txtCost.setText(cost);
            txtItemName.setText(purchase.getItemName());
        }

        return listView;
    }
}
