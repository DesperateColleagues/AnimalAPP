package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;

public class DialogAddPokeLink extends DialogFragment {

    public interface DialogAddPokeLinkListener {
        /**
         * This method is used to load into a spinner all the animals
         * that do not belongs to the current logged passionate
         *
         * @param spinner the spinner where to load the animals
         * */
        void loadOtherAnimals(Spinner spinner);

        /**
         * Callback called when a new link is added
         *
         * @param myCode the microchip of the passionate animal
         * @param otherCode the microchip of the other animal
         * @param type the type chosen
         * @param description the description of the poke link
         * */
        void onLinkAdded(String myCode, String otherCode, String type, String description);
    }

    private DialogAddPokeLinkListener listener;

    public void setListener(DialogAddPokeLinkListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_add_poke_link, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AnimalCardRoundedDialog);
        builder.setView(root);

        // Set dialog title
        @SuppressLint("InflateParams")
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(R.string.bill_pc);
        builder.setCustomTitle(titleView);

        Spinner passionateAnimalsSpinner = root.findViewById(R.id.passionateAnimalsSpinner);
        TextView txtInputNatureDescription = root.findViewById(R.id.txtInputNatureDescription);

        LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity()).getAnimalSet();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, buildSpinnerEntries(animalSet));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        passionateAnimalsSpinner.setAdapter(spinnerAdapter);
        passionateAnimalsSpinner.setVisibility(View.VISIBLE);

        Spinner otherAnimalsSpinner = root.findViewById(R.id.otherAnimalsSpinner);
        listener.loadOtherAnimals(otherAnimalsSpinner);

        ChipGroup typeChipGroup = root.findViewById(R.id.typeChipGroup);



        root.findViewById(R.id.addPokeLink).setOnClickListener(v -> {
            String description = txtInputNatureDescription.getText().toString();
            String type = "";

            for (int i = 0; i < typeChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) typeChipGroup.getChildAt(i);

                if (chip.isChecked()) {
                    type = chip.getText().toString();
                }
            }

            if (!description.equals("") && !type.equals("")) {
                listener.onLinkAdded(
                        ((String) passionateAnimalsSpinner.getSelectedItem()).split(" - ")[1],
                        ((String) otherAnimalsSpinner.getSelectedItem()).split(" - ")[1],
                        type,
                        description
                        );
            }
        });

        return builder.create();
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
