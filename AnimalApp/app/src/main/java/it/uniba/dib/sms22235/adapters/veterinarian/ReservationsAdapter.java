package it.uniba.dib.sms22235.adapters.veterinarian;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * Adapter to represent reservations
 * */
public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {
    private ArrayList<Reservation> reservationsList;
    private String currentDate;
    private String currentTime;
    private int listType;

    private OnItemClickListener onItemClickListener;

    public ReservationsAdapter (){
        reservationsList = new ArrayList<>();
    }

    public void remove(Reservation reservation) {
        reservationsList.remove(reservation);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtReservationInfo;
        CardView itemReservationCardView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtReservationInfo = itemView.findViewById(R.id.txtReservationInfo);
            itemReservationCardView = itemView.findViewById(R.id.itemReservationCardView);

        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ReservationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_reservation_single_ribbon_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservationsList.get(position);
        String res = reservation.getTime();
        if (listType == KeysNamesUtils.ReservationListType.VETERINARIAN.getValue()) {
            if (reservation.getOwner() != null && reservation.getAnimal() != null){
                res = res + " - " + reservation.getAnimal() + " - " + reservation.getOwner();
            }
            else if (reservation.getOwner() == null && reservation.getAnimal() == null){
                res = res + " - Non prenotato";
            } else {
                res = res + " - Stato appuntamento non definibile";
            }
        } else if (listType == KeysNamesUtils.ReservationListType.PASSIONATE.getValue()) {
            res = res + " - " + reservation.getVeterinarian();
        } else {
            res = reservation.toString();
        }
        holder.txtReservationInfo.setText(res);

        holder.itemReservationCardView.setOnClickListener(v -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(reservation);
            }
        });

    }

    public void setListType(int listType) {
        this.listType = listType;
    }

    public void addReservation(Reservation reservation) {
        reservationsList.add(reservation);
    }

    public void addAllReservations(ArrayList<Reservation> reservations) {
        reservationsList.addAll(reservations);
    }

    public void clearAll(){
        reservationsList.clear();
    }

    @Override
    public int getItemCount() {
        return reservationsList.size();
    }

    public Reservation getReservationAtPosition(int index) {
        return reservationsList.get(index);
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Reservation reservation);
    }
}