package it.uniba.dib.sms22235.activities.passionate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

public class DialogEditAnimalDataFragment extends DialogFragment {

    private Animal animal;
    private Spinner veterinarianListSpinner;

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public List<Veterinarian> getVeterinarianList() {
        return veterinarianList;
    }

    public void setVeterinarianList(List<Veterinarian> veterinarianList) {
        this.veterinarianList = veterinarianList;
    }

    private List<Veterinarian> veterinarianList;

    public interface DialogEditAnimalDataFragmentListener{
        void onDialogChoosedVeterinarian(Animal selectedAnimal, String selectedVeterinarian);
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
                R.style.AnimalCardRoundedDialog);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_choose_animal, null);

        builder.setView(root);
        builder.setTitle("Modifica informazioni animale");

        veterinarianListSpinner = root.findViewById(R.id.animalSpinner);

        ArrayList<String> list = new ArrayList<>();

        for (Veterinarian veterinarian : veterinarianList) {
            list.add(veterinarian.getFullName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        veterinarianListSpinner.setAdapter(dataAdapter);

        Button btnConfirmChooseAnimal = root.findViewById(R.id.btnConfirmChooseAnimal);
        btnConfirmChooseAnimal.setOnClickListener(v -> {
            listener.onDialogChoosedVeterinarian(animal, (String) veterinarianListSpinner.getSelectedItem());
            dismiss();
        });


        return builder.create();
    }
}
