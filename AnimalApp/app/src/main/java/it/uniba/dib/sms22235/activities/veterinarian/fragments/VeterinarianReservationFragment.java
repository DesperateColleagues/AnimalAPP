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
import it.uniba.dib.sms22235.activities.veterinarian.VeterinarianNavigationActivity;

public class VeterinarianReservationFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView reservationRecyclerView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_veterinarian_reservation, container, false);
        calendarView = rootView.findViewById(R.id.calendarViewVeterinarian);
        reservationRecyclerView = rootView.findViewById(R.id.reservationList);


        return rootView;

    }
}
