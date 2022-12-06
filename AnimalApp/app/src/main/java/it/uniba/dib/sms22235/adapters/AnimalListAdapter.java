package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;

public class AnimalListAdapter extends RecyclerView.Adapter<AnimalListAdapter.ViewHolder> {
    private ArrayList<Animal> animalList;
    private ArrayList<Bitmap> animalPic;

    public AnimalListAdapter (){
        animalList = new ArrayList<>();
        animalPic = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtAnimalName;
        ImageView animalPicPreview;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtAnimalName = itemView.findViewById(R.id.txtAnimalName);
            animalPicPreview = itemView.findViewById(R.id.animalPicPreview);
        }
    }

    /*public void setAnimalList(ArrayList<Animal> animalList) {
        this.animalList = animalList;
    }*/

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public AnimalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_animal_single_card, null));
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalListAdapter.ViewHolder holder, int position) {
        holder.txtAnimalName.setText(animalList.get(position).getName());

        if (animalPic.get(position) != null) {
            holder.animalPicPreview.setVisibility(View.VISIBLE);
            holder.animalPicPreview.setImageBitmap(animalPic.get(position));
        }

    }

    public void addAnimal(Animal animal) {
        animalList.add(animal);
    }

    public void addPic(Bitmap bitmap) {
        animalPic.add(bitmap);
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public Animal getAnimalAtPosition(int index) {
        return animalList.get(index);
    }

}
