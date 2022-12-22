package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import it.uniba.dib.sms22235.entities.users.Animal;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder>{

    private ArrayList<InfoMessage> infoMessages = new ArrayList<>();

    public MessageListAdapter(ArrayList<InfoMessage> infoMessages){
        this.infoMessages = infoMessages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_passionate_dash_single_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageListAdapter.ViewHolder holder, int position) {
        InfoMessage message = infoMessages.get(position);
        if(message.getLeftText() != 0) {
            holder.txtMessageLeftText.setText(message.getLeftText());
        }
        if(message.getRightImage() != 0) {
            holder.imgMessageRight.setImageResource(message.getRightImage());
        }
    }

    public void addMessage(InfoMessage message) {
        infoMessages.add(message);
    }

    @Override
    public int getItemCount() {
        return infoMessages.size();
    }

}
