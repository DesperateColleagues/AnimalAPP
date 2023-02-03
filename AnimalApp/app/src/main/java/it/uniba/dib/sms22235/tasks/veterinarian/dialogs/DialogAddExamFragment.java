package it.uniba.dib.sms22235.tasks.veterinarian.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.ChipGroup;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.operations.Exam;

public class DialogAddExamFragment extends DialogFragment {

    private DialogAddExamFragment.DialogAddExamFragmentListener listener;
    private Exam exam;

    public interface DialogAddExamFragmentListener{
        void onDialogAddExamDismissed(Exam exam);
    }

    public DialogAddExamFragment() {}

    public DialogAddExamFragment(Exam exam) {
        this.exam = exam;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AnimalCardRoundedDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_exam, null);
        builder.setView(root);

        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);

        TextView txtInputExamType = root.findViewById(R.id.txtInputExamType);
        TextView txtInputExamDescription = root.findViewById(R.id.txtInputExamDescription);

        if (exam != null) {
            titleText.setText(getResources().getString(R.string.modifica_diagnosi));
            txtInputExamDescription.setText(exam.getDescription());
        } else {
            titleText.setText(getResources().getString(R.string.aggiungi_diagnosi));
            exam = new Exam();
        }
        builder.setCustomTitle(titleView);

        RadioGroup radioGrp = root.findViewById(R.id.outcomeRadioGrp);

        Button btnConfirmAddExam = root.findViewById(R.id.btnAddExam);
        btnConfirmAddExam.setOnClickListener(v -> {

            String description = txtInputExamDescription.getText().toString();
            String type = txtInputExamType.getText().toString();
            int outcome = radioGrp.getCheckedRadioButtonId();

            if (!type.equals("") && !description.equals("") && outcome != -1) {
                if(outcome == R.id.pass) {
                    exam.setOutcome("PASS");
                } else if (outcome == R.id.fail) {
                    exam.setOutcome("FAIL");
                } else {
                    exam.setOutcome("UNDEFINED");
                }
                exam.setDescription(description);
                exam.setType(type);
                listener.onDialogAddExamDismissed(exam);
                dismiss();
            }
        });

        return builder.create();
    }

    public void setListener(DialogAddExamFragment.DialogAddExamFragmentListener listener) {
        this.listener = listener;
    }
}