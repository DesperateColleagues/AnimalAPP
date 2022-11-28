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
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class DialogAddImageDiaryFragment extends DialogFragment {

    private DialogAddImageDiaryFragmentListener listener;
    private ImageView photoDiaryImageInsert;
    private Bitmap selectedImageBitmap;
    private final String username;
    private List<PhotoDiaryPost> photoDiaryPostList;

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


    public interface DialogAddImageDiaryFragmentListener {
        /**
         * This callback is activated when the post is successfully saved.
         * Update the list view with the brand new added post.
         *
         * @param post the post ref
         * */
        void onImageAdded(PhotoDiaryPost post);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the posts' list from files
        photoDiaryPostList = (ArrayList<PhotoDiaryPost>) DataManipulationHelper.readDataInternally(
                getContext(),  KeysNamesUtils.FileDirsNames.passionatePostRefDirName(username));

        // If the return is null then, there aren't post, so the list can be created
        if (photoDiaryPostList == null){
            photoDiaryPostList = new ArrayList<>();
            Toast.makeText(getContext(), "Array appena creato", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "" + photoDiaryPostList.size(), Toast.LENGTH_SHORT).show();
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

        Button btnSelectedImage = root.findViewById(R.id.btnSelectImage);
        Button btnSavePost = root.findViewById(R.id.btnSavePost);

        btnSavePost.setOnClickListener(v -> {
            String inputImageDescription = txtInputImageDescription.getText().toString();

            boolean isEmptyInput = inputImageDescription.equals("");

            if (!isEmptyInput) {
                // If the description is not empty check if the image has been selected
                if (selectedImageBitmap != null) {
                    // Save the image at the specified dir
                    String fileName = "post_" + (photoDiaryPostList.size() + 1);

                    String imagePath = DataManipulationHelper.saveBitmapToInternalStorage(
                            selectedImageBitmap,
                            KeysNamesUtils.FileDirsNames.passionatePostDirName(username),
                            fileName + ".jpg",
                            getContext());

                    Log.d("IMAGE", imagePath);
                    // Create a post object and save it to the internal storage
                    PhotoDiaryPost post = new PhotoDiaryPost(imagePath, inputImageDescription);
                    photoDiaryPostList.add(post); // add the post to the list

                    if(DataManipulationHelper.saveDataInternally(getContext(), photoDiaryPostList,
                            KeysNamesUtils.FileDirsNames.passionatePostRefDirName(username))) {
                        listener.onImageAdded(post);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Problemi durante il salvataggio del post",
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
                        "Il campo descrizione non può esssere vuoto",
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
