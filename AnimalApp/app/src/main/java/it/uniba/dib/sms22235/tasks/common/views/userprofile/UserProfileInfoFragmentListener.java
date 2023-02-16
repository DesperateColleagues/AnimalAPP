package it.uniba.dib.sms22235.tasks.common.views.userprofile;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms22235.R;

/**
 * This interface perform the operation of the user profile to display info
 * */
public interface UserProfileInfoFragmentListener {

    /**
     * This method will save the profile image of the user
     *
     * @param storage the reference of the storage
     * @param imageUri the image uri to be saved
     * @param userId the id the will identify the image
     * @param context the app context
     * */
    default void saveImageProfile(@NonNull FirebaseStorage storage,
                                  Uri imageUri, String userId, Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.Widget_App_ProgressDialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Salvando l'immagine...");
        progressDialog.show();

        String storagePath = "profile/" + "profile_" + userId;

        StorageReference storageReference = storage.getReference(storagePath);

        storageReference.putFile(imageUri).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            Toast.makeText(context, "Immagine profilo caricata con successo", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * This method will save the profile image of the user
     *
     * @param storage the reference of the storage
     * @param imageView the image view where to load the imagge
     * @param userId the id the will identify the image
     * @param context the app context
     * */
    default void loadImageProfile(@NonNull FirebaseStorage storage, ImageView imageView,
                                  String userId, Context context) {
        String storagePath = "profile/" + "profile_" + userId;
        StorageReference storageReference = storage.getReference(storagePath);

        storageReference.getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(context).load(uri).into(imageView));

    }


}
