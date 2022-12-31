package it.uniba.dib.sms22235.activities.veterinarian.dialogs;

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


public class DialogAddDiagnosisFragment extends DialogFragment {

    private TextView txtInputDiagnosisDescription;

    public interface DialogAddDiagnosisFragmentListener{
        void onDialogAddDiagnosisDismissed(Diagnosis diagnosis);
    }

    private DialogAddDiagnosisFragment.DialogAddDiagnosisFragmentListener listener;

    public void setListener(DialogAddDiagnosisFragment.DialogAddDiagnosisFragmentListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_diagnosis, null);
        builder.setView(root);
        builder.setTitle("Aggiunta diagnosi");

        txtInputDiagnosisDescription = root.findViewById(R.id.txtInputDiagnosisDescription);

        Button btnAddDiagnosisFile = root.findViewById(R.id.btnAddDiagnosisFile);

        btnAddDiagnosisFile.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Feature will be implemented soon!", Toast.LENGTH_SHORT).show();
        });

        Button btnConfirmAddDiagnosis = root.findViewById(R.id.btnAddDiagnosis);
        btnConfirmAddDiagnosis.setOnClickListener(v -> {
            String diagnosis = txtInputDiagnosisDescription.getText().toString();
            listener.onDialogAddDiagnosisDismissed(new Diagnosis(diagnosis,null));
            dismiss();
        });

        return builder.create();
    }
}