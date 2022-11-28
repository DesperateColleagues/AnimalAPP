package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddImageDiaryFragment;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;

public class PhotoDiaryFragment extends Fragment implements DialogAddImageDiaryFragment.DialogAddImageDiaryFragmentListener {

    private DialogAddImageDiaryFragment dialogAddImageDiaryFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            dialogAddImageDiaryFragment = new DialogAddImageDiaryFragment((
                    (PassionateNavigationActivity) requireActivity()).getPassionateUsername());
            dialogAddImageDiaryFragment.setListener(this);
            dialogAddImageDiaryFragment.show(getParentFragmentManager(), "DialogAddImageDiary");
        });

        return inflater.inflate(R.layout.fragment_passionate_photo_diary, container, false);
    }

    public void onImageAdded(@NonNull PhotoDiaryPost post) {
    }
}
