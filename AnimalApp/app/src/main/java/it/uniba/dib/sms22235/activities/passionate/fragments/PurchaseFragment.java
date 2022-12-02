package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddPurchaseFragment;
import it.uniba.dib.sms22235.adapters.ListViewPurchasesAdapter;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PurchaseFragment extends Fragment implements DialogAddPurchaseFragment.DialogAddPurchaseFragmentListener {

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
    private ListViewPurchasesAdapter adapter;
    private NavController controller;

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

        return inflater.inflate(R.layout.fragment_passionate_purchase, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Purchase> purchasesList = ((PassionateNavigationActivity) requireActivity()).getPurchasesList();

        Context context = requireContext();

        adapter = new ListViewPurchasesAdapter(context, 0);

        if (purchasesList.size() > 0) {
            
            adapter.setPurchasesList(purchasesList);
        }

        // Once the purchases are retrieved from the activity the list view che be built
        ListView purchaseListView = view.findViewById(R.id.purchaseListView);
        purchaseListView.setAdapter(adapter);

        // Obtain data from Activity
        LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity()).getAnimalSet();
        FloatingActionButton fab = ((PassionateNavigationActivity) requireActivity()).getFab();

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

            bundle.putSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS,
                    buildSpinnerEntries(animalSet));
            controller.navigate(R.id.filterPurchaseFragment, bundle);
        });
    }

    @Override
    public void onDialogAddPurchaseFragmentDismissed(@NonNull Purchase purchase) {
        adapter.addPurchase(purchase);
        adapter.notifyDataSetChanged();

        listener.onPurchaseRegistered(purchase);
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