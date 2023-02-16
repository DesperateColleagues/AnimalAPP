package it.uniba.dib.sms22235.tasks.common.views.requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.login.LoginActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This interface describes the operations that occur when an animal ownership is transfered
 * */
public interface RequestsAnimalTransferOperationsListener {

    /**
     * This method perform all the operations in the transfer
     *
     * @param db the db reference
     * @param storage the storage reference
     * @param newOwner the new owner of the animal
     * @param microchip the microchip of the animal
     * @param animalName the name of the animal
     * @param oldOwner the old owner of the animal
     * @param reloadMessage the message of reloading of the app
     * @param context the app context
     * @param activity the attached activity
     * @param request the request reference
     * */
    default void transferOperations(@NonNull FirebaseFirestore db, FirebaseStorage storage,
                                    String newOwner, String microchip,
                                    String animalName, String oldOwner, String reloadMessage,
                                    Context context, FragmentActivity activity, Request request) {
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, microchip)
                .whereEqualTo(KeysNamesUtils.AnimalFields.NAME, animalName)
                .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, oldOwner)
                .get()
                .addOnSuccessListener(query -> {
                    if (query.size() > 0) {
                        Animal animal = Animal.loadAnimal(query.getDocuments().get(0));
                        animal.setOwner(newOwner);

                        String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                                + "_" + animal.getMicrochipCode();

                        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                .document(docKeyAnimal)
                                .set(animal)
                                .addOnSuccessListener((unused -> {
                                    try {
                                        transferAnimalPostsAndProfilePic(request, storage, db, newOwner,
                                                microchip, reloadMessage, oldOwner, context, activity);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }));
                    }
                });
    }

    /**
     * This method is used to obtain a task which returns the byte of the image of the post
     *
     * @param post the post
     * @param storage the storage reference
     * @param currentFolderReference the current foldere reference
     *
     * @return the task with the bytes of the post's image
     * */
    default Task<byte[]> getPostBytesTask(@NonNull PhotoDiaryPost post,
                                          @NonNull FirebaseStorage storage,
                                          String currentFolderReference) {
        // Build the file name of the current post
        String fileName = post.getFileName();
        String fileReference = currentFolderReference + fileName;

        // Obtain a reference of the storage
        StorageReference currentReference = storage.getReference(fileReference);

        // Set a limit of bytes
        final long FIVE_MEGABYTE = 1024 * 1024 * 5;

        // Get the bytes of the file from the reference of the storage
        return currentReference.getBytes(FIVE_MEGABYTE);
    }

    /**
     * This method is used to delete the current reference of the post
     *
     * @param post the post
     * @param storage the storage reference
     * @param currentFolderReference the current foldere reference
     * */
    default void deleteCurrentReference(@NonNull PhotoDiaryPost post,
                                        @NonNull FirebaseStorage storage,
                                        String currentFolderReference) {
        // Build the file name of the current post
        String fileName = post.getFileName();
        String fileReference = currentFolderReference + fileName;

        // Obtain a reference of the storage
        StorageReference currentReference = storage.getReference(fileReference);
        currentReference.delete();
    }

    /**
     * This method gives a task that updates the the post uri
     *
     * @param collectionName the name of the collection
     * @param db the db reference
     * @param post the post
     * */
    @NonNull
    default Task<Void> updatePostUriTask(String collectionName,
                                         @NonNull FirebaseFirestore db,
                                         @NonNull PhotoDiaryPost post) {
        return db.collection(collectionName)
                .document(post.getFileName())
                .set(post);
    }


    /**
     * This method is used to complete the request
     *
     * @param db db reference
     * @param reloadMessage the reload message
     * @param progressDialog the progress dialog
     * @param context the context of the app
     * @param request the request to complete
     * @param activity holder activity
     * */
    default void completeRequest(@NonNull FirebaseFirestore db,
                                 String reloadMessage,
                                 Context context,
                                 ProgressDialog progressDialog,
                                 @NonNull Request request,
                                 FragmentActivity activity) {
        request.setIsCompleted(true);

        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                .document(request.getId())
                .set(request)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    AlertDialog.Builder reloadDialogBuilder = new AlertDialog.Builder(context);
                    final AlertDialog reloadDialog = reloadDialogBuilder.create();
                    reloadDialog.setCancelable(false);
                    reloadDialog.setMessage(reloadMessage);
                    reloadDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialogInterface, i) -> {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        activity.finish();
                    });
                    reloadDialog.show();

                    Toast.makeText(context,
                            "Aggiornamento completato con successo", Toast.LENGTH_SHORT).show();

                });
    }

    /**
     * This method is used to upload all the references to animal's posts
     *
     * @param storage the firebase storage reference
     * @param db the firestore reference
     * @param microchip the microchip code of the animal
     * @param oldOwner the old owner of the animal
     * */
    default void transferAnimalPostsAndProfilePic(Request request, FirebaseStorage storage,
                                                  @NonNull FirebaseFirestore db,
                                                  String newOwner, String microchip,
                                                  String reloadMessage, String oldOwner,
                                                  Context context, FragmentActivity activity) throws IOException {

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage("Spostando i post...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String currentFolderReferencePosts;
        String currentFolderReferenceProfilePic;
        // Create the storage tree structure of the posts directory
        currentFolderReferencePosts = KeysNamesUtils.FileDirsNames.passionatePostDirName(oldOwner) +
                "/" +
                KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(microchip) + "/";
        // Create the storage tree structure of the profile pic file
        currentFolderReferenceProfilePic = KeysNamesUtils.FileDirsNames.passionatePostDirName(oldOwner) +
                "/";


        // The task to get the post
        Task<QuerySnapshot> postQuery = db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL, microchip)
                .get();

        postQuery.addOnCompleteListener(taskAll -> {
            if (taskAll.isSuccessful()) {
                QuerySnapshot snapshotPost = (QuerySnapshot) taskAll.getResult();

                List<DocumentSnapshot> posts = snapshotPost.getDocuments();

                if (posts.size() > 0) {
                    for (int i = 0; i < posts.size(); i++) {
                        PhotoDiaryPost post = PhotoDiaryPost.loadPhotoDiaryPost(posts.get(i));
                        Task<byte[]> taskBytes;

                        String newFolderReference;

                        boolean isPostMode = !post.getFileName().equals(KeysNamesUtils.FileDirsNames.animalProfilePic(microchip));

                        // Get the task with bytes of the current post and create the
                        // new reference to the folder in the storage
                        if (isPostMode) {
                            taskBytes = getPostBytesTask(post, storage, currentFolderReferencePosts);
                            newFolderReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(newOwner) +
                                    "/" +
                                    KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(microchip) + "/";
                        } else {
                            taskBytes = getPostBytesTask(post, storage, currentFolderReferenceProfilePic);
                            newFolderReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(newOwner) +
                                    "/" ;

                        }

                        // Get the bytes of the file from the reference of the storage and
                        // copy it into a new final variable in order to use it in the lambda
                        final String finalNewFolderReference = newFolderReference;

                        // Final variable used to check the loops to stop the progress dialog
                        int finalI = i;

                        taskBytes.addOnSuccessListener(bytes -> {
                            String newFileReference = finalNewFolderReference + post.getFileName();
                            StorageReference newReference = storage.getReference(newFileReference);

                            // Put the retrieved bytes into the storage and update
                            // the FireStore reference of the post
                            newReference.putBytes(bytes).addOnCompleteListener(taskChangeFileLocation ->
                                    taskChangeFileLocation.getResult().getStorage().getDownloadUrl().addOnCompleteListener(taskUri -> {
                                        post.setPostUri(taskUri.getResult().toString());

                                        updatePostUriTask(KeysNamesUtils.CollectionsNames.PHOTO_DIARY, db, post)
                                                .addOnSuccessListener(unused -> {
                                                    if (isPostMode) {
                                                        deleteCurrentReference(post, storage, currentFolderReferencePosts);
                                                    } else {
                                                        deleteCurrentReference(post, storage, currentFolderReferenceProfilePic);
                                                    }

                                                    if (finalI == posts.size() - 1) {
                                                        completeRequest(db, reloadMessage, context,
                                                                progressDialog, request, activity);
                                                    }
                                                });
                                    }));
                        });
                    }
                }
            }
        });
    }

}
