package it.uniba.dib.sms22235.tasks.common.views.backbenches;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;

/**
 * This fragment is used to add and display backbenches' info
 * */
public class BackBenchFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private TextView txtBackbenchDescription;
    private ImageView imgBackbench;
    private String ownerEmail;
    boolean isInsideRequest = false;

    private Button btnAddBackBenchImage;
    private Button btnAddBackBenchDescription;

    private BackbenchOperationsListener listener;

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

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (BackbenchOperationsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

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

        btnAddBackBenchImage = view.findViewById(R.id.btnAddBackBenchImage);

        if (isInsideRequest) {
            view.findViewById(R.id.txtBackbenchTitle).setVisibility(View.GONE);
            btnAddBackBenchImage.setVisibility(View.GONE);
        }

        btnAddBackBenchImage.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        btnAddBackBenchDescription = view.findViewById(R.id.btnAddBackBenchDescription);

        if (isInsideRequest) {
            btnAddBackBenchDescription.setVisibility(View.GONE);
        }

        txtBackbenchDescription = view.findViewById(R.id.txtBackbenchDescription);
        imgBackbench = view.findViewById(R.id.imgBackbench);

        btnAddBackBenchDescription.setOnClickListener(v -> {
            EditText inputEditTextField = new EditText(getContext());
            inputEditTextField.setHint(getResources().getString(R.string.descrizione_stallo));

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme)
                    .setView(inputEditTextField)
                    .setPositiveButton(getString(R.string.conferma), (dialogInterface, i) -> {
                        String description = inputEditTextField.getText().toString();

                        if (!description.equals("")) {
                            listener.updateBackBenchDescription(description, ownerEmail, db, getContext());
                        }
                    })
                    .setNegativeButton(getString(R.string.cancella), null);

            // Set dialog title
            @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_title);
            titleText.setText(getResources().getString(R.string.inserisci_descrizione_stallo));
            builder.setCustomTitle(titleView);

            builder.create().show();
        });

        loadBackbenchInfo(ownerEmail);
    }

    /**
     * This method is used to add the preview image of the backbecn
     *
     * @param uri the uri of the image to add
     * @param email the email of the user whose adding the preview pic
     * */
    private void addBackbenchPic(Uri uri, String email) {
        listener.updateBackbenchImage(email, db, storage, uri, getContext());
    }

    /**
     * This method is used to load backbench info
     *
     * @param email the email of the user whose info will be loaded
     * */
    private void loadBackbenchInfo(String email) {
        listener.loadBackbenchInfo(
                db,
                email,
                txtBackbenchDescription,
                btnAddBackBenchDescription,
                btnAddBackBenchImage,
                imgBackbench,
                getActivity(),
                getContext(),
                isAdded()
        );
    }
}
