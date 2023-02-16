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
import it.uniba.dib.sms22235.entities.operations.Request;

/**
 * This adapter is used to manage requests data and to give them a representation
 * */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private ArrayList<Request> requestsList;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private ViewHolder currentHolder;

    public void setContext(Context context) {
        this.context = context;
    }

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

        holder.txtRequestType.setText(request.getRequestType());
        holder.txtRequestTitle.setText(request.getRequestTitle());
        holder.txtRequestOwner.setText(request.getUserEmail());

        if (request.getIsCompleted()) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorPrimary, value, true);
            holder.requestDividerStatus.setBackgroundColor(value.data);
        }

        currentHolder = holder;

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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRequestTitle;
        TextView txtRequestType;
        TextView txtRequestOperationType;
        TextView txtRequestOwner;
        MaterialDivider requestDividerStatus;
        CardView itemRequestCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRequestTitle = itemView.findViewById(R.id.txtRequestTitle);
            txtRequestType = itemView.findViewById(R.id.txtRequestType);
            txtRequestOperationType = itemView.findViewById(R.id.txtRequestOperationType);
            txtRequestOwner = itemView.findViewById(R.id.txtRequestOwner);
            requestDividerStatus = itemView.findViewById(R.id.requestDividerStatus);
            itemRequestCardView = itemView.findViewById(R.id.itemRequestCardView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Request request);
    }
}
