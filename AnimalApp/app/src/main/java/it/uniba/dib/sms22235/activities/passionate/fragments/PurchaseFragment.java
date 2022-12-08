package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ListView;
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
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddPurchaseFragment;
import it.uniba.dib.sms22235.adapters.ListViewPurchasesAdapter;
import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PurchaseFragment extends Fragment implements
        DialogAddPurchaseFragment.DialogAddPurchaseFragmentListener,
        FilterPurchaseFragment.FilterPurchaseFragmentListener,
        Serializable {

    public interface PurchaseFragmentListener {
        /**
         * This method is called when the dialog to add the Purchase is dismissed.
         * It carries the purchase data, that must be stored into firebase and used
         * to update the list view to show all the purchases
         *
         * @param purchase the registered purchase
         * */
        void onPurchaseRegistered(Purchase purchase);
    }

    private PurchaseFragmentListener listener;
    private ListViewPurchasesAdapter purchaseAdapter;
    private NavController controller;
    private QueryPurchasesManager queryPurchases;
    private ListView purchaseListView;

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

        // Obtain data from Activity
        LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity())
                .getAnimalSet();

        FloatingActionButton fab = ((PassionateNavigationActivity) requireActivity())
                .getFab();

        String username = ((PassionateNavigationActivity) requireActivity()).getPassionateUsername();

        // Get the fab from the activity and set the listener
        fab.setOnClickListener(v -> {
            DialogAddPurchaseFragment dialogAddPurchaseFragment = new DialogAddPurchaseFragment(
                    buildSpinnerEntries(animalSet));

            dialogAddPurchaseFragment.setListener(this);
            dialogAddPurchaseFragment.show(requireActivity().getSupportFragmentManager(),
                    "DialogAddPurchaseFragment");
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
            Cursor cursor = queryPurchases.getMinimumPurchaseValue(username);
            if(cursor != null) {
                if (cursor.getCount() > 0){
                    while (cursor.moveToNext()){
                        minCost = cursor.getFloat(cursor.getColumnIndexOrThrow("minCost"));
                    }
                }
            }

            cursor = queryPurchases.getMaximumPurchaseValue(username);
            if(cursor != null) {
                if (cursor.getCount() > 0){
                    while (cursor.moveToNext()){
                        maxCost = cursor.getFloat(cursor.getColumnIndexOrThrow("maxCost"));
                    }
                }
            }

            bundle.putFloat(KeysNamesUtils.BundleKeys.MIN_COST, minCost);
            bundle.putFloat(KeysNamesUtils.BundleKeys.MAX_COST, maxCost);

            controller.navigate(R.id.filterPurchaseFragment, bundle);
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
                Cursor cursor = queryPurchases.getPurchaseByItemNameQuery(searchText, username);

                // Create a new adapter which will contain the purchase from the search query
                ListViewPurchasesAdapter adapterSearchedPurchases =  new ListViewPurchasesAdapter(
                        requireContext(), 0);

                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            // Retrieve the purchase using Cursor
                            Purchase purchase = new Purchase(
                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseFields.ANIMAL)),

                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseFields.ITEM_NAME)),

                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseFields.DATE)),

                                    cursor.getString(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseFields.CATEGORY)),

                                    cursor.getFloat(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseFields.COST)),

                                    cursor.getInt(cursor.getColumnIndexOrThrow(
                                            KeysNamesUtils.PurchaseFields.AMOUNT))
                            );

                            purchase.setOwner(cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.OWNER)));

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
    public void onFiltersAdded(List<String> animals, List<String> categories, Interval<Float> costs) {
        Cursor cursor = queryPurchases.runFilterQuery(animals, categories, costs);
        ArrayList<Purchase> purchasesSubList = new ArrayList<>();

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                Log.e("COUNT", cursor.getCount() + "");
                while (cursor.moveToNext()){
                    // Retrieve the purchase using Cursor
                    Purchase purchase = new Purchase(
                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.ANIMAL)),

                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.ITEM_NAME)),

                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.DATE)),

                            cursor.getString(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.CATEGORY)),

                            cursor.getFloat(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.COST)),

                            cursor.getInt(cursor.getColumnIndexOrThrow(
                                    KeysNamesUtils.PurchaseFields.AMOUNT))
                    );

                    purchase.setOwner(cursor.getString(cursor.getColumnIndexOrThrow(
                            KeysNamesUtils.PurchaseFields.OWNER)));

                    purchasesSubList.add(purchase);

                    // Get back to the purchase fragment passing the list as bundle
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeysNamesUtils.BundleKeys.FILTER_ADAPTER, purchasesSubList);
                    controller.navigate(R.id.passionate_purchase, bundle);

                    // Update the view from the activity
                    ((PassionateNavigationActivity) requireActivity()).restoreBottomAppBarVisibility();
                }
            } else {
                Toast.makeText(requireContext(), "Nessun risultato", Toast.LENGTH_SHORT).show();
            }
        }
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