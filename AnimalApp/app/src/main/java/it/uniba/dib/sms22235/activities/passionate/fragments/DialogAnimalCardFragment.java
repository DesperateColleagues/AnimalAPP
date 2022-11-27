package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;

public class DialogAnimalCardFragment extends DialogFragment {

    private Animal animal;
    private Uri photoUri;
    public static final int PICK_IMAGE = 1;
    private ImageView imgAnimalCardPhoto;
    private ImageView id2;
    private final String PATH = "/data/user/0/it.uniba.dib.sms22235/app_AnimalAPP_Images";

    public DialogAnimalCardFragment(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public Dialog onCreateDialog(@Nullable Bundle SavedInstanceBundle){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_animal_card, null);

        builder.setView(root);
        builder.setTitle("AnimalCard");


        TextView txtAnimalCardName = root.findViewById(R.id.txtAnimalCardName);
        TextView txtAnimalCardSpecies = root.findViewById(R.id.txtAnimalCardSpecies);
        TextView txtAnimalCardRace = root.findViewById(R.id.txtAnimalCardRace);
        TextView txtAnimalCardMicroChipCode = root.findViewById(R.id.txtAnimalCardMicrochipCode);
        TextView txtAnimalCardBirthDate = root.findViewById(R.id.txtAnimalCardBirthDate);

        txtAnimalCardName.setText(animal.getName());
        txtAnimalCardSpecies.setText(animal.getRace());
        txtAnimalCardRace.setText(animal.getRace());
        txtAnimalCardMicroChipCode.setText(animal.getMicrochipCode());
        txtAnimalCardBirthDate.setText(animal.getBirthDate());

        imgAnimalCardPhoto = root.findViewById(R.id.animalCardPhoto);

        Bitmap image = DataManipulationHelper.loadImageFromStorage(PATH,animal.getMicrochipCode()+".png");
        if(image != null) {
            imgAnimalCardPhoto.setImageBitmap(image);
        } else {
            imgAnimalCardPhoto.setImageResource(R.drawable.phd_circle);
        }

        imgAnimalCardPhoto.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        return builder.create();
    }

    ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImageUri);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference riversRef = storageRef.child("AnimalAPP_Images/"+selectedImageUri.getLastPathSegment());

                            UploadTask uploadTask = riversRef.putFile(selectedImageUri);

                            Toast.makeText(getContext(),R.string.impostazione_foto_animal_card, Toast.LENGTH_LONG).show();

                            uploadTask
                                    .addOnFailureListener(exception ->
                                        Toast.makeText(getContext(),"Errore, si è verificato un errore di rete",Toast.LENGTH_LONG).show())
                                    .addOnSuccessListener(taskSnapshot -> {
                                        imgAnimalCardPhoto.setImageBitmap(selectedImageBitmap);
                                        DataManipulationHelper.saveToInternalStorage(selectedImageBitmap,animal.getMicrochipCode()+".png", getContext());
                                    });
                        }
                        catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
        }
    }
}
