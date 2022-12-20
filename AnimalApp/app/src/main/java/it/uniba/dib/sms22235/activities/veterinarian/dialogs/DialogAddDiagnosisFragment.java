package it.uniba.dib.sms22235.activities.veterinarian.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DatePickerDialogFragment;
import it.uniba.dib.sms22235.common_dialogs.TimePickerDialogFragment;
import it.uniba.dib.sms22235.entities.operations.Reservation;


public class DialogAddDiagnosisFragment extends DialogFragment {

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_diagnosis, null);
        builder.setView(root);
        builder.setTitle("Aggiunta diagnosi");/*

        txtInputReservationDate = root.findViewById(R.id.txtInputReservationDate);
        txtInputReservationTime = root.findViewById(R.id.txtInputReservationTime);

        txtInputReservationDate.setOnClickListener(v -> {
            DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment(this);
            datePickerFragment.show(getParentFragmentManager(), "DatePickerFragment");
        });

        txtInputReservationTime.setOnClickListener(v -> {
            TimePickerDialogFragment timePickerFragment = new TimePickerDialogFragment(this);
            timePickerFragment.show(getParentFragmentManager(), "TimePickerFragment");
        });

        Button btnConfirmAddReservation = root.findViewById(R.id.btnConfirmAddReservation);
        btnConfirmAddReservation.setOnClickListener(v -> {
            String date = txtInputReservationDate.getText().toString();
            String time = txtInputReservationTime.getText().toString();

            boolean isEmptyInput = date.equals("") || time.equals("");

            if(!isEmptyInput){
                listener.onDialogAddReservationDismissed(new Reservation(date, time));
                dismiss();
            }
        });
*/

        return builder.create();
    }
}