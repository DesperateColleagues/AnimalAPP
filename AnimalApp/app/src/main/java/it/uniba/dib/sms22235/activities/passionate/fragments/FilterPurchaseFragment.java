package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.database.QueryPurchasesManager;
import it.uniba.dib.sms22235.entities.operations.Interval;
import it.uniba.dib.sms22235.entities.operations.Purchase;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;


public class FilterPurchaseFragment extends Fragment {

    public interface FilterPurchaseFragmentListener {
        ArrayList<Purchase> onFiltersAdded(List<String> animals, List<String> categories, Interval<Float> costs);
    }

    private ArrayList<String> animaList;
    private ArrayList<String> categories;
    private Float minCost;
    private Float maxCost;


    private FilterPurchaseFragmentListener listener;
    private NavController controller;

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();


        controller = Navigation.findNavController(container);

        if (arguments != null) {
            // Retrieve the animal list
            animaList = (ArrayList<String>) arguments
                    .getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_ANIMALS);

            // Initialize the listener
            listener = (FilterPurchaseFragmentListener) arguments
                    .getSerializable(KeysNamesUtils.BundleKeys.INTERFACE);

            minCost = arguments.getFloat(KeysNamesUtils.BundleKeys.MIN_COST);

            maxCost = arguments.getFloat(KeysNamesUtils.BundleKeys.MAX_COST);

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
        RangeSlider costRangeSlider = view.findViewById(R.id.costRangeSlider);

        List<Float> minMaxCosts = new ArrayList<>();

        if (Objects.equals(minCost, maxCost)){

            costRangeSlider.setValueFrom(0F);
            costRangeSlider.setValueTo(maxCost);

            minMaxCosts.add(0F);
        } else {

            costRangeSlider.setValueFrom(minCost);
            costRangeSlider.setValueTo(maxCost);

            minMaxCosts.add(minCost);
        }
        minMaxCosts.add(maxCost);
        costRangeSlider.setValues(minMaxCosts);

        // TODO: must be write an efficient code to assign a right number of step for every interval

        float intervalSlider = maxCost - minCost;

        if (intervalSlider == 0){
            costRangeSlider.setStepSize(maxCost);
        } else if (intervalSlider > 0 && intervalSlider <= 50){
            costRangeSlider.setStepSize(1F);
        } else if (intervalSlider > 50 && intervalSlider <= 1000){
            costRangeSlider.setStepSize(intervalSlider / 25);
        } else if (intervalSlider > 1000 && intervalSlider <= 2500) {
            costRangeSlider.setStepSize(intervalSlider / 50);
        } else {
            costRangeSlider.setStepSize(intervalSlider / 100);
        }

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
            List<String> animalList = new ArrayList<>();
            List<String> categoryList = new ArrayList<>();
            Interval<Float> interval = null;

            for (int i = 0; i < animalsChipGroup.getChildCount(); i++){

                Chip chip = (Chip) animalsChipGroup.getChildAt(i);

                if (chip.isChecked()){
                    animalList.add(chip.getText().toString().split(" - ")[1]);
                }
            }

            for (int i = 0; i < categoriesChipGroup.getChildCount(); i++){

                Chip chip = (Chip) categoriesChipGroup.getChildAt(i);

                if (chip.isChecked()){
                    categoryList.add(chip.getText().toString());
                }
            }


            interval = new Interval<>(costRangeSlider.getValues().get(0),
                    costRangeSlider.getValues().get(1));

            if(animalList.size() == 0){
                animalList = null;
            }

            if (categoryList.size() == 0){
                categoryList = null;
            }

            ArrayList<Purchase> purchasesSubList = listener.onFiltersAdded(animalList, categoryList, interval);

            // Get back to the purchase fragment passing the list as bundle
            Bundle bundle = new Bundle();
            bundle.putSerializable(KeysNamesUtils.BundleKeys.FILTER_ADAPTER, purchasesSubList);

            controller.navigate(R.id.action_filterPurchaseFragment_to_passionate_purchase, bundle);

            // Update the view from the activity
            ((PassionateNavigationActivity) requireActivity()).restoreBottomAppBarVisibility();
        });
    }
}
