package it.uniba.dib.sms22235.adapters.animals;

import android.annotation.SuppressLint;
import android.content.Context;
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
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class AnimalExamsAdapter extends RecyclerView.Adapter<AnimalExamsAdapter.ViewHolder> {
    private ArrayList<Exam> examsList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public AnimalExamsAdapter(){
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
    public AnimalExamsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_exam_single_ribbon_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalExamsAdapter.ViewHolder holder, int position) {
        Exam exam = examsList.get(position);

        holder.txtExamInfo.setText(String.format("%s - %s", exam.getType(), exam.getDateAdded()));

        if (exam.getOutcome().equals(KeysNamesUtils.ExamsFields.EXAM_PASS)) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
            holder.divider.setBackgroundColor(value.data);
        } else if (exam.getOutcome().equals(KeysNamesUtils.ExamsFields.EXAM_FAIL)) {
            holder.divider.setBackgroundColor(context.getResources().getColor(R.color.error_red));
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