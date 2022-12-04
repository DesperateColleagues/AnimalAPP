package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;


public class FilterPurchaseFragment extends Fragment {

    public interface FilterPurchaseFragmentListener {
        void onFiltersAdded(List<String> animals, List<String> categories, Interval<Float> costs);
    }

    private ArrayList<String> animaList;
    private ArrayList<String> categories;

    private FilterPurchaseFragmentListener listener;

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            // Retrieve the animal list
            animaList = (ArrayList<String>) arguments
                    .getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS);

            // Initialize the listener
            listener = (FilterPurchaseFragmentListener) arguments
                    .getSerializable(KeysNamesUtils.BundleKeys.INTERFACE);

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

        ChipGroup animalsChipGroup = view.findViewById(R.id.animalsChipGroup);
        ChipGroup categoriesChipGroup = view.findViewById(R.id.categoriesChipGroup);

        if (animaList.size() > 0) {
            for(String entry : animaList) {
                @SuppressLint("InflateParams") Chip chip = (Chip) getLayoutInflater()
                        .inflate(R.layout.item_chip_fragment_filter, null);
                chip.setText(entry);
                chip.setCloseIcon(null);
                chip.setOnClickListener(v -> chip.setSelected(true));
                animalsChipGroup.addView(chip);
            }
        }

        for (String category : categories) {
            @SuppressLint("InflateParams") Chip chip = (Chip) getLayoutInflater()
                    .inflate(R.layout.item_chip_fragment_filter, null);
            chip.setText(category);
            chip.setCloseIcon(null);
            chip.setOnClickListener(v -> chip.setSelected(true));
            categoriesChipGroup.addView(chip);
        }

        Button btnAddFilter = view.findViewById(R.id.btnAddFilter);
        btnAddFilter.setOnClickListener(v -> {
            // todo: pass the selected filter to purchase fragment
            listener.onFiltersAdded(null, null, null);
        });
    }
}
