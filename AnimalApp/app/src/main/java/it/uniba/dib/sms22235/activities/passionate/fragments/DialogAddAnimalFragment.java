package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.registration.RegistrationActivity;
import it.uniba.dib.sms22235.activities.registration.fragments.RegistrationPersonFragment;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.User;
import it.uniba.dib.sms22235.entities.users.Veterinary;

public class DialogAddAnimalFragment extends DialogFragment {

    public interface DialogAddAnimalFragmentListener {
        /**
         * Triggered when the user completes its sign up process
         *
         * @param animal the animal to register
         * */
        void onAnimalRegistered(Animal animal);
    }

    private DialogAddAnimalFragmentListener listener;

    public DialogAddAnimalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (DialogAddAnimalFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_animal, null);

        builder.setView(root);
        builder.setTitle("Registrazione Animale");

        EditText txtInputAnimalName = root.findViewById(R.id.txtInputAnimalName);
        EditText txtInputAnimalSpecies = root.findViewById(R.id.txtInputAnimalSpecies);
        EditText txtInputRace = root.findViewById(R.id.txtInputRace);
        EditText txtInputMicrochipCode = root.findViewById(R.id.txtInputMicrochipCode);
        EditText txtInputBirthDate = root.findViewById(R.id.txtInputBirthDate);

        Button btnConfirmAnimalRegistration = root.findViewById(R.id.btnConfirmAnimalRegistration);

        btnConfirmAnimalRegistration.setOnClickListener(v -> {
            String animalName = txtInputAnimalName.getText().toString();
            String animalSpecies = txtInputAnimalSpecies.getText().toString();
            String race = txtInputRace.getText().toString();
            String microchipCode = txtInputMicrochipCode.getText().toString();
            //String birthDate = txtInputBirthDate.getText().toString();

            boolean isEmptyInput = animalName.equals("") || animalSpecies.equals("")
                    || race.equals("") || microchipCode.equals("");

            if (!isEmptyInput){
                listener.onAnimalRegistered(new Animal(animalName, animalSpecies, race,
                        microchipCode, "22-11-2022"));
            } else {
                Toast.makeText(getContext(), "Alcuni campi sono vuoti!", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}