package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Reservation;

public class DialogReservationDetailsFragment extends DialogFragment {

    private Reservation reservation;

    public DialogReservationDetailsFragment(Reservation reservation) {
        this.reservation = reservation;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public Dialog onCreateDialog(@Nullable Bundle SavedInstanceBundle){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(),
                R.style.AnimalCardRoundedDialog);

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_reservation_details, null);

        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Dettagli appuntamento");
        builder.setCustomTitle(titleView);

        TextView txtReservationAnimal = root.findViewById(R.id.txtReservationAnimal);
        TextView txtReservationVeterinarian = root.findViewById(R.id.txtReservationVeterinarian);
        TextView txtReservationDateTime = root.findViewById(R.id.txtReservationDateTime);

        txtReservationAnimal.setText(reservation.getAnimal());

        txtReservationVeterinarian.setText(reservation.getVeterinarian());
        txtReservationDateTime.setText(
                new StringBuilder()
                        .append(reservation.getDate())
                        .append(" - ")
                        .append(reservation.getTime()));

        return builder.create();
    }
}
