package it.uniba.dib.sms22235.tasks.common.views.requests.passionate.fragments.animalprofile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.common.views.requests.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.adapters.PostGridAdapter;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;

public class PhotoDiaryFragment extends Fragment {

    public interface PhotoDiaryFragmentListener {
        /**
         * This callback is called when the post is added to the fragment and is now
         * ready to be saved into the storage
         *
         * @param post the post to be saved into both FireStore and storage
         * */
        void onPostAdded(PhotoDiaryPost post);

        /**
         * This callback is called at the very beginning of the onViewCreated lifecycle of the
         * fragment and it is used to keep track of the added post in order to update
         * the adapter data set and to refresh the view
         *
         * @param adapter the adapter of the view to be updated
         * @param postsList the list of the post
         * */
        void loadPost(PostGridAdapter adapter, List<PhotoDiaryPost>postsList, String animal);
    }

    private PhotoDiaryFragmentListener listener;
    private List<PhotoDiaryPost> posts;
    private String animalMicrochip;

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
                            listener.onPostAdded(new PhotoDiaryPost(
                                uri.toString(),animalMicrochip
                            ));
                        }
                    }
                }
            });

    // Used to launch the callback to retrieve intent's results
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


    public PhotoDiaryFragment() {
        // not supported
    }

    public PhotoDiaryFragment(String animalMicrochip) {
        this.animalMicrochip = animalMicrochip;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (PhotoDiaryFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        posts = new ArrayList<>();

        return inflater.inflate(R.layout.fragment_passionate_photo_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();
        PostGridAdapter postGridAdapter = new PostGridAdapter(context, posts);

        Button btnAddAnimalPost = view.findViewById(R.id.btnAddAnimalPost);
        btnAddAnimalPost.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            photoUploadAndSaveActivity.launch(i);
        });

        RecyclerView diaryRecycler = view.findViewById(R.id.diaryRecycler);
        diaryRecycler.setLayoutManager(new GridLayoutManager(context, 3));
        diaryRecycler.setAdapter(postGridAdapter);

        listener.loadPost(postGridAdapter, posts, animalMicrochip);
    }
}
