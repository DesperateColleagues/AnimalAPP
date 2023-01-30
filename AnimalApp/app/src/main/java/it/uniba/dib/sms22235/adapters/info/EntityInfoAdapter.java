package it.uniba.dib.sms22235.adapters.info;

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
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class EntityInfoAdapter extends RecyclerView.Adapter<EntityInfoAdapter.ViewHolder> {

    private Context context;

    private ArrayList<Veterinarian> veterinarianList;
    private ArrayList<Organization> organizationList;

    private int SIZE;

    public enum AdapterMode {
        VET, ORG
    }

    private final AdapterMode adapterMode;

    public EntityInfoAdapter(AdapterMode adapterMode) {
        this.adapterMode = adapterMode;
    }

    public void setVeterinarianList(@NonNull ArrayList<Veterinarian> veterinarianList) {
        if (organizationList != null) {
            organizationList.clear();
        }

        this.veterinarianList = veterinarianList;

        SIZE = veterinarianList.size();
    }

    public void setOrganizationList(ArrayList<Organization> organizationList) {
        if (veterinarianList != null) {
            veterinarianList.clear();
        }

        this.organizationList = organizationList;

        SIZE = organizationList.size();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_fragment_passionate_veterinarian_list_single_card, null));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (adapterMode == AdapterMode.VET) {
            Veterinarian veterinarian = veterinarianList.get(position);

            holder.txtLocation.setText(veterinarian.getClinicAddress());
            holder.txtName.setText(veterinarian.getFullName() +
                    " - " + veterinarian.getClinicName());

            holder.btnContactEmail.setOnClickListener(v ->
                    composeEmail(new String[]{veterinarian.getEmail()}));

            holder.btnCall.setOnClickListener(v -> {
                String phone = veterinarian.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            });
        } else if (adapterMode == AdapterMode.ORG) {
            Organization organization = organizationList.get(position);

            holder.txtLocation.setText(organization.getOrgAddress());

            String purpose = "Ente pubblico";

            if (organization.getPurpose().equals(KeysNamesUtils.RolesNames.PRIVATE_ORGANIZATION)) {
                purpose = "Ente privato";
            }

            holder.txtName.setText(purpose +
                    " - " + organization.getOrgName());

            holder.btnContactEmail.setOnClickListener(v ->
                    composeEmail(new String[]{organization.getEmail()}));

            holder.btnCall.setOnClickListener(v -> {
                String phone = organization.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            });
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void composeEmail(String[] addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contatto veterinario");

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return SIZE;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtLocation;
        ImageButton btnCall;
        ImageButton btnContactEmail;
        CardView itemCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtLocation = itemView.findViewById(R.id.txtLocation);

            btnCall = itemView.findViewById(R.id.btnCall);
            btnContactEmail = itemView.findViewById(R.id.btnContactEmail);

            itemCardView = itemView.findViewById(R.id.itemCardView);
        }
    }
}
