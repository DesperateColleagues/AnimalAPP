package it.uniba.dib.sms22235.activities.veterinarian.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.fragments.VeterinaryReservationFragment;
import it.uniba.dib.sms22235.activities.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.activities.veterinarian.dialogs.DialogAddReservationFragment;
import it.uniba.dib.sms22235.adapters.ListViewPurchasesAdapter;
import it.uniba.dib.sms22235.adapters.ReservationsAdapter;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

public class VeterinarianReservationFragment extends Fragment implements DialogAddReservationFragment.
        DialogAddReservationFragmentListener {

    private CalendarView calendarView;
    private RecyclerView reservationRecyclerView;
    private DialogAddReservationFragment dialogAddReservationFragment;
    private ArrayList<Reservation> dayReservationsList;
    private ReservationsAdapter adapter;
    private String currentDate;
    private String currentTime;

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
        return inflater.inflate(R.layout.fragment_veterinarian_reservation, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarViewVeterinarian);
        reservationRecyclerView = view.findViewById(R.id.reservationList);

        adapter = new ReservationsAdapter();

        // These methods allows to retrieve the current date and the current time. We need them to
        // specify if a reservation inside the recycler view is allowed to have a diagnose liked
        // to it
        SimpleDateFormat dateSDF = new SimpleDateFormat("dd/MM/yy", Locale.ITALY);
        currentDate = dateSDF.format(new Date());
        SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm", Locale.ITALY);
        currentTime = timeSDF.format(new Date());

        // Retrieving only today's reservations
        dayReservationsList = ((VeterinarianNavigationActivity) requireActivity()).getDayReservationsList(currentDate);

        // Adding them to the adapter and attaching the adapter to the RecyclerView
        adapter.addAllReservations(dayReservationsList);
        reservationRecyclerView.setAdapter(adapter);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            Toast.makeText(getContext(),"Build Date: "+buildDate(i, i1, i2),Toast.LENGTH_SHORT).show();

            // Retrieving only a specific day reservations
            dayReservationsList = ((VeterinarianNavigationActivity) requireActivity()).getDayReservationsList(buildDate(i, i1, i2));

            // Removing from the adapter the old reservations, adding the new ones to the adapter and attaching the adapter to the RecyclerView again
            adapter.clearAll();
            adapter.addAllReservations(dayReservationsList);
            reservationRecyclerView.setAdapter(adapter);
            reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        });

        // Event listener for the fab
        ((VeterinarianNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {

            // If fab is pressed, a dialog will appear that is used to add a new reservation
            dialogAddReservationFragment = new DialogAddReservationFragment();
            dialogAddReservationFragment.setListener(this);
            dialogAddReservationFragment.show(getParentFragmentManager(), "DialogAddReservationFragment");
        });

        // Recycler view on touch event listener
        // This allows every item in the recycler to do something when pressed
        /*reservationRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), reservationRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                // Obtaining from the adapter references to the specific reservation date and time
                String selectedDate = adapter.getReservationAtPosition(position).getDate();
                String selectedTime = adapter.getReservationAtPosition(position).getTime();

                // If the reservation happened before now, the vet would be able to upload a diagnosis
                if (checkIfDateDiagnosable(selectedDate, selectedTime)) {

                    Toast.makeText(VeterinarianReservationFragment.this.getContext(), "You are eligible to upload a diagnose for this reservation!", 0).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));*/

        reservationRecyclerView.setAdapter(adapter);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    // Basically a method to reconstruct a String from day, month and year expressed as Integers
    private String buildDate(int intYear, int intMonth, int intDay) {
        StringBuilder stringBuilder = new StringBuilder();
        if (intDay > 0 && intDay < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(intDay).append("/");
        intMonth++;
        if (intMonth > 0 && intMonth < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(intMonth).append("/");
        stringBuilder.append(new StringBuilder().append(intYear).substring(2));

        return stringBuilder.toString();
    }

    private boolean checkIfDateDiagnosable(String selectedDate, String selectedTime) {
        boolean isDateDiagnosable = true;

        String[] selectedDateArray = selectedDate.split("/");
        String[] currentDateArray = this.currentDate.split("/");

        if (selectedDateArray[2].compareTo(currentDateArray[2]) > 0) {
            isDateDiagnosable = false;
        } else if (selectedDateArray[1].compareTo(currentDateArray[1]) > 0) {
            isDateDiagnosable = false;
        } else if (selectedDateArray[0].compareTo(currentDateArray[0]) <= 0) {
            if (selectedDateArray[0].compareTo(currentDateArray[0]) == 0) {
                isDateDiagnosable = checkIfTimeDiagnosable(selectedTime);
            }
        } else {
            isDateDiagnosable = false;
        }
        return isDateDiagnosable;
    }

    private boolean checkIfTimeDiagnosable(String selected) {

        String[] selectedTimeArray = selected.split(":");
        String[] currentTimeArray = this.currentTime.split(":");

        if (selectedTimeArray[0].compareTo(currentTimeArray[0]) > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDialogAddReservationDismissed(Reservation reservation){
        String email = ((VeterinarianNavigationActivity) requireActivity()).getVeterinarianEmail();
        reservation.setVeterinarian(email);
        adapter.addReservation(reservation);
        adapter.notifyDataSetChanged();
        listener.onReservationRegistered(reservation);
    }
}
