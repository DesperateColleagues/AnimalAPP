package it.uniba.dib.sms22235.tasks.common.views.backbenches;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.entities.operations.Backbench;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class BackBenchFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Backbench backbench;
    private TextView txtBackbenchDescription;
    private ImageView imgBackbench;
    private String ownerEmail;
    boolean isInsideRequest = false;

    public BackBenchFragment() {
        // not supported
    }

    public BackBenchFragment(String ownerEmail) {
        isInsideRequest = true;
        this.ownerEmail = ownerEmail;
    }

    private final ActivityResultLauncher<Intent> cropResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), resCrop -> {
                if (resCrop.getResultCode() == Activity.RESULT_OK) {
                    // Get intent data as result
                    Intent data = resCrop.getData();

                    if (data != null) {
                        // Get the output uri of the crop intent
                        Uri uri = UCrop.getOutput(data);

                        if (uri != null) {
                            // Update firestore and storage
                            addBackbenchPic(uri, ownerEmail);
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null && data.getData() != null) {
                        Uri sourceUri = data.getData();

                        // Destination URI of the cropped image
                        String destUriString = UUID.randomUUID().toString() + ".jpg";
                        Uri destUri = Uri.fromFile(new File(requireContext().getCacheDir(), destUriString));

                        // Crop the image to fit the correct aspect ratio
                        Intent cropIntent = UCrop.of(sourceUri, destUri)
                                .withAspectRatio(1, 1)
                                .getIntent(requireContext());

                        cropResult.launch(cropIntent);
                    }
                }
            });

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();
        storage = ((NavigationActivityInterface) requireActivity()).getStorageInstance();

        return inflater.inflate(R.layout.fragment_backbench, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isInsideRequest) {
            ownerEmail = ((NavigationActivityInterface) requireActivity()).getUserId();
        }

        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);

        // Create a new backbench instance and load its info
        backbench = new Backbench(ownerEmail);
        loadBackbenchInfo(ownerEmail);

        Button btnAddBackBenchImage = view.findViewById(R.id.btnAddBackBenchImage);

        if (isInsideRequest) {
            btnAddBackBenchImage.setVisibility(View.GONE);
        }

        btnAddBackBenchImage.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        Button btnAddBackBenchDescription = view.findViewById(R.id.btnAddBackBenchDescription);

        if (isInsideRequest) {
            btnAddBackBenchDescription.setVisibility(View.GONE);
        }

        txtBackbenchDescription = view.findViewById(R.id.txtBackbenchDescription);
        imgBackbench = view.findViewById(R.id.imgBackbench);

        btnAddBackBenchDescription.setOnClickListener(v -> {
            EditText inputEditTextField = new EditText(getContext());
            inputEditTextField.setHint("Descrizione stallo");

            // todo: refactor dialog

            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("Title")
                    .setMessage("Inserisci una descrizione per il tuo stallo")
                    .setView(inputEditTextField)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String description = inputEditTextField.getText().toString();

                        if (!description.equals("")) {
                            backbench.setDescription(description);

                            db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                                    .document(KeysNamesUtils.FileDirsNames.backBenchPic(ownerEmail))
                                    .set(backbench)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(getContext(), "Descrizione stallo inserita correttamente",
                                                    Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.show();
        });
    }

    /**
     * This method is used to add the preview image of the backbecn
     *
     * @param uri the uri of the image to add
     * @param email the email of the user whose adding the preview pic
     * */
    private void addBackbenchPic(Uri uri, String email) {
        String fileName = KeysNamesUtils.FileDirsNames.backBenchPic(email);

        // Create the storage tree structure
        String fileReference = KeysNamesUtils.FileDirsNames.BACKBENCH_POST +
                "/" + fileName;

        StorageReference storageReference = storage.getReference(fileReference);


        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Salvando l'immagine...");
        progressDialog.show();

        // Start the upload task
        UploadTask uploadTask = storageReference.putFile(uri);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage()
                        .getDownloadUrl().addOnCompleteListener(taskUri -> {
                            backbench.setDownloadableImage(taskUri.getResult().toString());
                            db.collection(KeysNamesUtils.CollectionsNames.BACKBENCH)
                                    .document(KeysNamesUtils.FileDirsNames.backBenchPic(email))
                                    .set(backbench)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Immagine stallo caricata", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                        });
            }
        });
    }

    /**
     * This method is used to load backbench info
     *
     * @param email the email of the user whose info will be loaded
     * */
    private void loadBackbenchInfo(String email) {
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
                            backbench = Backbench.loadBackbench(change.getDocument());
                            txtBackbenchDescription.setText(backbench.getDescription());
                            Glide.with(requireContext()).load(backbench.getDownloadableImage()).into(imgBackbench);
                        }
                    }
                });
    }
}
