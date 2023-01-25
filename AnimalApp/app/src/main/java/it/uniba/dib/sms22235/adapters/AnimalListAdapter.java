package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private final ArrayList<Animal> animalList;
    private final ArrayList<Bitmap> animalPic;
    protected final int orientation;
    private Context context;

    public AnimalListAdapter(int orientation) {
        animalList = new ArrayList<>();
        animalPic = new ArrayList<>();
        this.orientation = orientation;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtAnimalName;
        ImageView animalPicPreview;
        TextView txtAnimalOwner;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            txtAnimalName = itemView.findViewById(R.id.txtAnimalName);
            animalPicPreview = itemView.findViewById(R.id.animalPic);
            txtAnimalOwner = itemView.findViewById(R.id.txtAnimalOwner);
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public AnimalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (orientation == RecyclerView.HORIZONTAL) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_animal_single_square_card, null));
        } else { //orientation == RecyclerView.VERTICAL
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_animal_single_ribbon_card, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalListAdapter.ViewHolder holder, int position) {

        if(orientation == RecyclerView.HORIZONTAL) {
            holder.txtAnimalName.setText(animalList.get(position).getName());
            if (animalPic.get(position) != null) {
                holder.animalPicPreview.setVisibility(View.VISIBLE);
                holder.animalPicPreview.setImageBitmap(animalPic.get(position));
            }
        } else {
            holder.txtAnimalName.setText(
                    String.format("%s%s", context.getResources()
                            .getString(R.string.nome_animale_vet_list),
                            animalList.get(position).getName())
            );
            holder.txtAnimalOwner.setText(
                    String.format("%s%s", context.getResources()
                                    .getString(R.string.proprietario_animale_vet_list),
                            animalList.get(position).getOwner())
            );
        }

    }

    public void addAnimal(Animal animal) {
        animalList.add(animal);
    }

    public void addAllAnimals(ArrayList<Animal> list) {
        animalList.addAll(list);
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

    public Animal getAnimalByMicroChipCode(String microChipCode) {
        for (Animal animal : animalList) {
            if (animal.getMicrochipCode().equals(microChipCode)){
                return animal;
            }
        }
        return null;
    }

    public void remove(Animal animal) { animalList.remove(animal); }

}
