package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;


public class FilterPurchaseFragment extends Fragment {

    private ArrayList<String> animaList;
    private ArrayList<String> categories;

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            animaList = (ArrayList<String>) arguments.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS);

            if (animaList == null) {
                animaList = new ArrayList<>();

                Toast.makeText(getContext(), "Lista animali vuota, impossibile effettuare ricerche",
                        Toast.LENGTH_SHORT).show();
            }
        }

        categories = new ArrayList<>();

        categories.add("Toelettatura");
        categories.add("Divertimento");
        categories.add("Cibo");
        categories.add("Spese mediche");

        return inflater.inflate(R.layout.fragment_passionate_filter_purchase, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinnerAnimalsFilter = view.findViewById(R.id.spinnerAnimalsFilter);
        Spinner spinnerCategoriesFilter = view.findViewById(R.id.spinnerCategoriesFilter);


        if (animaList.size() > 0) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_dropdown_item, animaList);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAnimalsFilter.setAdapter(spinnerAdapter);
        }

        ArrayAdapter<String> spinnerCategory = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoriesFilter.setAdapter(spinnerCategory);
    }
}
