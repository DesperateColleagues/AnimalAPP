package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import it.uniba.dib.sms22235.R;

import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAddPurchaseFragment;
import it.uniba.dib.sms22235.adapters.purchases.ListViewPurchasesAdapter;
import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment is used to display a purchases and to add them
 * */
public class PassionatePurchaseFragment extends Fragment implements
        DialogAddPurchaseFragment.DialogAddPurchaseFragmentListener,
        PassionateFilterPurchaseFragment.FilterPurchaseFragmentListener,
        Serializable {

    /**
     * Operation of this fragment
     * */
    public interface PurchaseFragmentListener {
        /**
         * This method is called when the dialog to add the Purchase is dismissed.
         * It carries the purchase data, that must be stored into firebase and used
         * to update the list view to show all the purchases
         *
         * @param purchase the registered purchase
         * */
        void onPurchaseRegistered(Purchase purchase);

        /**
         * This callback is used to eliminate a purchase once the user click confirm to delete option
         *
         * @param pos the position of the purchase to delete
         * @param dataSetPurchase the data set where the purchase will be eliminated
         * @param adapter the adapter to update
         * */
        void onPurchaseDeleted(int pos,
                               ArrayList<Purchase> dataSetPurchase,
                               ListViewPurchasesAdapter adapter);
    }

    private transient PurchaseFragmentListener listener;
    private transient ListViewPurchasesAdapter purchaseAdapter;
    private transient NavController controller;
    private transient QueryPurchasesManager queryPurchases;
    private transient ListView purchaseListView;

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (PurchaseFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        controller = Navigation.findNavController(container);
        queryPurchases = new QueryPurchasesManager(requireContext());

        return inflater.inflate(R.layout.fragment_passionate_purchase, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Purchase> purchasesList;

        Context context = requireContext();

        Bundle arguments = getArguments();

        if (arguments != null) {
            purchasesList = (ArrayList<Purchase>) arguments.getSerializable(KeysNamesUtils.BundleKeys.FILTER_ADAPTER);

            // If the arguments has size 0 then the filter query does not return any result.
            // The original data set will be displayed
            if (purchasesList.size() == 0) {
                purchasesList = ((PassionateNavigationActivity)requireActivity()).getPurchasesList();
            }

        } else {
            purchasesList = ((PassionateNavigationActivity)requireActivity()).getPurchasesList();
        }

        purchaseAdapter = new ListViewPurchasesAdapter(context, 0);

        if (purchasesList.size() > 0) {
            purchaseAdapter.setPurchasesList(purchasesList);
        }

        // Once the purchases are retrieved from the activity the list view che be built
        purchaseListView = view.findViewById(R.id.purchaseListView);
        purchaseListView.setAdapter(purchaseAdapter);
        purchaseListView.setOnItemClickListener((parent, view1, position, id) -> {
            ListViewPurchasesAdapter currentAdapter = (ListViewPurchasesAdapter) parent.getAdapter();
            ArrayList<Purchase> currentDataSet = currentAdapter.getPurchasesList();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);

            // Set dialog title
            @SuppressLint("InflateParams")
            View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_title);
            titleText.setText(R.string.dati_spesa);
            builder.setCustomTitle(titleView);
            // Show the purchase info
            builder.setMessage(
                    "• " + getString(R.string.nome_item) +  ": " + currentDataSet.get(position).getItemName() +
                    "\n• " + getString(R.string.costo) + ": " + currentDataSet.get(position).getCost() +
                    "\n• " + getString(R.string.costo_totale) + ": " + (currentDataSet.get(position).getCost() * currentDataSet.get(position).getAmount() +
                    "\n• " + getString(R.string.data_acquisto) + ": " + currentDataSet.get(position).getDate()));

            // Set a positive action that let the user delete the purchase. The purchase is deleted
            // only if the user confirm the deletion by another alert dialog
            builder.setPositiveButton(R.string.elimina, (dialog, which) -> {
                listener.onPurchaseDeleted(position, currentDataSet, currentAdapter);
                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.chiudi, ((dialog, which) -> dialog.dismiss()));

            builder.create().show();

        });

        // Obtain data from Activity
        LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity())
                .getAnimalSet();


        FloatingActionButton fab = ((PassionateNavigationActivity) requireActivity())
                .getFab();
        fab.setVisibility(View.VISIBLE);

        String owner = ((PassionateNavigationActivity) requireActivity()).getUserId();

        // Get the fab from the activity and set the listener
        fab.setOnClickListener(v -> {
            if (animalSet.size() > 0) {
                DialogAddPurchaseFragment dialogAddPurchaseFragment = new DialogAddPurchaseFragment(
                        buildSpinnerEntries(animalSet));

                dialogAddPurchaseFragment.setListener(this);
                dialogAddPurchaseFragment.show(requireActivity().getSupportFragmentManager(),
                        "DialogAddPurchaseFragment");
            } else {
                Toast.makeText(context, getString(R.string.aggiunta_spesa_fallita_no_animali), Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonFilter = view.findViewById(R.id.buttonFilter);

        buttonFilter.setOnClickListener(v -> {
                ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);
                fab.setVisibility(View.GONE);

                Bundle bundle = new Bundle();

                // Add the list of animal's names
                bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS,
                        buildSpinnerEntries(animalSet));

                // Add a binding to FilterPurchaseFragment listener
                bundle.putSerializable(KeysNamesUtils.BundleKeys.INTERFACE, this);

                float minCost = -1, maxCost = -1;
                Cursor cursor = queryPurchases.getMinimumPurchaseValue(owner);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            minCost = cursor.getFloat(cursor.getColumnIndexOrThrow("minCost"));
                        }
                    }
                }

                cursor = queryPurchases.getMaximumPurchaseValue(owner);
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            maxCost = cursor.getFloat(cursor.getColumnIndexOrThrow("maxCost"));
                        }
                    }
                }

                bundle.putFloat(KeysNamesUtils.BundleKeys.MIN_COST, minCost);
                bundle.putFloat(KeysNamesUtils.BundleKeys.MAX_COST, maxCost);

                controller.navigate(R.id.action_passionate_purchase_to_filterPurchaseFragment, bundle);
        });

        SearchView searchView = view.findViewById(R.id.searchViewProduct);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // No need to implement this kind of search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {
                // Execute the query to obtain the purchases with the searched item
                Cursor cursor = queryPurchases.getPurchaseByItemNameQuery(searchText, owner);

                // Create a new adapter which will contain the purchase from the search query
                ListViewPurchasesAdapter adapterSearchedPurchases =  new ListViewPurchasesAdapter(
                        requireContext(), 0);

                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            // Retrieve the purchase using Cursor
                            Purchase purchase = new Purchase(
                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_ANIMAL)),

                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_ITEM_NAME)),

                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_DATE)),

                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_CATEGORY)),

                                    cursor.getFloat(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST)),

                                    cursor.getInt(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_AMOUNT))
                            );

                            purchase.setOwner(cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER
                            )));

                            purchase.setId(cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_ID
                            )));

                            adapterSearchedPurchases.addPurchase(purchase);
                            purchaseListView.setAdapter(adapterSearchedPurchases);
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDialogAddPurchaseFragmentDismissed(@NonNull Purchase purchase) {
        purchaseAdapter.addPurchase(purchase);
        purchaseAdapter.notifyDataSetChanged();
        listener.onPurchaseRegistered(purchase);
    }

    @Override
    public ArrayList<Purchase> onFiltersAdded(String owner, List<String> animals, List<String> categories, Interval<Float> costs, String dateFrom, String dateTo) {
        Cursor cursor = queryPurchases.runFilterQuery(owner, animals, categories, costs,
                dateFrom, dateTo);
        ArrayList<Purchase> purchasesSubList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Log.e("AnimalAPP - Spese", "PassionatePurchaseFragment:253 - Dimensione cursore: " + cursor.getCount());
                while (cursor.moveToNext()){
                    // Retrieve the purchase using Cursor
                    Purchase purchase = new Purchase(
                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_ANIMAL)),

                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_ITEM_NAME)),

                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_DATE)),

                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_CATEGORY)),

                            cursor.getFloat(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_COST)),

                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseContract.COLUMN_NAME_AMOUNT))
                    );

                    purchase.setId(cursor.getString(cursor.getColumnIndexOrThrow(
                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_ID
                    )));

                    purchase.setOwner(cursor.getString(cursor.getColumnIndexOrThrow(
                            KeysNamesUtils.PurchaseContract.COLUMN_NAME_OWNER)));

                    purchasesSubList.add(purchase);
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.nessun_risultato), Toast.LENGTH_SHORT).show();
            }
        }

        return purchasesSubList;
    }

    @NonNull
    private ArrayList<String> buildSpinnerEntries(@NonNull LinkedHashSet<Animal> animals) {
        ArrayList<String> list = new ArrayList<>();

        for (Animal animal : animals) {
            list.add(animal.toString());
        }

        return list;
    }
}