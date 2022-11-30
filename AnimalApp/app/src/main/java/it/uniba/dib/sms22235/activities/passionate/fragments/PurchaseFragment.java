package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
        void onPurchaseRegistered(Purchase purchase);
    }

    private PurchaseFragmentListener listener;

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
        return inflater.inflate(R.layout.fragment_passionate_purchase, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Animal> animalList = ((PassionateNavigationActivity) requireActivity()).getAnimalList();

        // Get the fab from the activity and set the listener
        ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            DialogAddPurchaseFragment dialogAddPurchaseFragment = new DialogAddPurchaseFragment(animalList);
            dialogAddPurchaseFragment.setListener(this);
            dialogAddPurchaseFragment.show(requireActivity().getSupportFragmentManager(),
                    "DialogAddPurchaseFragment");
        });
    }

    @Override
    public void onDialogAddPurchaseFragmentDismissed(@NonNull Purchase purchase) {
        // Notify the activity to save the purchase into the DB and to update the ListView
        // todo: add list view as input and pass it to the activity
        listener.onPurchaseRegistered(purchase);
    }
}