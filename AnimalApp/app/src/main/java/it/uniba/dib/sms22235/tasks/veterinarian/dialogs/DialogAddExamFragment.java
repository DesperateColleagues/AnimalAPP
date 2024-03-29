package it.uniba.dib.sms22235.tasks.veterinarian.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This dialog is used to add exams data
 * */
public class DialogAddExamFragment extends DialogFragment {

    private TextView txtInputExamType;
    private TextView txtInputExamDescription;
    private DialogAddExamFragment.DialogAddExamFragmentListener listener;
    private Exam exam;

    /**
     * Dialog operations
     * */
    public interface DialogAddExamFragmentListener{
        /**
         * Callback used to add a new exam
         *
         * @param exam the added exam
         * */
        void onDialogAddExamDismissed(Exam exam);
    }

    public DialogAddExamFragment() {}

    public DialogAddExamFragment(Exam exam) {
        this.exam = exam;
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_exam, null);
        builder.setView(root);

        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);

        txtInputExamDescription = root.findViewById(R.id.txtInputExamDescription);
        txtInputExamType = root.findViewById(R.id.txtInputExamType);

        TextView titleText = titleView.findViewById(R.id.dialog_title);

        LinearLayout outcomeLayout = root.findViewById(R.id.outcome_layout);
        RadioGroup radioGrp = root.findViewById(R.id.outcomeRadioGrp);

        if (exam != null) {
            titleText.setText(getResources().getString(R.string.modifica_esame));
            Log.e("AnimalAPP - Esami", "DialogAddExamFragment:55 - Esame: " + exam.toString());
            txtInputExamDescription.setText(exam.getDescription());
            txtInputExamType.setText(exam.getType());
        } else {
            titleText.setText(getResources().getString(R.string.aggiungi_esame));
            outcomeLayout.setVisibility(View.GONE);
            exam = new Exam();
        }
        builder.setCustomTitle(titleView);

        Button btnConfirmAddExam = root.findViewById(R.id.btnAddExam);
        btnConfirmAddExam.setOnClickListener(v -> {

            String description = txtInputExamDescription.getText().toString();
            String type = txtInputExamType.getText().toString();
            int outcome = radioGrp.getCheckedRadioButtonId();

            if (!type.equals("") && !description.equals("")) {
                if(outcome == R.id.pass) {
                    exam.setOutcome(KeysNamesUtils.ExamsFields.EXAM_PASS);
                } else if (outcome == R.id.fail) {
                    exam.setOutcome(KeysNamesUtils.ExamsFields.EXAM_FAIL);
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