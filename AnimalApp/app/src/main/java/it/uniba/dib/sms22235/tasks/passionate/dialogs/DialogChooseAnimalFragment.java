package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;

/**
 * A dialog with a spinner to select a specific animal
 * */
public class DialogChooseAnimalFragment extends DialogFragment {

    private Spinner animalListSpinner;
    private final List<Animal> animalsByVeterinarian;

    public DialogChooseAnimalFragment(List<Animal> animalsByVeterinarian) {
        this.animalsByVeterinarian = animalsByVeterinarian;
    }


    /**
     * Specify what happens when an animal is selected
     * */
    public interface DialogChooseAnimalFragmentListener{
        /**
         * Callback called when an animal is selected
         *
         * @param selectedAnimal selected animal microchip
         * */
        void onDialogSelectedAnimal(String selectedAnimal);
    }

    private DialogChooseAnimalFragment.DialogChooseAnimalFragmentListener listener;

    public void setListener(DialogChooseAnimalFragmentListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_choose_animal, null);

        // Set dialog title
        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(getString(R.string.scegli_animale_prenotazione));
        builder.setCustomTitle(titleView);
        builder.setView(root);

        animalListSpinner = root.findViewById(R.id.animalSpinner);

        List<String> list = new ArrayList<>();
        for (Animal animal : animalsByVeterinarian) {
            list.add(animal.getMicrochipCode());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalListSpinner.setAdapter(dataAdapter);

        Button btnConfirmChooseAnimal = root.findViewById(R.id.btnConfirmChooseAnimal);
        btnConfirmChooseAnimal.setOnClickListener(v -> {
            listener.onDialogSelectedAnimal((String) animalListSpinner.getSelectedItem());
            dismiss();
        });


        return builder.create();
    }
}
