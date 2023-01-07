package it.uniba.dib.sms22235.adapters;

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
import it.uniba.dib.sms22235.entities.operations.Request;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private ArrayList<Request> requestsList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_request_single_card, null));
    }

    public void setRequestsList(ArrayList<Request> requestsList) {
        this.requestsList = requestsList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request request = requestsList.get(position);

        holder.txtRequestOperationType.setText(request.getOperationType());
        holder.txtRequestType.setText(request.getRequestType());
        holder.txtRequestBody.setText(request.getRequestBody());
        holder.txtRequestTitle.setText(request.getRequestTitle());

        holder.itemRequestCardView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRequestTitle;
        TextView txtRequestBody;
        TextView txtRequestType;
        TextView txtRequestOperationType;
        CardView itemRequestCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRequestTitle = itemView.findViewById(R.id.txtRequestTitle);
            txtRequestBody = itemView.findViewById(R.id.txtRequestBody);
            txtRequestType = itemView.findViewById(R.id.txtRequestType);
            txtRequestOperationType = itemView.findViewById(R.id.txtRequestOperationType);
            itemRequestCardView = itemView.findViewById(R.id.itemRequestCardView);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(Request request);
    }
}
