package it.uniba.dib.sms22235.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.adapters.animals.AnimalPostAdapter;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

public class InterfacesOperationsHelper {

    protected FirebaseFirestore db;
    protected Context context;

    public InterfacesOperationsHelper(Context context){
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public static class AnimalHealthOperations {

        private final FirebaseFirestore db;
        private final Context context;

        public AnimalHealthOperations(Context context, FirebaseFirestore db){
            this.db = db;
            this.context = context;
        }

        public void registerDiagnosis(@NonNull Diagnosis diagnosis) {
            db.collection(KeysNamesUtils.CollectionsNames.DIAGNOSIS)
                    .document(diagnosis.getId())
                    .set(diagnosis)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context,
                                "Diagnosi inserita con successo",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context,
                                    "Errore interno, dati non aggiornati",
                                    Toast.LENGTH_SHORT).show()
                    );
        }

        public void registerExam(@NonNull Exam exam) {
            db.collection(KeysNamesUtils.CollectionsNames.EXAMS)
                    .document(exam.getId())
                    .set(exam)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context,
                                "Esame inserito con successo",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context,
                                    "Errore interno, dati non aggiornati",
                                    Toast.LENGTH_SHORT).show()
                    );
        }
    }

    public static class AnimalCommonOperations {

        private final FirebaseFirestore db;
        private final Context context;

        public AnimalCommonOperations(Context context, FirebaseFirestore db){
            this.db = db;
            this.context = context;
        }

        public boolean checkDate(String startDate, String endDate) {
            SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
            String currentDate = dateSDF.format(new Date());
            long distanceA = -1 , distanceB = -1;
            try {
                Date d1 = dateSDF.parse(startDate);
                Date d2 = dateSDF.parse(endDate);
                Date d3 = dateSDF.parse(currentDate);
                distanceA = d1.getTime() - d3.getTime();
                distanceB = d3.getTime() - d2.getTime();
            } catch (ParseException e) {
                return false;
            }
            return (distanceA <= 0 && distanceB <= 0);
        }

        public void checkIfAtHome(Animal animal, ImageView image) {
            SimpleDateFormat dateSDF = new SimpleDateFormat("dd/MM/yy", Locale.ITALY);
            String currentDate = dateSDF.format(new Date());

            db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                    .whereEqualTo("animal", animal.getMicrochipCode())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                List<DocumentSnapshot> residenceDocuments = querySnapshot.getDocuments();
                                for (DocumentSnapshot snapshot : residenceDocuments) {
                                    AnimalResidence residence = AnimalResidence.loadResidence(snapshot);
                                    long distanceA = -1 , distanceB = -1;
                                    try {
                                        Date d1 = dateSDF.parse(residence.getStartDate());
                                        Date d2 = dateSDF.parse(residence.getEndDate());
                                        Date d3 = dateSDF.parse(currentDate);
                                        distanceA = d1.getTime() - d3.getTime();
                                        distanceB = d3.getTime() - d2.getTime();
                                    } catch (ParseException ignored) {}
                                    if (distanceA < 0 && distanceB < 0) {
                                        image.setColorFilter(context.getResources().getColor(R.color.error_red));
                                        //image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_house_siding_24));
                                        break;
                                    }
                                }
                            }
                        }
                    });
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void loadProfilePic(String fileName, ImageView imageView) {
            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                    .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.FILE_NAME, fileName)
                    .addSnapshotListener((value, error) -> {

                        // Handle the error if the listening is not working
                        if (error != null) {
                            Log.w("Error listen", "listen:error", error);
                            return;
                        }

                        if (value != null) {

                            if (value.getDocumentChanges().size() > 0) {
                                // The profile image document collection can contain one document per animal
                                DocumentChange change = value.getDocumentChanges().get(0);

                                // Extract the post and load it with GLIDE
                                PhotoDiaryPost post = PhotoDiaryPost.loadPhotoDiaryPost(change.getDocument());
                                Glide.with(context).load(post.getPostUri()).into(imageView);
                            } else {
                                Glide.with(context).load(context.getDrawable(R.drawable.phd_circle)).into(imageView);
                            }
                        }

                    });
        }

        public void onProfilePicAdded(Uri source, String microchip, String userId) {
            String fileName = KeysNamesUtils.FileDirsNames.animalProfilePic(microchip);

            // Create the storage tree structure
            String fileReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(userId) +
                    "/" + fileName;

            // Get a reference of the storage by passing the tree structure
            StorageReference storageReference = FirebaseStorage.getInstance().getReference
                    (fileReference);

            // Give to the user a feedback to wait
            ProgressDialog progressDialog = new ProgressDialog(context, R.style.Widget_App_ProgressDialog);
            progressDialog.setMessage("Salvando l'immagine...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Start the upload task
            UploadTask uploadTask = storageReference.putFile(source);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    task.getResult()
                            .getStorage()
                            .getDownloadUrl().addOnCompleteListener(taskUri -> {
                                PhotoDiaryPost postProfileImage = new PhotoDiaryPost(taskUri.getResult().toString(), microchip);
                                postProfileImage.setFileName(fileName);

                                db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                        .document(KeysNamesUtils.FileDirsNames.animalProfilePic(microchip))
                                        .delete().addOnCompleteListener(taskDelete -> {
                                            // Useless to check if the task is successful. The following
                                            // query has to be executed in both cases

                                            // Save the post into the FireStore
                                            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                                    .document(KeysNamesUtils.FileDirsNames.animalProfilePic(microchip))
                                                    .set(postProfileImage)
                                                    .addOnSuccessListener(documentReference -> {
                                                        Toast.makeText(context,
                                                                "Immagine profilo caricata con successo", Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    });
                                        });

                            });
                }
            });
        }

        public void getAnimalDiagnosis(AnimalDiagnosisAdapter adapter, RecyclerView recyclerView, String animal, AnimalDiagnosisAdapter.OnItemClickListener listener){
            db.collection(KeysNamesUtils.CollectionsNames.DIAGNOSIS)
                    .whereEqualTo(KeysNamesUtils.DiagnosisFields.ANIMAL, animal)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()){
                                List<DocumentSnapshot> diagnosisDocuments = task.getResult().getDocuments();
                                for (DocumentSnapshot snapshot : diagnosisDocuments) {
                                    adapter.addDiagnosis(Diagnosis.loadDiagnosis(snapshot));
                                    Log.e("AnimalAPP - Diagnosi", "InterfacesOperationsHelper:240 - Diagnosi: " + Diagnosis.loadDiagnosis(snapshot));
                                }
                            }
                            adapter.setOnItemClickListener(listener);
                            recyclerView.setAdapter(adapter);

                        } else {
                            Toast.makeText(context, "Nessuna diagnosi presente.", Toast.LENGTH_SHORT).show(); // TODO image
                        }
                    });
        }

        public void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal, AnimalExamsAdapter.OnItemClickListener listener){
            db.collection(KeysNamesUtils.CollectionsNames.EXAMS)
                    .whereEqualTo(KeysNamesUtils.ExamsFields.EXAM_ANIMAL, animal)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()){
                                List<DocumentSnapshot> examsDocuments = task.getResult().getDocuments();
                                for (DocumentSnapshot snapshot : examsDocuments) {
                                    adapter.addExam(Exam.loadExam(snapshot));
                                    Log.e("AnimalAPP - Esami", "InterfacesOperationsHelper:263 - Esame: " + Exam.loadExam(snapshot));
                                }
                            }
                            adapter.setOnItemClickListener(listener);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(context, "Nessun esame presente.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @SuppressLint("NotifyDataSetChanged")
        public void loadPost(AnimalPostAdapter adapter, List<PhotoDiaryPost> postsList, String animal) {
            Log.d("ANIMAL", animal);
            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                    .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL, animal)
                    .addSnapshotListener((value, error) -> {

                        // Handle the error if the listening is not working
                        if (error != null) {
                            Log.w("Error listen", "listen:error", error);
                            return;
                        }

                        if (value != null && value.getDocumentChanges().size() > 0) {
                            // Check for every document
                            for (DocumentChange change : value.getDocumentChanges()) {
                                PhotoDiaryPost post = PhotoDiaryPost.loadPhotoDiaryPost(change.getDocument());

                                if (post.getFileName().equals(KeysNamesUtils.FileDirsNames.animalProfilePic(animal))) {
                                    continue;
                                }

                                switch (change.getType()) {
                                    case ADDED:
                                        postsList.add(post);
                                        break;
                                    case REMOVED:
                                        postsList.remove(post);
                                        break;
                                }

                            }

                            // Notify the changes on the adapter
                            Collections.reverse(postsList);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }

    }

    public static class AnimalOwnerOperations {

        private final FirebaseFirestore db;
        private final Context context;

        public AnimalOwnerOperations(Context context, FirebaseFirestore db) {
            this.db = db;
            this.context = context;
        }

        public void updateAnimal(@NonNull Animal animal) {

            String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                    + "_" + animal.getMicrochipCode();

            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, animal.getMicrochipCode())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                        .document(docKeyAnimal)
                                        .set(animal)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(context,
                                                    "Animale aggiornato con successo",
                                                    Toast.LENGTH_LONG).show();

                                            // Update the local animal's files
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Errore interno, dati non aggiornati",
                                                        Toast.LENGTH_SHORT).show());
                            }
                        }
                    });
        }

        public List<Veterinarian> getVeterinariansList(@NonNull List<Veterinarian> veterinariansList) {
            List<Veterinarian> clonedVeterinarianList = new ArrayList<>();

            for (Veterinarian veterinarian : veterinariansList) {
                clonedVeterinarianList.add((Veterinarian) veterinarian.clone());
            }

            return clonedVeterinarianList;
        }

        public void onAnimalUpdated(@NonNull Animal animal) { updateAnimal(animal); }

        public void onPostAdded(@NonNull PhotoDiaryPost post, String userId) {
            String fileName = System.currentTimeMillis() + "";

            post.setFileName(fileName);

            // Create the storage tree structure
            String fileReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(userId) +
                    "/" +
                    KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(post.getPostAnimal())
                    + "/" + fileName;

            // Get a reference of the storage by passing the tree structure
            StorageReference storageReference = FirebaseStorage.getInstance().getReference
                    (fileReference);

            // Give to the user a feedback to wait
            ProgressDialog progressDialog = new ProgressDialog(context,R.style.Widget_App_ProgressDialog);
            progressDialog.setMessage("Salvando l'immagine...");
            progressDialog.show();

            // Start the upload task
            UploadTask uploadTask = storageReference.putFile(Uri.parse(post.getPostUri()));

            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    task.getResult().getStorage()
                            .getDownloadUrl().addOnCompleteListener(taskUri -> {
                                // Set the download post URI
                                post.setPostUri(taskUri.getResult().toString());

                                // Save the post into the FireStore
                                db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                        .document(fileName)
                                        .set(post)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(context,
                                                    "Caricamento completato con successo", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        });
                            });
                }
            });
        }

    }
}
