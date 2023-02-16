package it.uniba.dib.sms22235.tasks.common.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

/**
 * A classic time picker dialog
 * */
public class DialogTimePickerFragment extends DialogFragment {

    private final Fragment parent;

    public DialogTimePickerFragment(Fragment parent) {
        this.parent = parent;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Locale.setDefault(Locale.ITALY);

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        return new android.app.TimePickerDialog(getContext(), (TimePickerDialog.OnTimeSetListener) parent, hour, minutes, true);
    }
}
