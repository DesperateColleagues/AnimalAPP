package it.uniba.dib.sms22235.tasks.veterinarian.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;

/**
 * Dialog to add diagnosis
 * */
public class DialogAddDiagnosisFragment extends DialogFragment {

    private TextView txtInputDiagnosisDescription;
    private DialogAddDiagnosisFragment.DialogAddDiagnosisFragmentListener listener;
    private Diagnosis diagnosis;

    /**
     * Operation of this fragment
     * */
    public interface DialogAddDiagnosisFragmentListener{
        /**
         * Callback used when a diagnosis is added
         *
         * @param diagnosis the added diagnosis
         * */
        void onDialogAddDiagnosisDismissed(Diagnosis diagnosis);
    }

    public DialogAddDiagnosisFragment() {}

    public DialogAddDiagnosisFragment(Diagnosis diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_diagnosis, null);
        builder.setView(root);

        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);

        txtInputDiagnosisDescription = root.findViewById(R.id.txtInputDiagnosisDescription);

        TextView titleText = titleView.findViewById(R.id.dialog_title);

        if (diagnosis != null) {
            titleText.setText(getResources().getString(R.string.modifica_diagnosi));
            txtInputDiagnosisDescription.setText(diagnosis.getDescription());
        } else {
            titleText.setText(getResources().getString(R.string.aggiungi_diagnosi));
        }
        builder.setCustomTitle(titleView);

        Button btnAddDiagnosisFile = root.findViewById(R.id.btnAddDiagnosisFile);
        btnAddDiagnosisFile.setOnClickListener(view -> {
            Toast.makeText(getContext(), getString(R.string.error_generic), Toast.LENGTH_SHORT).show();
        });

        Button btnConfirmAddDiagnosis = root.findViewById(R.id.btnAddDiagnosis);
        btnConfirmAddDiagnosis.setOnClickListener(v -> {
            if (diagnosis != null) {
                String description = txtInputDiagnosisDescription.getText().toString();
                diagnosis.setDescription(description);
                listener.onDialogAddDiagnosisDismissed(diagnosis);
            } else {
                String description = txtInputDiagnosisDescription.getText().toString();
                listener.onDialogAddDiagnosisDismissed(new Diagnosis(description,null));
            }
            dismiss();
        });

        return builder.create();
    }

    public void setListener(DialogAddDiagnosisFragment.DialogAddDiagnosisFragmentListener listener) {
        this.listener = listener;
    }
}