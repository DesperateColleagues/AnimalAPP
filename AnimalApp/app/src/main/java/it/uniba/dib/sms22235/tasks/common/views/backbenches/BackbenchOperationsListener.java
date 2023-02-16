package it.uniba.dib.sms22235.tasks.common.views.backbenches;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Backbench;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This class is used to perform the operations that occur in Backbench management
 * */
public interface BackbenchOperationsListener {

    default void updateBackBenchDescription(String description,
                                            String ownerEmail,
                                            @NonNull FirebaseFirestore db,
                                            Context context) {

        db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                .whereEqualTo(KeysNamesUtils.BackbenchFields.OWNER, ownerEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Backbench backbench;

                    // Determines if the backbench has to be instantiated or retrieved by the document
                    if (queryDocumentSnapshots.size() > 0) {
                        backbench = Backbench.loadBackbench(queryDocumentSnapshots.getDocuments().get(0));
                    } else {
                        backbench = new Backbench(ownerEmail);
                    }

                    backbench.setDescription(description);
                    saveBackbench(db, backbench, ownerEmail, context);
                });
    }

    default void saveBackbench(@NonNull FirebaseFirestore db, Backbench backbench, String ownerEmail, Context context) {
        db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                .document(KeysNamesUtils.FileDirsNames.backBenchPic(ownerEmail))
                .set(backbench)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, context.getString(R.string.descrizione_stallo_inserita_successo),
                                Toast.LENGTH_SHORT).show());
    }

    default void updateBackbenchImage(String ownerEmail,
                                 @NonNull FirebaseFirestore db,
                                 FirebaseStorage storage,
                                 Uri uri,
                                 Context context) {
        db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                .whereEqualTo(KeysNamesUtils.BackbenchFields.OWNER, ownerEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Backbench backbench;

                    // Determines if the backbench has to be instantiated or retrieved by the document
                    if (queryDocumentSnapshots.size() > 0) {
                        backbench = Backbench.loadBackbench(queryDocumentSnapshots.getDocuments().get(0));
                    } else {
                        backbench = new Backbench(ownerEmail);
                    }

                    String fileName = KeysNamesUtils.FileDirsNames.backBenchPic(ownerEmail);

                    // Create the storage tree structure
                    String fileReference = KeysNamesUtils.FileDirsNames.BACKBENCH_POST +
                            "/" + fileName;

                    StorageReference storageReference = storage.getReference(fileReference);


                    // Give to the user a feedback to wait
                    ProgressDialog progressDialog = new ProgressDialog(context, R.style.Widget_App_ProgressDialog);
                    progressDialog.setMessage(context.getString(R.string.salvando_immagine));
                    progressDialog.show();

                    // Start the upload task
                    UploadTask uploadTask = storageReference.putFile(uri);
                    uploadTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            task.getResult().getStorage()
                                    .getDownloadUrl().addOnCompleteListener(taskUri -> {
                                        backbench.setDownloadableImage(taskUri.getResult().toString());
                                        db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                                                .document(KeysNamesUtils.FileDirsNames.backBenchPic(ownerEmail))
                                                .set(backbench)
                                                .addOnSuccessListener(unused -> {
                                                    Toast.makeText(context, context.getResources().getString(R.string.salvata_immagine), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                });
                                    });
                        }
                    });
                });

    }

    default void loadBackbenchInfo(@NonNull FirebaseFirestore db,
                                   String email,
                                   TextView txtBackbenchDescription,
                                   Button btnAddBackBenchDescription,
                                   Button btnAddBackBenchImage,
                                   ImageView imgBackbench,
                                   FragmentActivity fragmentActivity,
                                   Context context,
                                   boolean isAdded) {
        db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                .whereEqualTo(KeysNamesUtils.BackbenchFields.OWNER, email)
                .addSnapshotListener((value, error) -> {
                    // Handle the error if the listening is not working
                    if (error != null) {
                        Log.w("Error listen", "listen:error", error);
                        return;
                    }

                    if (value != null) {

                        if (value.getDocumentChanges().size() > 0) {
                            // The backbench image document collection can contain one document per owner
                            DocumentChange change = value.getDocumentChanges().get(0);

                            // Extract the post and load it with GLIDE
                            assignValueToBackbench(txtBackbenchDescription,
                                    btnAddBackBenchDescription,
                                    btnAddBackBenchImage,
                                    imgBackbench, isAdded, context, fragmentActivity, change);
                        }
                    }
                });
    }

    default void assignValueToBackbench(@NonNull TextView txtBackbenchDescription,
                                        Button btnAddBackBenchDescription,
                                        Button btnAddBackBenchImage,
                                        ImageView imgBackbench,
                                        boolean isAdded,
                                        Context context,
                                        FragmentActivity fragmentActivity,
                                        @NonNull DocumentChange documentChange
    ) {
        Backbench backbench = Backbench.loadBackbench(documentChange.getDocument());

        txtBackbenchDescription.setText(backbench.getDescription());

        if(!backbench.getDescription().equals("")) {
            btnAddBackBenchDescription.setText(context.getResources().getString(R.string.modifica));
        }

        if (isAdded) {
            Glide.with(fragmentActivity).load(backbench.getDownloadableImage()).into(imgBackbench);
            //Fragment BackBenchFragment not attached to a context.
            btnAddBackBenchImage.setText(context.getResources().getString(R.string.modifica_immagine_stallo));
        }
    }
}
