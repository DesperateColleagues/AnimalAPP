package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddImageDiaryFragment;
import it.uniba.dib.sms22235.adapters.ListViewPhotoDiaryAdapter;
import it.uniba.dib.sms22235.adapters.PostGridAdapter;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.entities.users.Animal;


public class PhotoDiaryFragment extends Fragment implements DialogAddImageDiaryFragment.DialogAddImageDiaryFragmentListener {

    private DialogAddImageDiaryFragment dialogAddImageDiaryFragment;
    private PhotoDiaryFragmentListener listener;
    private List<PhotoDiaryPost> posts;

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
        void loadPost(PostGridAdapter adapter, List<PhotoDiaryPost>postsList);
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


        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.VISIBLE);
        ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity()).getAnimalSet();
            dialogAddImageDiaryFragment = new DialogAddImageDiaryFragment(buildSpinnerEntries(animalSet));
            dialogAddImageDiaryFragment.setListener(this);
            dialogAddImageDiaryFragment.show(getChildFragmentManager(), "DialogAddImageDiary");
        });

        RecyclerView diaryRecycler = view.findViewById(R.id.diaryRecycler);
        diaryRecycler.setLayoutManager(new GridLayoutManager(context, 3));
        diaryRecycler.setAdapter(postGridAdapter);

        listener.loadPost(postGridAdapter, posts);
    }

    public void onImageAdded(PhotoDiaryPost post) {
        listener.onPostAdded(post);
    }

    @NonNull
    private ArrayList<String> buildSpinnerEntries(@NonNull LinkedHashSet<Animal> animals) {
        ArrayList<String> list = new ArrayList<>();

        for (Animal animal : animals) {
            list.add(animal.toString());
        }

        return list;
    }
}
