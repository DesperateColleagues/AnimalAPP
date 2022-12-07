package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.entities.users.Animal;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {
    private ArrayList<Reservation> reservationsList;

    public ReservationsAdapter (){
        reservationsList = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtReservationInfo;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtReservationInfo = itemView.findViewById(R.id.txtReservationInfo);
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ReservationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_reservation_single_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ViewHolder holder, int position) {
        Reservation reservation = reservationsList.get(position);
        String res = reservation.getDate() + " " + reservation.getTime();
        holder.txtReservationInfo.setText(res);

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

}
