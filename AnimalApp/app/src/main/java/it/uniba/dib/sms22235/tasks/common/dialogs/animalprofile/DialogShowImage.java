package it.uniba.dib.sms22235.tasks.common.dialogs.animalprofile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;

public class DialogShowImage extends DialogFragment {

    private final String uri;

    public interface DialogShowImageListener {
        void onPicDeletionSelected(String uri);
        void onPicShared(String uri);
    }

    private DialogShowImageListener listener;

    public void setListener(DialogShowImageListener listener) {
        this.listener = listener;
    }

    public DialogShowImage (String uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater(); //get the layout inflater
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_show_image, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AnimalCardRoundedDialog);
        builder.setView(root);

        Glide.with(requireActivity()).load(uri).into((ImageView) root.findViewById(R.id.imgPhotoDiaryShow));

        root.findViewById(R.id.btnDismiss).setOnClickListener(v -> dismiss());

        ImageButton btnDeleteImg = root.findViewById(R.id.btnDeleteImg);
        ImageButton btnShareImg = root.findViewById(R.id.btnShareImg);

        if (!(requireActivity() instanceof PassionateNavigationActivity)) {
            btnDeleteImg.setVisibility(View.GONE);
            btnShareImg.setVisibility(View.GONE);
        }

        btnDeleteImg.setOnClickListener(v -> {
            listener.onPicDeletionSelected(uri);
            dismiss();
        });

        btnShareImg.setOnClickListener(v -> {
            listener.onPicShared(uri);
            dismiss();
        });

        return builder.create();
    }

}
