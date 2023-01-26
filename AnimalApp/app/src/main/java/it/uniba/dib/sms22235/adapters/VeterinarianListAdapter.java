package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

public class VeterinarianListAdapter extends RecyclerView.Adapter<VeterinarianListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Veterinarian> veterinarianList;

    public void setVeterinarianList(ArrayList<Veterinarian> veterinarianList) {
        this.veterinarianList = veterinarianList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_passionate_veterinarian_list_single_card, null));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtVeterinarianLocation.setText(veterinarianList.get(position).getClinicAddress());
        holder.txtVeterinarianName.setText(veterinarianList.get(position).getFullName() +
                " - " + veterinarianList.get(position).getClinicName());

        holder.btnContactEmailVeterinarian.setOnClickListener(v ->
                composeEmail(new String [] {veterinarianList.get(position).getEmail()}, "Contatto veterinario"));

        holder.btnCall.setOnClickListener(v -> {
            String phone = veterinarianList.get(position).getPhoneNumber();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            context.startActivity(intent);
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return veterinarianList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtVeterinarianName;
        TextView txtVeterinarianLocation;
        ImageButton btnCall;
        ImageButton btnContactEmailVeterinarian;
        CardView itemVeterinarianCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtVeterinarianName = itemView.findViewById(R.id.txtVeterinarianName);
            txtVeterinarianLocation = itemView.findViewById(R.id.txtVeterinarianLocation);

            btnCall = itemView.findViewById(R.id.btnCall);
            btnContactEmailVeterinarian = itemView.findViewById(R.id.btnContactEmailVeterinarian);

            itemVeterinarianCardView = itemView.findViewById(R.id.itemVeterinarianCardView);
        }
    }
}
