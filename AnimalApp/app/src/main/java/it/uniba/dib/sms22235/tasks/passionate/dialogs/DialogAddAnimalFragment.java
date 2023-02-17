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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;

/**
 * This dialog is used to inser animal data
 * */
public class DialogAddAnimalFragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    /**
     * Interface that specifies the action that occur when an animal data is addd
     * */
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
        titleText.setText(R.string.registrazione_animale);
        builder.setCustomTitle(titleView);

        SimpleDateFormat dateSDF = new SimpleDateFormat("dd/MM/yy", Locale.ITALY);

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
            long dateDistance = -1;

            String animalName = txtInputAnimalName.getText().toString();
            String animalSpecies = txtInputAnimalSpecies.getText().toString();
            String race = txtInputRace.getText().toString();
            String microchipCode = txtInputMicrochipCode.getText().toString();
            String birthDate = txtInputBirthDate.getText().toString();

            try {
                Date d1 = dateSDF.parse(birthDate);
                Date d2 = dateSDF.parse(dateSDF.format(new Date()));
                dateDistance = d2.getTime() - d1.getTime();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            boolean isEmptyInput = animalName.equals("") || animalSpecies.equals("")
                    || race.equals("") || microchipCode.equals("") || birthDate.equals("");

            // If the input is not empty the animal can be registered
            if (!isEmptyInput && dateDistance >= 0){
                listener.onDialogAddAnimalDismissed(new Animal(animalName, animalSpecies, race,
                        microchipCode, birthDate));
                dismiss();
            } else if (isEmptyInput) {
                Toast.makeText(getContext(), getResources().getString(R.string.alcuni_campi_vuoti), Toast.LENGTH_SHORT).show();
            } else if (dateDistance < 0) {
                Toast.makeText(getContext(), getResources().getString(R.string.error_date), Toast.LENGTH_SHORT).show();
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