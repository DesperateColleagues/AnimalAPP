package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;

import it.uniba.dib.sms22235.entities.users.Animal;

public class DialogAnimalCardFragment extends DialogFragment {

    private final Animal animal;
    private AnimalProfile.AnimalProfileListener animalProfileListener;

    public DialogAnimalCardFragment(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle SavedInstanceBundle){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(),
                R.style.AnimalCardRoundedDialog);

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_animal_card, null);

        builder.setView(root);

        // Set dialog title
        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("AnimalCard - Preview");
        builder.setCustomTitle(titleView);

        // Retrieve the input text views
        TextView txtAnimalCardName = root.findViewById(R.id.txtAnimalCardName);
        TextView txtAnimalCardSpecies = root.findViewById(R.id.txtAnimalCardSpecies);
        TextView txtAnimalCardRace = root.findViewById(R.id.txtAnimalCardRace);
        TextView txtAnimalCardMicroChipCode = root.findViewById(R.id.txtAnimalCardMicrochipCode);
        TextView txtAnimalCardBirthDate = root.findViewById(R.id.txtAnimalCardBirthDate);

        String microChipText = "Microchip: " + animal.getMicrochipCode();

        // Set up Text views text
        txtAnimalCardName.setText(animal.getName());
        txtAnimalCardSpecies.setText(animal.getAnimalSpecies());
        txtAnimalCardRace.setText(animal.getRace());
        txtAnimalCardMicroChipCode.setText(microChipText);
        txtAnimalCardBirthDate.setText(animal.getBirthDate());

        ImageView imgAnimalCardPhoto = root.findViewById(R.id.animalCardProfileSend);
        animalProfileListener.loadProfilePic(animal.getMicrochipCode(), imgAnimalCardPhoto);

        Button shareButton = root.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> {

            Bitmap bitmap = generateSharePic(root.findViewById(R.id.drawableView));
            String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(),
                bitmap, UUID.randomUUID().toString(), null);
            Uri imageUri =  Uri.parse(path);

            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("image/jpeg");
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            try {
                startActivity(Intent.createChooser(whatsappIntent, "Select"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(requireContext(), "Whatsapp non installato", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    private Bitmap generateSharePic(@NonNull View view) {
        Bitmap viewBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(viewBitmap);

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }
        else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }

        // Render the view on the created canvas
        view.draw(canvas);

        return viewBitmap;
    }

    public void setAnimalProfileListener(AnimalProfile.AnimalProfileListener animalProfileListener) {
        this.animalProfileListener = animalProfileListener;
    }

}
