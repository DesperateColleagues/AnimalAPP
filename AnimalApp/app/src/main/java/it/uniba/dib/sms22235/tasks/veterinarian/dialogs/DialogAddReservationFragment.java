package it.uniba.dib.sms22235.tasks.veterinarian.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DatePickerDialogFragment;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogTimePickerFragment;
import it.uniba.dib.sms22235.entities.operations.Reservation;

public class DialogAddReservationFragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText txtInputReservationDate;
    private EditText txtInputReservationTime;

    public DialogAddReservationFragment() {
    }


    public interface DialogAddReservationFragmentListener{
        void onDialogAddReservationDismissed(Reservation reservation);
    }

    private DialogAddReservationFragmentListener listener;

    public void setListener(DialogAddReservationFragmentListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_reservation, null);
        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(getResources().getString(R.string.aggiungi_appuntamento));
        builder.setCustomTitle(titleView);

        txtInputReservationDate = root.findViewById(R.id.txtInputReservationDate);
        txtInputReservationTime = root.findViewById(R.id.txtInputReservationTime);

        txtInputReservationDate.setOnClickListener(v -> {
            DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment(this);
            datePickerFragment.show(getParentFragmentManager(), "DatePickerFragment");
        });

        txtInputReservationTime.setOnClickListener(v -> {
            DialogTimePickerFragment timePickerFragment = new DialogTimePickerFragment(this);
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


        return builder.create();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(mCalendar.getTime());
        txtInputReservationDate.setText(selectedDate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        String selectedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(mCalendar.getTime());
        txtInputReservationTime.setText(selectedTime);
    }

}
