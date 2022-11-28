package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.LinkedHashSet;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddImageDiaryFragment;
import it.uniba.dib.sms22235.adapters.ListViewPhotoDiaryAdapter;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class PhotoDiaryFragment extends Fragment implements DialogAddImageDiaryFragment.DialogAddImageDiaryFragmentListener {

    private DialogAddImageDiaryFragment dialogAddImageDiaryFragment;
    private ListViewPhotoDiaryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passionate_photo_diary, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireActivity().getApplicationContext();

        adapter = new ListViewPhotoDiaryAdapter(context, 0);

        String username = ((PassionateNavigationActivity) requireActivity()).getPassionateUsername();

        ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            dialogAddImageDiaryFragment = new DialogAddImageDiaryFragment(username);
            dialogAddImageDiaryFragment.setListener(this);
            dialogAddImageDiaryFragment.show(getParentFragmentManager(), "DialogAddImageDiary");
        });

        // Retrieve the posts' references from file system
        LinkedHashSet<PhotoDiaryPost> postSet = (LinkedHashSet<PhotoDiaryPost>) DataManipulationHelper.readDataInternally(
                getContext(),  KeysNamesUtils.FileDirsNames.passionatePostRefDirName(username));

        // If the retrieved set is not null the list view can be built
        if (postSet != null) {
            for (PhotoDiaryPost post : postSet) {
                // The retrieved posts can be added to the adapter
                adapter.addPost(post);
            }
        }

        ListView diaryListView = view.findViewById(R.id.diaryListView);
        diaryListView.setAdapter(adapter);
    }

    public void onImageAdded(PhotoDiaryPost post) {
        // Update the list with the brand new post and then update the list
        adapter.addPost(post);
        adapter.notifyDataSetChanged();
    }
}
