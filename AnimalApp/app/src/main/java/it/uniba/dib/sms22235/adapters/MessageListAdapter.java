package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.InfoMessage;

/**
 * Adapter to represent messages
 * */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder>{

    private final ArrayList<InfoMessage> infoMessages;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(InfoMessage message);
    }

    public MessageListAdapter(ArrayList<InfoMessage> infoMessages){
        this.infoMessages = infoMessages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtMessageLeftText;
        ImageView imgMessageRight;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtMessageLeftText = itemView.findViewById(R.id.txtMessageLeftText);
            imgMessageRight = itemView.findViewById(R.id.imgMessageRight);
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_passionate_message_single_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder holder, int position) {
        InfoMessage message = infoMessages.get(position);
        holder.txtMessageLeftText.setText(message.getLeftText());
        holder.imgMessageRight.setImageResource(message.getRightImage());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoMessages.size();
    }

    public InfoMessage getMessageAtPosition(int index) {
        return infoMessages.get(index);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
