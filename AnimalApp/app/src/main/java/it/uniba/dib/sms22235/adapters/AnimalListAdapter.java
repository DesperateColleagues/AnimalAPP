package it.uniba.dib.sms22235.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;

public class AnimalListAdapter extends RecyclerView.Adapter<AnimalListAdapter.ViewHolder> {
    ArrayList<Animal> animalList = new ArrayList<>();
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.animalName);

        }
    }

    @NonNull
    @Override
    public AnimalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_animal_single_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalListAdapter.ViewHolder holder, int position) {
        holder.name.setText(animalList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public AnimalListAdapter (ArrayList<Animal> animalList){
        this.animalList= animalList;
    }

}
