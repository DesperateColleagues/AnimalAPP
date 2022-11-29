package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddPurchaseFragment;
import it.uniba.dib.sms22235.entities.operations.Purchase;

public class PurchaseFragment extends Fragment implements DialogAddPurchaseFragment.DialogAddPurchaseFragmentListener {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_passionate_purchase, container, false);
    }

    @Override
    public void onDialogAddPurchaseFragmentDismissed(Purchase purchase) {

    }
}