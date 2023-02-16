package it.uniba.dib.sms22235.adapters.commonoperations;

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
import it.uniba.dib.sms22235.entities.operations.Report;

/**
 * This adapter is used to manage the reports data and to give them a representation
 * */
public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Report> reportsList;
    private ViewHolder.OnItemClickListener onItemClickListener;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setReportsList(ArrayList<Report> reportsList) {
        this.reportsList = reportsList;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ReportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_reports_single_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Report report = reportsList.get(position);

        holder.txtReportTitle.setText(report.getReportTitle());
        holder.txtReportReporter.setText(report.getReporter());
        holder.txtReportAddress.setText(report.getReportAddress());

        if (report.getCompleted()) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
            holder.requestDividerStatus.setBackgroundColor(value.data);
        }

        holder.itemReportCardView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(report);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReportTitle;
        TextView txtReportReporter;
        TextView txtReportAddress;
        MaterialDivider requestDividerStatus;

        CardView itemReportCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtReportTitle = itemView.findViewById(R.id.txtReportTitle);
            txtReportReporter = itemView.findViewById(R.id.txtReportReporter);
            txtReportAddress = itemView.findViewById(R.id.txtReportAddress);
            requestDividerStatus = itemView.findViewById(R.id.requestDividerStatus);
            itemReportCardView = itemView.findViewById(R.id.itemReportCardView);
        }

        public interface OnItemClickListener {
            void onItemClick(Report report);
        }
    }

    public void setOnItemClickListener(ViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
