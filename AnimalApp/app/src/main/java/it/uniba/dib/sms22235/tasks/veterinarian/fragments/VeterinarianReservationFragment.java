package it.uniba.dib.sms22235.tasks.veterinarian.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddDiagnosisFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddReservationFragment;
import it.uniba.dib.sms22235.adapters.veterinarian.ReservationsAdapter;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * Fragment used to add a new reservation
 * */
public class VeterinarianReservationFragment extends Fragment implements
        DialogAddReservationFragment.DialogAddReservationFragmentListener {

    private CalendarView calendarView;
    private RecyclerView reservationRecyclerView;
    private DialogAddReservationFragment dialogAddReservationFragment;
    private DialogAddDiagnosisFragment dialogAddDiagnosisFragment;
    private ArrayList<Reservation> dayReservationsList;
    private ReservationsAdapter adapter;
    private Reservation reservation;
    private String currentDate;
    private String selectedDate;
    private String currentTime;


    /**
     * Operations of the fragment
     * */
    public interface VeterinarianReservationFragmentListener {
        /**
         * Adds a new reservation
         *
         * @param reservation reservation
         * */
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
        adapter.setListType(KeysNamesUtils.ReservationListType.VETERINARIAN.getValue());

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
            selectedDate = buildDate(i, i1, i2);
            // Retrieving only a specific day reservations
            dayReservationsList = ((VeterinarianNavigationActivity) requireActivity()).getDayReservationsList(selectedDate);
            // Removing from the adapter the old reservations, adding the new ones to the adapter and attaching the adapter to the RecyclerView again
            adapter.clearAll();
            adapter.addAllReservations(dayReservationsList);
            reservationRecyclerView.setAdapter(adapter);
            reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        });

        ((VeterinarianNavigationActivity) requireActivity()).getFab().setVisibility(View.VISIBLE);
        // Event listener for the fab
        ((VeterinarianNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {

            // If fab is pressed, a dialog will appear that is used to add a new reservation
            dialogAddReservationFragment = new DialogAddReservationFragment();
            dialogAddReservationFragment.setListener(this);
            dialogAddReservationFragment.show(getParentFragmentManager(), "DialogAddReservationFragment");
        });

        reservationRecyclerView.setAdapter(adapter);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    // Basically a method to reconstruct a String from day, month and year expressed as Integers
    @NonNull
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

    private boolean checkIfDateDiagnosable(@NonNull String selectedDate, String selectedTime) {
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

    private boolean checkIfTimeDiagnosable(@NonNull String selected) {

        String[] selectedTimeArray = selected.split(":");
        String[] currentTimeArray = this.currentTime.split(":");

        return selectedTimeArray[0].compareTo(currentTimeArray[0]) <= 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogAddReservationDismissed(@NonNull Reservation reservation){
        String email = ((VeterinarianNavigationActivity) requireActivity()).getUserId();
        reservation.setVeterinarian(email);
        if(reservation.getDate().equals(selectedDate)){
            adapter.addReservation(reservation);
        }
        adapter.notifyDataSetChanged();
        listener.onReservationRegistered(reservation);
    }
}
