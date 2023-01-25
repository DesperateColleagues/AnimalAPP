package it.uniba.dib.sms22235.tasks.common.views.requests;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.views.backbenches.BackBenchFragment;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class RequestDetailFragment extends Fragment {

    private Request request;

    // Manage Qr scanning
    private final ActivityResultLauncher<ScanOptions> qrDecodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(getContext(), "Attaccati al cazzo", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseStorage storage = ((NavigationActivityInterface) requireActivity()).getStorageInstance();
            FirebaseFirestore db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();

            // Take the result of QR intent
            String [] split = result.getContents().split(" - ");

            String microchip = split[0];
            String animalName = split[1];
            String oldOwner = split[2];

            // Start the change owner operations by updating the DB entry that corresponds
            // to the decoded QR fields
            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, microchip)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.NAME, animalName)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, oldOwner)
                    .get()
                    .addOnSuccessListener(query -> {
                        if (query.size() > 0) {
                            Animal animal = Animal.loadAnimal(query.getDocuments().get(0));
                            animal.setOwner(Objects.requireNonNull(((NavigationActivityInterface) requireActivity()).getUserId()));

                            String docKeyAnimal = KeysNamesUtils.RolesNames.ANIMAL
                                    + "_" + animal.getMicrochipCode();

                            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                                    .document(docKeyAnimal)
                                    .set(animal)
                                    .addOnSuccessListener(unused -> {
                                        try {
                                            manageChangeOwnerPosts(storage, db, microchip, oldOwner);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                        }
                    });
        }
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            request = (Request) bundle.getSerializable(KeysNamesUtils.CollectionsNames.REQUESTS);
        }

        return inflater.inflate(R.layout.fragment_request_details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button sendMail = view.findViewById(R.id.btnSendMail);

        TextView txtRequestDetailsTitle = view.findViewById(R.id.txtRequestDetailsTitle);
        TextView txtRequestDetailsEntityType = view.findViewById(R.id.txtRequestDetailsEntityType);
        TextView txtRequestDetailsBody = view.findViewById(R.id.txtRequestDetailsBody);

        txtRequestDetailsTitle.setText(request.getRequestTitle());
        txtRequestDetailsBody.setText(request.getRequestBody());

        // Setup request details messages
        txtRequestDetailsEntityType.setText(
                new StringBuilder()
                        .append(getContext().getResources().getString(R.string.operation))
                        .append(": ")
                        .append(request.getRequestType())
        );

        sendMail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, request.getUserEmail());
            intent.putExtra(Intent.EXTRA_SUBJECT, request.getRequestTitle());
            intent.putExtra(Intent.EXTRA_TEXT, request.getRequestBody());
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.sendMail)));
        });

        // Manage backbenches requests
        if (request.getRequestType().equals("Offerta stallo")) {

            FrameLayout frameLayout = view.findViewById(R.id.frameBackbench);
            frameLayout.setVisibility(View.VISIBLE);

            view.findViewById(R.id.infoDivider).setVisibility(View.VISIBLE);
            view.findViewById(R.id.txtRequestDetailsTitle2).setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.frameBackbench, new BackBenchFragment(request.getUserEmail()));
            transaction.commit();
        }

        // Manage animal request
        if (request.getRequestType().equals("Offerta animale")) {
            Button btnShowAnimalProfile = view.findViewById(R.id.btnShowAnimalProfile);
            btnShowAnimalProfile.setOnClickListener(v -> {
                // todo show animal profile
            });

            Button btnConfirmAnimalRequestQr = view.findViewById(R.id.btnConfirmAnimalRequestQr);
            btnConfirmAnimalRequestQr.setOnClickListener(v -> {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Scannerizza il QR code"); // todo translate
                options.setBeepEnabled(true);
                options.setBarcodeImageEnabled(true);
                options.setOrientationLocked(false);

                qrDecodeLauncher.launch(options);
            });

            // Set Buttons visibility
            btnShowAnimalProfile.setVisibility(View.VISIBLE);
            btnConfirmAnimalRequestQr.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This method is used to upload all the references to animal's posts
     *
     * @param storage the firebase storage reference
     * @param db the firestore reference
     * @param microchip the microchip code of the animal
     * @param oldOwner the old owner of the animal
     * */
    private void manageChangeOwnerPosts(FirebaseStorage storage, @NonNull FirebaseFirestore db, String microchip, String oldOwner) throws IOException {
        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(requireContext(),R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage("Spostando i post...");
        progressDialog.show();

        // Create the storage tree structure
        String currentFolderReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(oldOwner) +
                "/" +
                KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(microchip) + "/";

        // Obtain the posts' list of that specific animal
        db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL, microchip)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> posts = task.getResult().getDocuments();

                        if (posts.size() > 0) {
                            // Scan every post retrieved
                            for (DocumentSnapshot snapshot : posts) {
                                PhotoDiaryPost post = PhotoDiaryPost.loadPhotoDiaryPost(snapshot);

                                // Build the file name of the current post
                                String fileName = post.getFileName();
                                String fileReference = currentFolderReference + fileName;

                                // Obtain a reference of the storage
                                StorageReference currentReference = storage.getReference(fileReference);

                                // Set a limit of bytes
                                final long ONE_MEGABYTE = 1024 * 1024;

                                // Get the bytes of the file from the reference of the storage
                                currentReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                    // Retrieve the new owner of the animal via the ActivityInterface
                                    String newOwner = ((NavigationActivityInterface) requireActivity()).getUserId();

                                    // Create the new reference to the folder in the storage,
                                    // the reference of the new file. Then it is possible to obtain
                                    // a new storage reference
                                    String newFolderReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(newOwner) +
                                            "/" +
                                            KeysNamesUtils.FileDirsNames.passionatePostRefDirAnimal(microchip) + "/";
                                    String newFileReference = newFolderReference + fileName;
                                    StorageReference newReference = storage.getReference(newFileReference);

                                    // Put the retrieved bytes into the storage and update
                                    // the FireStore reference of the post
                                    newReference.putBytes(bytes).addOnCompleteListener(taskChangeFileLocation -> {
                                        if (taskChangeFileLocation.isSuccessful()) {
                                            taskChangeFileLocation.getResult().getStorage()
                                                    .getDownloadUrl().addOnCompleteListener(taskUri -> {

                                                // Set the download post URI
                                                post.setPostUri(taskUri.getResult().toString());

                                                // Save the post into the FireStore deleting the old reference
                                                db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY)
                                                        .document(fileName)
                                                        .set(post)
                                                        .addOnSuccessListener(documentReference -> {
                                                            Toast.makeText(getContext(),
                                                                    "Caricamento completato con successo", Toast.LENGTH_LONG).show();

                                                            currentReference.delete();
                                                            progressDialog.dismiss();


                                                            // Change profile image as well
                                                            try {
                                                                manageChangeOwnerProfilePic(storage, db, microchip, oldOwner);
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        });
                                            });
                                        }
                                    });
                                });
                            }
                        }
                    }
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
    private void manageChangeOwnerProfilePic(FirebaseStorage storage, @NonNull FirebaseFirestore db, String microchip, String oldOwner) throws IOException {
        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(requireContext(),R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage("Spostando l'immagine profilo...");
        progressDialog.show();

        // Create the storage tree structure
        String currentFolderReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(oldOwner) +
                "/";

        // Obtain the posts' list of that specific animal
        db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY_PROFILE)
                .whereEqualTo(KeysNamesUtils.PhotoDiaryFields.POST_ANIMAL, microchip)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> posts = task.getResult().getDocuments();

                        if (posts.size() > 0) {
                            // Scan every post retrieved
                            DocumentSnapshot snapshot = posts.get(0);
                            PhotoDiaryPost post = PhotoDiaryPost.loadPhotoDiaryPost(snapshot);

                            // Build the file name of the current post
                            String fileName = post.getFileName();
                            String fileReference = currentFolderReference + fileName;

                            // Obtain a reference of the storage
                            StorageReference currentReference = storage.getReference(fileReference);

                            // Set a limit of bytes
                            final long ONE_MEGABYTE = 1024 * 1024;

                            // Get the bytes of the file from the reference of the storage
                            currentReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                // Retrieve the new owner of the animal via the ActivityInterface
                                String newOwner = ((NavigationActivityInterface) requireActivity()).getUserId();

                                // Create the new reference to the folder in the storage,
                                // the reference of the new file. Then it is possible to obtain
                                // a new storage reference
                                String newFolderReference = KeysNamesUtils.FileDirsNames.passionatePostDirName(newOwner) +
                                        "/" ;
                                String newFileReference = newFolderReference + fileName;
                                StorageReference newReference = storage.getReference(newFileReference);

                                // Put the retrieved bytes into the storage and update
                                // the FireStore reference of the post
                                newReference.putBytes(bytes).addOnCompleteListener(taskChangeFileLocation -> {
                                    if (taskChangeFileLocation.isSuccessful()) {
                                        taskChangeFileLocation.getResult().getStorage()
                                                .getDownloadUrl().addOnCompleteListener(taskUri -> {

                                                    // Set the download post URI
                                                    post.setPostUri(taskUri.getResult().toString());

                                                    // Save the post into the FireStore deleting the old reference
                                                    db.collection(KeysNamesUtils.CollectionsNames.PHOTO_DIARY_PROFILE)
                                                            .document(fileName)
                                                            .set(post)
                                                            .addOnSuccessListener(documentReference -> {
                                                                Toast.makeText(getContext(),
                                                                        "Caricamento completato con successo", Toast.LENGTH_LONG).show();

                                                                currentReference.delete();
                                                                progressDialog.dismiss();

                                                                request.setIsCompleted(true);
                                                                db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                                                                        .document(request.getId())
                                                                        .set(request)
                                                                        .addOnSuccessListener(unused -> {
                                                                            Toast.makeText(getContext(),
                                                                                    "Aggiornamento completato con successo", Toast.LENGTH_SHORT).show();


                                                                            /*AlertDialog.Builder reloadDialogBuilder = new AlertDialog.Builder(getContext());
                                                                            final AlertDialog reloadDialog = reloadDialogBuilder.create();
                                                                            reloadDialog.setCancelable(false);
                                                                            reloadDialog.setMessage("Devi rieffettuare l'accesso per confermare");
                                                                            reloadDialog.setPositiveButton("OK", (dialog, which) -> SWITCH ACTIVITY);
                                                                            reloadDialog.show();*/

                                                                            // todo: switch Activity
                                                                            // todo: refactor with Tasks.whenAllComplete()
                                                                        });
                                                            });
                                                });
                                    }
                                });
                            });
                        }
                    }
                });
    }

    static class Adapter extends FragmentPagerAdapter {
        private Fragment fragment;


        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        public void addFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Informazioni stallo";
        }
    }
}
