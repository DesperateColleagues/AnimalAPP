package it.uniba.dib.sms22235.tasks.common.dialogs.requests;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.Request;

/**
 * This dialog is used to set the temporary residence of an animal
 * in backbenches' requests
 * */
public class DialogRequestBackbench extends DialogFragment {

    /**
     * This interface perform actions that occur when the temporary residence is added
     * */
    public interface DialogRequestBackbenchListener {
        /**
         * This method is called when a new temporary residence is added
         *
         * @param residence the new residence
         * @param request the request
         * */
        void onValueAdded(AnimalResidence residence, Request request);
    }

    private DialogRequestBackbenchListener listener;
    private String emailOwner;
    private Request request;

    public DialogRequestBackbench(Request request) {
        this.request = request;
    }

    public void setListener(DialogRequestBackbenchListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogTheme);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_request_backbench, null);

        // Set dialog title
        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(getResources().getString(R.string.completa_richiesta));
        builder.setCustomTitle(titleView);
        builder.setView(root);

        EditText txtInputDateIntervalReq =  root.findViewById(R.id.txtInputDateIntervalReq);
        txtInputDateIntervalReq.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builderDate = MaterialDatePicker.Builder.dateRangePicker();
            MaterialDatePicker<Pair<Long, Long>> materialDatePicker = builderDate.build();

            Calendar calendar = Calendar.getInstance();
            // Set predefined values
            builderDate.setSelection(new Pair<>(calendar.getTimeInMillis(), calendar.getTimeInMillis()));
            materialDatePicker.show(getParentFragmentManager(), "MaterialDatePicker");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date dateFrom = new Date(selection.first);
                Date dateTo = new Date(selection.second);

                String dateInterval = format.format(dateFrom) + " a " + format.format(dateTo);
                txtInputDateIntervalReq.setText(dateInterval);
            });
        });

        builder.setPositiveButton(getResources().getString(R.string.conferma), (dialog, which) -> {
            String value = ((EditText) root.findViewById(R.id.txtAddInfoToRequest)).getText().toString();
            String dateInterval = txtInputDateIntervalReq.getText().toString();

            if (!value.equals("") && !dateInterval.equals("")) {
                String startDate = dateInterval.split(" a ")[0];
                String endDate = dateInterval.split(" a ")[1];

                AnimalResidence residence = new AnimalResidence(startDate, endDate, request.getUserEmail(), "true");
                residence.setAnimal(value);
                listener.onValueAdded(residence, request);
                dismiss();
            }
        });

        return builder.create();
    }
}
