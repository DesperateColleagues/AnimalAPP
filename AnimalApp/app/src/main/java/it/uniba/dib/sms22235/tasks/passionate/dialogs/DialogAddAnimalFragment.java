package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;

public class DialogAddAnimalFragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    public interface DialogAddAnimalFragmentListener {
        /**
         * Callback triggered when the user completes its sign up process
         *
         * @param animal the animal to register
         * */
        void onDialogAddAnimalDismissed(Animal animal);
    }

    private DialogAddAnimalFragmentListener listener;
    private EditText txtInputBirthDate;

    public DialogAddAnimalFragment() {
        // Required empty public constructor
    }

    public void setListener(DialogAddAnimalFragmentListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_animal, null);

        // Set dialog main options
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Registrazione animale");
        builder.setCustomTitle(titleView);

        // Retrieve EditTexts objects from the inflated view
        EditText txtInputAnimalName = root.findViewById(R.id.txtInputAnimalName);
        EditText txtInputAnimalSpecies = root.findViewById(R.id.txtInputAnimalSpecies);
        EditText txtInputRace = root.findViewById(R.id.txtInputRace);
        EditText txtInputMicrochipCode = root.findViewById(R.id.txtInputMicrochipCode);

        txtInputBirthDate = root.findViewById(R.id.txtInputBirthDate);
        // Show the date picker
        txtInputBirthDate.setOnClickListener(v -> {
            DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment(this);
            datePickerFragment.show(getParentFragmentManager(), "DatePickerFragment");
        });

        Button btnConfirmAnimalRegistration = root.findViewById(R.id.btnConfirmAnimalRegistration);

        btnConfirmAnimalRegistration.setOnClickListener(v -> {
            String animalName = txtInputAnimalName.getText().toString();
            String animalSpecies = txtInputAnimalSpecies.getText().toString();
            String race = txtInputRace.getText().toString();
            String microchipCode = txtInputMicrochipCode.getText().toString();
            String birthDate = txtInputBirthDate.getText().toString();

            boolean isEmptyInput = animalName.equals("") || animalSpecies.equals("")
                    || race.equals("") || microchipCode.equals("") || birthDate.equals("");

            // If the input is not empty the animal can be registered
            if (!isEmptyInput){
                listener.onDialogAddAnimalDismissed(new Animal(animalName, animalSpecies, race,
                        microchipCode, birthDate));
                dismiss();
            } else {
                Toast.makeText(getContext(), "Alcuni campi sono vuoti!", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(mCalendar.getTime());
        txtInputBirthDate.setText(selectedDate);
    }
}