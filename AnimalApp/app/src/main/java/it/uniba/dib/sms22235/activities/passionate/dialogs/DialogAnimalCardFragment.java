package it.uniba.dib.sms22235.activities.passionate.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.io.IOException;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class DialogAnimalCardFragment extends DialogFragment {

    private Animal animal;
    private ImageView imgAnimalCardPhoto;

    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            // Get the selected image as a Bitmap
                            Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().getContentResolver(), selectedImageUri);

                            // Prepare the Firebase storage to save data in cloud
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference riversRef = storageRef.child(
                                    "AnimalAPP_Images/" + selectedImageUri.getLastPathSegment());

                            // Start the firebase tak
                            UploadTask uploadTask = riversRef.putFile(selectedImageUri);

                            Toast.makeText(getContext(),R.string.impostazione_foto_animal_card,
                                    Toast.LENGTH_LONG).show();

                            // Set the listeners of the upload task
                            uploadTask
                                    .addOnFailureListener(exception ->
                                            Toast.makeText(getContext(),
                                                    "Errore, si è verificato un errore di rete",
                                                    Toast.LENGTH_LONG).show())

                                    .addOnSuccessListener(taskSnapshot -> {
                                        imgAnimalCardPhoto.setImageBitmap(selectedImageBitmap);

                                        DataManipulationHelper.saveBitmapToInternalStorage(
                                                selectedImageBitmap,
                                                KeysNamesUtils.FileDirsNames.PROFILE_IMAGES,
                                                animal.getMicrochipCode() + ".png",
                                                getContext());
                                    });
                        }
                        catch (IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    public DialogAnimalCardFragment(Animal animal) {
        this.animal = animal;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle SavedInstanceBundle){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Create the inflater and inflate the layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_animal_card, null);

        builder.setView(root);
        builder.setTitle("AnimalCard");

        // Retrieve the input text views
        TextView txtAnimalCardName = root.findViewById(R.id.txtAnimalCardName);
        TextView txtAnimalCardSpecies = root.findViewById(R.id.txtAnimalCardSpecies);
        TextView txtAnimalCardRace = root.findViewById(R.id.txtAnimalCardRace);
        TextView txtAnimalCardMicroChipCode = root.findViewById(R.id.txtAnimalCardMicrochipCode);
        TextView txtAnimalCardBirthDate = root.findViewById(R.id.txtAnimalCardBirthDate);

        String microChipText = "Microchip" + animal.getMicrochipCode();

        // Set up Text views text
        txtAnimalCardName.setText(animal.getName());
        txtAnimalCardSpecies.setText(animal.getAnimalSpecies());
        txtAnimalCardRace.setText(animal.getRace());
        txtAnimalCardMicroChipCode.setText(microChipText);
        txtAnimalCardBirthDate.setText(animal.getBirthDate());

        imgAnimalCardPhoto = root.findViewById(R.id.animalCardPhoto);

        String path =
                KeysNamesUtils.FileDirsNames.BASE_PATH +
                KeysNamesUtils.FileDirsNames.ROOT_PREFIX +
                KeysNamesUtils.FileDirsNames.PROFILE_IMAGES;

        Bitmap image = DataManipulationHelper.loadBitmapFromStorage(path,
                animal.getMicrochipCode() + ".png");
        if(image != null) {
            imgAnimalCardPhoto.setImageBitmap(image);
        } else {
            imgAnimalCardPhoto.setImageResource(R.drawable.phd_circle);
        }

        // Set the listener that starts the intent to select a picture from the device
        imgAnimalCardPhoto.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            // Execute the async task to obtain intent result
            photoUploadAndSaveActivity.launch(i);
        });

        return builder.create();
    }

}
