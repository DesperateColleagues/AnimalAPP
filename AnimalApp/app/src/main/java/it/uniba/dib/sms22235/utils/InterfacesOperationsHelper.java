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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.DiagnosisAdapter;
import it.uniba.dib.sms22235.adapters.ExamsAdapter;
import it.uniba.dib.sms22235.adapters.PostGridAdapter;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import androidx.fragment.app.FragmentManager;

public class InterfacesOperationsHelper {

    protected FirebaseFirestore db;
    protected Context context;

    public InterfacesOperationsHelper(Context context){
        db = FirebaseFirestore.getInstance();
        this.context = context;
    }

    public void registerDiagnosis(Diagnosis diagnosis) {
        Log.wtf("WTF",diagnosis.getId());
        Log.wtf("WTF",diagnosis.getDescription());
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

    public class AnimalCommonOperations {

        private FirebaseFirestore db;
        private Context context;

        public AnimalCommonOperations(Context context, FirebaseFirestore db){
            this.db = db;
            this.context = context;
        }

        public void checkIfAtHome(Animal animal, ImageView image) {/*
        db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                .whereEqualTo("animal", animal.getMicrochipCode())
                .whereEqualTo("date", "currentdate")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()){
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_house_siding_24));
                        } else {
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_home_24));
                        }
                    }
                });*/
        }

        public void loadProfilePic(String microchip, ImageView imageView) {
            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY_PROFILE)
                    .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL, microchip)
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

                                db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY_PROFILE)
                                        .document(KeysNamesUtils.FileDirsNames.animalProfilePic(microchip))
                                        .delete().addOnCompleteListener(taskDelete -> {
                                            // Useless to check if the task is successful. The following
                                            // query has to be executed in both cases

                                            // Save the post into the FireStore
                                            db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY_PROFILE)
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

        public void getAnimalDiagnosis(DiagnosisAdapter adapter, RecyclerView recyclerView, String animal, FragmentManager fm){
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
                                    Log.wtf("Diagnosi", Diagnosis.loadDiagnosis(snapshot).toString());
                                }
                            }
                            recyclerView.setAdapter(adapter);

                            adapter.setOnItemClickListener(diagnosis -> {
                                String info = "• <b>" +
                                        "Animale" +
                                        ": </b>"+
                                        diagnosis.getAnimal() +
                                        "\n<br>" +
                                        "• <b>" +
                                        "Descrizione" +
                                        ": </b>"+
                                        diagnosis.getDescription() +
                                        "\n<br>" +
                                        "• <b>" +
                                        "Data aggiunta al sistema" +
                                        ": </b>" +
                                        diagnosis.getDateAdded();
                                DialogEntityDetailsFragment entityDetailsFragment = new DialogEntityDetailsFragment(info);
                                entityDetailsFragment.show(fm, "DialogEntityDetailsFragment");
                            });
                        } else {
                            Toast.makeText(context, "Nessuna diagnosi presente.", Toast.LENGTH_SHORT).show(); // TODO image
                        }
                    });
        }

        public void getAnimalExams(ExamsAdapter adapter, RecyclerView recyclerView, String animal, FragmentManager fm){
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
                                    Log.wtf("Esami", Exam.loadExam(snapshot).toString());
                                }
                            }
                            recyclerView.setAdapter(adapter);

                            adapter.setOnItemClickListener(exam -> {
                                String info = "• <b>" +
                                        "Animale" +
                                        ": </b>"+
                                        exam.getAnimal() +
                                        "\n<br>" +
                                        "• <b>" +
                                        "Esito" +
                                        ": </b>"+
                                        exam.getOutcome() +
                                        "\n<br>" +
                                        "• <b>" +
                                        "Tipo" +
                                        ": </b>" +
                                        exam.getType()+
                                        "\n<br>" +
                                        "• <b>" +
                                        "Descrizione" +
                                        ": </b>" +
                                        exam.getDescription();
                                DialogEntityDetailsFragment entityDetailsFragment = new DialogEntityDetailsFragment(info);
                                entityDetailsFragment.show(fm, "DialogEntityDetailsFragment");
                            });
                        } else {
                            Toast.makeText(context, "Nessun esame presente.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @SuppressLint("NotifyDataSetChanged")
        public void loadPost(PostGridAdapter adapter, List<PhotoDiaryPost> postsList, String animal) {
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

    public class AnimalOwnerOperations {

        private FirebaseFirestore db;
        private Context context;

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

        public List<Veterinarian> getVeterinariansList(List<Veterinarian> veterinariansList) {
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
