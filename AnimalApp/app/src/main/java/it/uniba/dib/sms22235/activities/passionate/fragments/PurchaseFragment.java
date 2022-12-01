package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddPurchaseFragment;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;

public class PurchaseFragment extends Fragment implements DialogAddPurchaseFragment.DialogAddPurchaseFragmentListener {

    public interface PurchaseFragmentListener {
        /**
         * This method is called when the dialog to add the Purchase is dismissed.
         * It carries the purchase data, that must be stored into firebase and used
         * to update the list view to show all the purchases
         *
         * @param purchase the registered purchase
         * */
        void onPurchaseRegistered(Purchase purchase, ListView listView);

        /**
         * */
        void retrieveUserPurchases(ListView listView);
    }

    private PurchaseFragmentListener listener;
    private ListView purchaseListView;
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

        View root = inflater.inflate(R.layout.fragment_passionate_purchase, container, false);

        purchaseListView = root.findViewById(R.id.purchaseListView);
        listener.retrieveUserPurchases(purchaseListView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain data from Activity
        ArrayList<Animal> animalList = ((PassionateNavigationActivity) requireActivity()).getAnimalList();
        FloatingActionButton fab = ((PassionateNavigationActivity) requireActivity()).getFab();

        // Get the fab from the activity and set the listener
        fab.setOnClickListener(v -> {
            DialogAddPurchaseFragment dialogAddPurchaseFragment = new DialogAddPurchaseFragment(animalList);
            dialogAddPurchaseFragment.setListener(this);
            dialogAddPurchaseFragment.show(requireActivity().getSupportFragmentManager(),
                    "DialogAddPurchaseFragment");
        });

        Button buttonFilter = view.findViewById(R.id.buttonFilter);
        buttonFilter.setOnClickListener(v -> {
            ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);
            fab.setVisibility(View.GONE);

            Bundle bundle = new Bundle();

            bundle.putSerializable("ANIMAL", animalList);
            controller.navigate(R.id.filterPurchaseFragment, bundle);
        });
    }

    @Override
    public void onDialogAddPurchaseFragmentDismissed(@NonNull Purchase purchase) {
        // Notify the activity to save the purchase into the DB and to update the ListView
        listener.onPurchaseRegistered(purchase, purchaseListView);
    }
}