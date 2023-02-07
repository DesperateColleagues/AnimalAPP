package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;

public class DialogAddPassionateAnimalToPokeLink extends DialogFragment {

    public interface DialogAddPassionateAnimalToPokeLinkListener {
        /**
         * This method is called when the friend username has been added
         *
         * @param username the added username
         * */
        void onFriendAdded(String username);
    }

    private DialogAddPassionateAnimalToPokeLinkListener listener;

    public void setListener(DialogAddPassionateAnimalToPokeLinkListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View root = getLayoutInflater().inflate(R.layout.fragment_dialog_add_passionate_animal_to_poke_link, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        builder.setView(root);
        @SuppressLint("InflateParams")
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(R.string.seleziona_amico);
        builder.setCustomTitle(titleView);

        root.findViewById(R.id.btnConfirmPassionateLink).setOnClickListener(v -> {
            String friendId = ((EditText) root.findViewById(R.id.txtInputPassionateLink)).getText().toString();

            if (friendId.equals("")) {
                Toast.makeText(getContext(), "Campo vuoto! Impossibile procedere", Toast.LENGTH_SHORT).show();
            } else if (friendId.equals(((PassionateNavigationActivity) requireActivity()).getUserId())) {
                Toast.makeText(getContext(), "Lo username inserito è il tuo!", Toast.LENGTH_SHORT).show();
            } else {
                listener.onFriendAdded(friendId);
                dismiss();
            }
        });

        return builder.create();
    }
}
