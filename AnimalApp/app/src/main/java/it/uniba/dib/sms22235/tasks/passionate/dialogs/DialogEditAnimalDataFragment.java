package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.veterinarian.VeterinarianArrayAdapter;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

/**
 * This dialog is used to edit animal data
 * */
public class DialogEditAnimalDataFragment extends DialogFragment {

    private List<Veterinarian> veterinarianList;
    private Animal animal;
    private Spinner veterinarianListSpinner;

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public void setVeterinarianList(List<Veterinarian> veterinarianList) {
        this.veterinarianList = veterinarianList;
    }

    /**
     * Specify what happens when an animal is edited
     * */
    public interface DialogEditAnimalDataFragmentListener{
        /**
         * Used to add a veterinarian to the animal
         *
         * @param selectedAnimal the selected animal microchip
         * */
        void onDialogChoosedVeterinarian(Animal selectedAnimal);
    }

    private DialogEditAnimalDataFragment.DialogEditAnimalDataFragmentListener listener;

    public void setListener(DialogEditAnimalDataFragment.DialogEditAnimalDataFragmentListener listener) {
        this.listener = listener;
    }

    public DialogEditAnimalDataFragment(Animal animal, List<Veterinarian> veterinarianList) {
        this.animal = animal;
        this.veterinarianList = veterinarianList;
    }

    public DialogEditAnimalDataFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle SavedInstanceBundle){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(),
                R.style.AlertDialogTheme);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_choose_animal, null);
        builder.setView(root);

        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(getString(R.string.modifica_info_animale));
        builder.setCustomTitle(titleView);

        veterinarianListSpinner = root.findViewById(R.id.animalSpinner);

        VeterinarianArrayAdapter dataAdapter = new VeterinarianArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, veterinarianList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        veterinarianListSpinner.setAdapter(dataAdapter);

        TextInputLayout textInputEditWeight = root.findViewById(R.id.textinput_weight);
        TextInputLayout textInputEditHeight = root.findViewById(R.id.textinput_height);
        TextInputLayout textInputEditNature = root.findViewById(R.id.textinput_nature);
        textInputEditWeight.setVisibility(View.VISIBLE);
        textInputEditHeight.setVisibility(View.VISIBLE);
        textInputEditNature.setVisibility(View.VISIBLE);
        EditText txtInputWeight = root.findViewById(R.id.txtInputWeight);
        EditText txtInputHeight = root.findViewById(R.id.txtInputHeight);
        EditText txtInputNature = root.findViewById(R.id.txtInputNature);

        txtInputNature.setText(animal.getNature());
        txtInputHeight.setText(animal.getHeight() + "");
        txtInputWeight.setText(animal.getWeight() + "");

        Button btnConfirmChooseAnimal = root.findViewById(R.id.btnConfirmChooseAnimal);
        btnConfirmChooseAnimal.setOnClickListener(v -> {
            String weight = txtInputWeight.getText().toString();
            String height = txtInputHeight.getText().toString();
            String nature = txtInputNature.getText().toString();

            boolean isEmptyInput = weight.equals("") || height.equals("") || nature.equals("");

            if (!isEmptyInput) {
                animal.setNature(nature);
                animal.setHeight(Double.parseDouble(height));
                animal.setWeight(Double.parseDouble(weight));
                animal.setVeterinarian(((Veterinarian) veterinarianListSpinner.getSelectedItem()).getEmail());
            }

            listener.onDialogChoosedVeterinarian(animal);
            dismiss();
        });


        return builder.create();
    }
}
