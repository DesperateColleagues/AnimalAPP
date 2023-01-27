package it.uniba.dib.sms22235.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

public class VeterinarianArrayAdapter extends ArrayAdapter<Veterinarian> {

    private final List<Veterinarian> list;

    public VeterinarianArrayAdapter(@NonNull Context context, int resource, @NonNull List veterinarians) {
        super(context, resource, veterinarians);
        list = new ArrayList<>(veterinarians);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_fragment_spinner_element, parent, false
            );
        }

        TextView txtSpinnerText = convertView.findViewById(R.id.txtSpinnerText);

        txtSpinnerText.setText(
                new StringBuilder()
                        .append(list.get(position).getFullName())
                        .append(" - ")
                        .append(list.get(position).getClinicName()));

        return convertView;
    }
}
