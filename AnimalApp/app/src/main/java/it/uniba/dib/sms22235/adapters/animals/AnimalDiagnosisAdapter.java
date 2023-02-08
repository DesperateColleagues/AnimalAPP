package it.uniba.dib.sms22235.adapters.animals;

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
import it.uniba.dib.sms22235.entities.operations.Diagnosis;

public class AnimalDiagnosisAdapter extends RecyclerView.Adapter<AnimalDiagnosisAdapter.ViewHolder> {
    private ArrayList<Diagnosis> diagnosisList;

    private OnItemClickListener onItemClickListener;

    public AnimalDiagnosisAdapter(){
        diagnosisList = new ArrayList<>();
    }

    public void remove(Diagnosis diagnosis) {
        diagnosisList.remove(diagnosis);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDiagnosisInfo;
        CardView itemDiagnosisCardView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtDiagnosisInfo = itemView.findViewById(R.id.txtDiagnosisInfo);
            itemDiagnosisCardView = itemView.findViewById(R.id.itemDiagnosisCardView);

        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public AnimalDiagnosisAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_diagnosis_single_ribbon_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalDiagnosisAdapter.ViewHolder holder, int position) {
        Diagnosis diagnosis = diagnosisList.get(position);

        holder.txtDiagnosisInfo.setText(String.format("%s - %s", diagnosis.getId(), diagnosis.getDateAdded()));

        holder.itemDiagnosisCardView.setOnClickListener(v -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(diagnosis);
            }
        });

    }

    public void addDiagnosis(Diagnosis diagnosis) {diagnosisList.add(diagnosis);}

    @Override
    public int getItemCount() {
        return diagnosisList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Diagnosis diagnosis);
    }
}