package it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments;

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
import it.uniba.dib.sms22235.entities.users.AbstractPersonUser;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.animalprofile.DialogShowImage;
import it.uniba.dib.sms22235.adapters.animals.AnimalPostAdapter;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This class displays the post of the animal
 * */
public class PhotoDiaryFragment extends Fragment implements DialogShowImage.DialogShowImageListener {

    /**
     * This interface displays the common operations of photo diary
     * */
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
        void loadPost(AnimalPostAdapter adapter, List<PhotoDiaryPost>postsList, String animal);

        /**
         * This callback is called when a picture of the animal has been selected
         * to be deleted
         *
         * @param url uri of the picture to delete
         * */
        void onPostDeleted(String url, String microchip);

        /**
         * This callback is called when a picture is selected to be share
         *
         * @param url the url of the picture to be shared
         * */
        void onPostShared(String url);
    }

    private PhotoDiaryFragmentListener listener;
    private List<PhotoDiaryPost> posts;
    private String animalMicrochip;
    private Animal animal;
    private AbstractPersonUser user;
    private int viewMode;

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
                                uri.toString(), animal.getMicrochipCode()
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
        throw new UnsupportedOperationException();
    }


    public PhotoDiaryFragment(Animal animal, AbstractPersonUser user, int viewMode) {
        this.animal = animal;
        this.user = user;
        this.viewMode = viewMode;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

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
        AnimalPostAdapter postGridAdapter = new AnimalPostAdapter(context, posts);

        Button btnAddAnimalPost = view.findViewById(R.id.btnAddAnimalPost);


        if (viewMode != KeysNamesUtils.AnimalInformationViewModeFields.PHOTO_ONLY) {
            btnAddAnimalPost.setVisibility(View.GONE);
        } else if (getActivity() instanceof PassionateNavigationActivity) {
            if (((Passionate) ((NavigationActivityInterface) requireActivity()).getUser()).getUserIdentifier().equals(animal.getOwner()) &&
                    viewMode == KeysNamesUtils.AnimalInformationViewModeFields.PHOTO_ONLY) {
            btnAddAnimalPost.setOnClickListener(v -> {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                photoUploadAndSaveActivity.launch(i);
            });
            }
        }

        postGridAdapter.setOnItemClickListener(uri -> {
            DialogShowImage dialogShowImage = new DialogShowImage(uri);
            dialogShowImage.setListener(this);
            dialogShowImage.show(getChildFragmentManager(), "DialogShowImage");
        });

        RecyclerView diaryRecycler = view.findViewById(R.id.diaryRecycler);
        diaryRecycler.setLayoutManager(new GridLayoutManager(context, 3));
        diaryRecycler.setAdapter(postGridAdapter);
        listener.loadPost(postGridAdapter, posts, animal.getMicrochipCode());
    }

    @Override
    public void onPicDeletionSelected(String uri) {
        listener.onPostDeleted(uri, animalMicrochip);
    }

    @Override
    public void onPicShared(String uri) {
        listener.onPostShared(uri);
    }
}
