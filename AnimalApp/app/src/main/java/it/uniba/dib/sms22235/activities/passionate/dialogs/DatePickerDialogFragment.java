package it.uniba.dib.sms22235.activities.passionate.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerDialogFragment extends DialogFragment  {

    private final Fragment parent;

    public DatePickerDialogFragment(Fragment parent) {
        this.parent = parent;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Set the correct language of the Calendar based on user's local

        Locale.setDefault(Locale.ITALY);

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialogFrag and return it
        return new android.app.DatePickerDialog(getContext(),
                (DatePickerDialog.OnDateSetListener) parent, year, month, day);
    }

}
