package it.uniba.dib.sms22235.tasks.common.views.requests.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;

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
    private Uri destUri = null;
    private Set<PhotoDiaryPost> photoDiaryPostSet;
    private final List<String> animalList;
    private Button btnSavePost;

    public DialogAddImageDiaryFragment(List<String>animalList) {
        this.animalList = animalList;
    }

    // Used to launch the callback to retrieve intent's results
    private final ActivityResultLauncher<Intent> photoUploadAndSaveActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null && data.getData() != null) {
                        Uri sourceUri = data.getData();

                        // Destination URI of the cropped image
                        String destUriString = UUID.randomUUID().toString() + ".jpg";
                        destUri = Uri.fromFile(new File(requireContext().getCacheDir(), destUriString));

                        // Crop the image to fit the correct aspect ratio
                        UCrop.of(sourceUri, destUri)
                                .withAspectRatio(1, 1)
                                .start(requireActivity());

                        btnSavePost.setVisibility(View.VISIBLE);
                    }
                }
            });


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If the return is null then, there aren't post, so the set can be created
        if (photoDiaryPostSet == null) {
            photoDiaryPostSet = new LinkedHashSet<>();
        }
    }

    public void setPhotoDiaryImageInsert(Uri uri) {
        photoDiaryImageInsert.setVisibility(View.VISIBLE);
        photoDiaryImageInsert.setImageURI(uri);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AnimalCardRoundedDialog);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_image_diary, null);

        builder.setView(root);
        builder.setTitle("Aggiunta al photo diary");

        photoDiaryImageInsert = root.findViewById(R.id.photoDiaryImageInsert);
        Spinner spinnerAnimalsAddPhotoDiary = root.findViewById(R.id.spinnerAnimalsAddPhotoDiary);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, animalList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnimalsAddPhotoDiary.setAdapter(spinnerAdapter);

        Button btnSelectedImage = root.findViewById(R.id.btnSelectImage);
        btnSavePost = root.findViewById(R.id.btnSavePost);

        btnSavePost.setOnClickListener(v -> {
            if (destUri != null) {
                String animal = spinnerAnimalsAddPhotoDiary.getSelectedItem().toString().split(" - ")[1];
                PhotoDiaryPost post = new PhotoDiaryPost(
                        destUri.toString(), animal);

                listener.onImageAdded(post);

                dismiss();
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
