package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDivider;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.entities.operations.Reservation;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class ExamsAdapter extends RecyclerView.Adapter<ExamsAdapter.ViewHolder> {
    private ArrayList<Exam> examsList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public ExamsAdapter (){
        examsList = new ArrayList<>();
    }

    public void remove(Exam exam) {
        examsList.remove(exam);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtExamInfo;
        CardView itemExamCardView;
        MaterialDivider divider;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtExamInfo = itemView.findViewById(R.id.txtExamInfo);
            itemExamCardView = itemView.findViewById(R.id.itemExamCardView);
            divider = itemView.findViewById(R.id.divider);

        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ExamsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_exam_single_ribbon_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ExamsAdapter.ViewHolder holder, int position) {
        Exam exam = examsList.get(position);

        holder.txtExamInfo.setText(
                new StringBuilder()
                        .append(exam.getAnimal())
                        .append(" - ")
                        .append(exam.getType())
                        .append(" - ")
                        .append(exam.getOutcome())
                        .toString()
        );

        if (exam.getOutcome().equals("PASS")) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
            holder.divider.setBackgroundColor(value.data);
        } else if (exam.getOutcome().equals("FAIL")) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.color.holo_red_dark, value, true);
            holder.divider.setBackgroundColor(value.data);
        }

        holder.itemExamCardView.setOnClickListener(v -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(exam);
            }
        });

    }

    public void addExam(Exam exam) {examsList.add(exam);}

    @Override
    public int getItemCount() {
        return examsList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Exam exam);
    }
}