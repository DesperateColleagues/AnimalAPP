package it.uniba.dib.sms22235.activities.veterinarian.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.fragments.VeterinaryReservationFragment;
import it.uniba.dib.sms22235.activities.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.activities.veterinarian.dialogs.DialogAddReservationFragment;
import it.uniba.dib.sms22235.entities.operations.Reservation;

public class VeterinarianReservationFragment extends Fragment implements DialogAddReservationFragment.
        DialogAddReservationFragmentListener {

    private CalendarView calendarView;
    private RecyclerView reservationRecyclerView;
    private DialogAddReservationFragment dialogAddReservationFragment;

    public interface VeterinarianReservationFragmentListener {
        void onReservationRegistered(Reservation reservation);
    }

    VeterinarianReservationFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        VeterinarianNavigationActivity activity = (VeterinarianNavigationActivity) getActivity();
        try {
            listener = (VeterinarianReservationFragment.VeterinarianReservationFragmentListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                    + "Must implement the interface");
        }

        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_veterinarian_reservation, container, false);
        calendarView = rootView.findViewById(R.id.calendarViewVeterinarian);
        reservationRecyclerView = rootView.findViewById(R.id.reservationList);

        ((VeterinarianNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            dialogAddReservationFragment = new DialogAddReservationFragment();
            dialogAddReservationFragment.setListener(this);
            dialogAddReservationFragment.show(getParentFragmentManager(), "DialogAddReservationFragment");
        });

        return rootView;

    }

    @Override
    public void onDialogAddReservationDismissed(Reservation reservation){
        String email = ((VeterinarianNavigationActivity) requireActivity()).getVeterinarianEmail();
        reservation.setVeterinarian(email);
        listener.onReservationRegistered(reservation);
    }
}
