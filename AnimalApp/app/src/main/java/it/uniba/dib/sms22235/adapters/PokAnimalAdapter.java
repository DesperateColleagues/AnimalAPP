package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PokeLink;

public class PokAnimalAdapter extends RecyclerView.Adapter<PokAnimalAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PokeLink> pokeLinksList;
    private ViewHolder.OnItemClickListener listener;

    public void addPokeLink (PokeLink pokeLink) {
        pokeLinksList.add(pokeLink);
    }

    public PokAnimalAdapter () {
        pokeLinksList = new ArrayList<>();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fragment_passionate_pokanimal, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PokeLink pokeLink = pokeLinksList.get(position);

            Glide.with(context).load(pokeLink.getOtherAnimalUri()).into(holder.otherAnimal);
            Glide.with(context).load(pokeLink.getPassionateAnimalUri()).into(holder.passionateAnimal);
            holder.txtPokeLinkDescription.setText(pokeLink.getDescription());

    }

    public void setListener(ViewHolder.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return pokeLinksList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView passionateAnimal;
        ImageView otherAnimal;
        TextView txtPokeLinkDescription;
        CardView pokenimalCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            passionateAnimal = itemView.findViewById(R.id.passionateAnimal);
            otherAnimal = itemView.findViewById(R.id.otherAnimal);
            txtPokeLinkDescription = itemView.findViewById(R.id.txtPokeLinkDescription);
        }

        public interface OnItemClickListener {
            void onItemClick();
        }
    }

}
