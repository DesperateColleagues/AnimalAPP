package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.entities.operations.Request;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Report> reportsList;

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
        // todo implement bind
    }

    @Override
    public int getItemCount() {
        return reportsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReportTitle;
        TextView txtReportReporter;

        CardView itemReportCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReportTitle = itemView.findViewById(R.id.txtReportTitle);
            txtReportReporter = itemView.findViewById(R.id.txtReportReporter);

            itemReportCardView = itemView.findViewById(R.id.itemReportCardView);
        }

        public interface OnItemClickListener {
            void onItemClick(Request request);
        }
    }


}
