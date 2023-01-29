package it.uniba.dib.sms22235.tasks.common.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;

public class DialogEntityDetailsFragment extends DialogFragment{

    private String info;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public DialogEntityDetailsFragment(String info) {
        this.info = info;
    }

    public Dialog onCreateDialog(Bundle SavedInstanceBundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(),
                R.style.AnimalCardRoundedDialog);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_entity_details, null);

        builder.setView(root);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Dettagli");
        builder.setCustomTitle(titleView);

        TextView txtEntityInfo = root.findViewById(R.id.txtEntityInfo);

        txtEntityInfo.setText((Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY)));

        return builder.create();
    }

}