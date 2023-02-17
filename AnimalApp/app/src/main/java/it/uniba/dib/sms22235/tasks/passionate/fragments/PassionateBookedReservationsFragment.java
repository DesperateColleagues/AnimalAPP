package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogReservationDetailsFragment;
import it.uniba.dib.sms22235.adapters.veterinarian.ReservationsAdapter;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment shows all the passionate's booked reservations
 * */
public class PassionateBookedReservationsFragment extends Fragment {

    private RecyclerView reservationRecyclerView;
    private ReservationsAdapter adapter;
    private ArrayList<Reservation> passionateReservationsList;
    private ArrayList<Reservation> currentReservationsList;
    private int currentType = ResType.PAST.getValue();
    private int lastType = ResType.FUTURE.getValue();

    private enum ResType {
        PAST(0), FUTURE(1);
        private final int value;

        ResType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);
        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);
        Bundle bundle = requireActivity().getIntent().getExtras();
        if (bundle != null) {
            passionateReservationsList = (ArrayList<Reservation>) bundle.getSerializable(KeysNamesUtils.BundleKeys.PASSIONATE_RESERVATIONS);
        }

        currentReservationsList = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_passionate_booked_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reservationRecyclerView = view.findViewById(R.id.passionateBookedReservationsList);
        adapter = new ReservationsAdapter();
        adapter.setListType(KeysNamesUtils.ReservationListType.PASSIONATE.getValue());

        recreateList();
        updateState();

        adapter.addAllReservations(currentReservationsList);
        reservationRecyclerView.setAdapter(adapter);

        reservationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        Button btnReservationType = view.findViewById(R.id.passionateBookedReservationsButton);
        btnReservationType.setOnClickListener(view1 -> {
            recreateList();
            adapter.clearAll();
            adapter.addAllReservations(currentReservationsList);
            reservationRecyclerView.setAdapter(adapter);
            if (currentType == ResType.FUTURE.getValue()) {
                updateState();
                btnReservationType.setText(R.string.appuntamenti_passati);
            } else if (currentType == ResType.PAST.getValue()) {
                updateState();
                btnReservationType.setText(R.string.appuntamenti_futuri);
            } else {
                Toast.makeText(getContext(), requireContext().getResources().getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemClickListener(reservation -> {
            DialogReservationDetailsFragment dialogReservationDetailsFragment = new DialogReservationDetailsFragment(reservation);
            dialogReservationDetailsFragment.show(getParentFragmentManager(), "DialogReservationDetailsFragment");
        });
    }

    /* This method is used to recreate the list inside the adapter. It will be emptied on every
       btnReservationType click and then repopulated.
    * */
    private void recreateList() {
        currentReservationsList.clear();
        // getting current time in millis
        long currentDate = ZonedDateTime.now().toInstant().toEpochMilli();

        if (lastType == ResType.FUTURE.getValue()) {
            for (Reservation reservation : passionateReservationsList) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                LocalDate date = LocalDate.parse(reservation.getDate(), formatter);
                long reservationDate = TimeUnit.DAYS.toMillis(date.toEpochDay());
                //Log.e("AnimalAPP - Prenotazione", "Numero: " + i + "\nC: " + currentDate + "\nR: " + reservationDate + "\nData prenotazione: " + reservation.getDate());
                if(currentDate < reservationDate) {
                    currentReservationsList.add(reservation);
                }
            }
        } else if (lastType == ResType.PAST.getValue()) {
            for (Reservation reservation : passionateReservationsList) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
                LocalDate date = LocalDate.parse(reservation.getDate(), formatter);
                long reservationDate = TimeUnit.DAYS.toMillis(date.toEpochDay());
                //Log.e("AnimalAPP - Prenotazione", "Numero: " + i + "\nC: " + currentDate + "\nR: " + reservationDate + "\nData prenotazione: " + reservation.getDate());
                if(currentDate > reservationDate) {
                    currentReservationsList.add(reservation);
                }
            }
        }
    }

    /* This small method is used to keep track of the current state of btnReservationType and the
       RecyclerView. This allows to display the upcoming reservations or the last reservations
    * */
    private void updateState() {
        int temp = currentType;
        currentType = lastType;
        lastType = temp;

    }
}
