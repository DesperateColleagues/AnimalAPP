package it.uniba.dib.sms22235.tasks.common.dialogs.reports;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;

public class DialogReportAddInfo extends DialogFragment {

    public interface DialogReportAddInfoListener {
        void onInfoAdded(String title, String description);
    }

    private DialogReportAddInfoListener listener;

    public void setListener(DialogReportAddInfoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater(); //get the layout inflater
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_report_add_info, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AnimalCardRoundedDialog);
        builder.setView(root);

        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Informazioni segnalazione");
        builder.setCustomTitle(titleView);

        EditText txtInputAddDescriptionToReport = root.findViewById(R.id.txtInputAddDescriptionToReport);
        EditText txtInputAddTitleToReport = root.findViewById(R.id.txtInputAddTitleToReport);

        root.findViewById(R.id.btnConfirmReportInfo).setOnClickListener(v -> {
            String description = txtInputAddDescriptionToReport.getText().toString();
            String title = txtInputAddTitleToReport.getText().toString();

            boolean isEmpty = title.equals("") || description.equals("");

            if (!isEmpty) {
                listener.onInfoAdded(title, description);
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Campi vuoti!", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
