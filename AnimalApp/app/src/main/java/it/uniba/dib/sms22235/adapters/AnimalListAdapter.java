package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
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
    ArrayList<Animal> animalList;

    public AnimalListAdapter (){
        animalList = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.txtAnimalName);

        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public AnimalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_animal_single_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalListAdapter.ViewHolder holder, int position) {
        holder.name.setText(animalList.get(position).getName());
    }

    public void addAnimal(Animal animal) {
        animalList.add(animal);
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public Animal getAnimalAtPosition(int index) {
        return animalList.get(index);
    }

}
