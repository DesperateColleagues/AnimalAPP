package it.uniba.dib.sms22235.activities.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class DialogAddImageDiaryFragment extends DialogFragment {

    public interface DialogAddImageDiaryFragmentListener {
        /**
         * This callback is activated when the post is successfully saved.
         * Update the list view with the brand new added post.
         *
         * */
        void onImageAdded(PhotoDiaryPost post);
    }

    private DialogAddImageDiaryFragmentListener listener;
    private ImageView photoDiaryImageInsert;
    private Bitmap selectedImageBitmap;
    private final String username;
    private Set<PhotoDiaryPost> photoDiaryPostSet;

    public DialogAddImageDiaryFragment(String username) {
        selectedImageBitmap = null;
        this.username = username;
    }

    // Used to launch the callback to retrieve intent's results
    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();

                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().getContentResolver(), selectedImageUri);

                            photoDiaryImageInsert.setImageBitmap(selectedImageBitmap);
                            photoDiaryImageInsert.setVisibility(View.VISIBLE);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the posts' set from file system that will be update
        // when the user will add a new post
        photoDiaryPostSet = (LinkedHashSet<PhotoDiaryPost>) DataManipulationHelper.readDataInternally(
                getContext(),  KeysNamesUtils.FileDirsNames.passionatePostRefDirName(username));

        // If the return is null then, there aren't post, so the set can be created
        if (photoDiaryPostSet == null) {
            photoDiaryPostSet = new LinkedHashSet<>();
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_image_diary, null);

        builder.setView(root);
        builder.setTitle("Aggiunta al photo diary");

        photoDiaryImageInsert = root.findViewById(R.id.photoDiaryImageInsert);

        EditText txtInputImageDescription = root.findViewById(R.id.txtInputImageDescription);
        EditText txtInputPostTitle = root.findViewById(R.id.txtInputPostTitle);

        Button btnSelectedImage = root.findViewById(R.id.btnSelectImage);
        Button btnSavePost = root.findViewById(R.id.btnSavePost);

        btnSavePost.setOnClickListener(v -> {
            String inputImageDescription = txtInputImageDescription.getText().toString();
            String inputPostTitle = txtInputPostTitle.getText().toString();

            boolean isEmptyInput = inputImageDescription.equals("") || inputPostTitle.equals("");

            if (!isEmptyInput) {
                // If the description is not empty check if the image has been selected
                if (selectedImageBitmap != null) {
                    // Create a new post object
                    PhotoDiaryPost post = new PhotoDiaryPost(inputPostTitle, inputImageDescription);

                    // If the post title is unique it will be added to the Set
                    if (photoDiaryPostSet.add(post)) {
                        // Save the image at the specified dir
                        String fileName = post.getTitle() + ".jpg";

                        // Save the Bitmap to the internal storage
                        String dirPath = DataManipulationHelper.saveBitmapToInternalStorage(
                                selectedImageBitmap,
                                KeysNamesUtils.FileDirsNames.passionatePostDirName(username),
                                fileName,
                                getContext());

                        // Set the file data inside the post object
                        post.setDirName(dirPath);
                        post.setFileName(fileName);

                        // Save the updated set with the new post in the internal storage
                        if (DataManipulationHelper.saveDataInternally(getContext(), photoDiaryPostSet,
                                KeysNamesUtils.FileDirsNames.passionatePostRefDirName(username))) {
                            // Update the ListView with the new post
                            listener.onImageAdded(post);

                            // Send a notification to the user that the post is now visible
                            Toast.makeText(getContext(),"Immagine aggiunta al diary",
                                    Toast.LENGTH_LONG).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Problemi durante il salvataggio del post",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Il titolo è stato già usato",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(
                            getContext(),
                            "Selezionare un immagine prima di completare il post",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(
                        getContext(),
                        "I campi titoli e descrizione non possono esssere vuoti",
                        Toast.LENGTH_LONG).show();
            }
        });

        btnSelectedImage.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        return builder.create();
    }

    public void setListener(DialogAddImageDiaryFragmentListener listener) {
        this.listener = listener;
    }

}
