package it.uniba.dib.sms22235.tasks.veterinarian.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Reservation;


public class BSDialogEditDiagnosisFragment extends BottomSheetDialogFragment {

    private OnDeleteListener onDeleteListener;
    private OnUpgradeListener onUpgradeListener;
    private OnShowListener onShowListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bsdialog_diagnosis_veterinarian, container, false);
        root.findViewById(R.id.btnOnDeleteDiagnosis).setOnClickListener(v -> {
            onDeleteListener.onDelete();
        });
        root.findViewById(R.id.btnOnUpgradeDiagnosis).setOnClickListener(v -> {
            onUpgradeListener.onUpgrade();
        });
        root.findViewById(R.id.btnOnShowDiagnosis).setOnClickListener(v -> {
            onShowListener.onShow();
        });
        return root;
    }

    public BSDialogEditDiagnosisFragment setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public BSDialogEditDiagnosisFragment setOnUpgradeListener(OnUpgradeListener onUpgradeListener) {
        this.onUpgradeListener = onUpgradeListener;
        return this;
    }

    public BSDialogEditDiagnosisFragment setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
        return this;
    }

    public interface OnDeleteListener {
        void onDelete();
    }

    public interface OnUpgradeListener{
        void onUpgrade();
    }

    public interface OnShowListener {
        void onShow();
    }

}