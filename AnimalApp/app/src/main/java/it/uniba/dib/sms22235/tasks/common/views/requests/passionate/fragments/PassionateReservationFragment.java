package it.uniba.dib.sms22235.tasks.common.views.requests.passionate.fragments;

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
import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.common.views.requests.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.common.views.requests.passionate.dialogs.DialogChooseAnimalFragment;
import it.uniba.dib.sms22235.adapters.ReservationsAdapter;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PassionateReservationFragment extends Fragment implements DialogChooseAnimalFragment.
        DialogChooseAnimalFragmentListener{

    private CalendarView calendarView;
    private RecyclerView reservationRecyclerView;
    private DialogChooseAnimalFragment dialogChooseAnimalFragment;
    private ArrayList<Reservation> dayReservationsList;
    private ReservationsAdapter adapter;
    private String currentDate;
    private String currentTime;
    private Reservation selectedReservation;


    public interface PassionateReservationFragmentListener{
        void onReservationBooked(Reservation reservation);
        List<Animal> getAnimalsByVeterinarian(String veterinarian);
    }

    PassionateReservationFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();
        try {
            listener = (PassionateReservationFragment.PassionateReservationFragmentListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_passionate_calendar_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarViewPassionate);
        reservationRecyclerView = view.findViewById(R.id.reservationListPassionate);

        adapter = new ReservationsAdapter();
        adapter.setListType(KeysNamesUtils.ReservationListType.PASSIONATE.getValue());
        adapter.setOnItemClickListener(reservation -> {

            this.selectedReservation = reservation;

            dialogChooseAnimalFragment = new DialogChooseAnimalFragment(listener.getAnimalsByVeterinarian(reservation.getVeterinarian()));
            dialogChooseAnimalFragment.setListener(this);
            dialogChooseAnimalFragment.show(getParentFragmentManager(), "DialogChooseAnimalFragment");

        });

        SimpleDateFormat dateSDF = new SimpleDateFormat("dd/MM/yy", Locale.ITALY);
        currentDate = dateSDF.format(new Date());
        SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm", Locale.ITALY);
        currentTime = timeSDF.format(new Date());

        dayReservationsList = ((PassionateNavigationActivity) requireActivity()).getAvailableReservationsList(currentDate);

        adapter.addAllReservations(dayReservationsList);

        reservationRecyclerView.setAdapter(adapter);
        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {

            // Retrieving only a specific day reservations
            dayReservationsList = ((PassionateNavigationActivity) requireActivity()).getAvailableReservationsList(buildDate(i, i1, i2));

            // Removing from the adapter the old reservations, adding the new ones to the adapter and attaching the adapter to the RecyclerView again
            adapter.clearAll();
            adapter.addAllReservations(dayReservationsList);
            reservationRecyclerView.setAdapter(adapter);
            reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        });
        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.INVISIBLE);
        ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {});

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


    @Override
    public void onDialogChoosedAnimal(String selectedAnimal) {
        this.selectedReservation.setAnimal(selectedAnimal);
        adapter.remove(selectedReservation);
        listener.onReservationBooked(selectedReservation);
        adapter.notifyDataSetChanged();
        dialogChooseAnimalFragment.dismiss();
    }
}