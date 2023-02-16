package it.uniba.dib.sms22235.tasks.common.dialogs.animalprofile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;

/**
 * This dialog is used to show a specific image
 * */
public class DialogShowImage extends DialogFragment {

    private final String uri;

    /**
     * This interface is used to perform operations that occur when a picture is selected
     * and shown in this dialog
     * */
    public interface DialogShowImageListener {

        /**
         * This method is called when a picture is set to be deleted
         *
         * @param uri the uri of the picture to delete
         * */
        void onPicDeletionSelected(String uri);

        /**
         * This method is called when a picture is set to be shared
         *
         * @param uri the uri of the image to share
         * */
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
