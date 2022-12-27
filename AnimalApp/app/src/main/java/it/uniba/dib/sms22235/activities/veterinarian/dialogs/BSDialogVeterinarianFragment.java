package it.uniba.dib.sms22235.activities.veterinarian.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Reservation;


public class BSDialogVeterinarianFragment extends BottomSheetDialogFragment {


    private final Reservation reservation;
    private OnDeleteListener onDeleteListener;
    private OnAddDiagnosisListener onAddDiagnosisListener;
    private OnUpgradeListener onUpgradeListener;

    public BSDialogVeterinarianFragment(Reservation reservation) {
        this.reservation = reservation;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bsdialog_veterinarian, container, false);
        root.findViewById(R.id.btnOnDeleteDiagnosis).setOnClickListener(v -> {
            onDeleteListener.onDelete();
        });
        root.findViewById(R.id.btnOnUpgradeDiagnosis).setOnClickListener(v -> {
            onUpgradeListener.onUpgrade();
        });
        root.findViewById(R.id.btnOnAddDiagnosis).setOnClickListener(v -> {
            onAddDiagnosisListener.onAddDiagnosis();
        });


        return root;
    }

    public BSDialogVeterinarianFragment setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public BSDialogVeterinarianFragment setOnAddDiagnosisListener(OnAddDiagnosisListener onAddDiagnosisListener) {
        this.onAddDiagnosisListener = onAddDiagnosisListener;
        return this;
    }

    public BSDialogVeterinarianFragment setOnUpgradeListener(OnUpgradeListener onUpgradeListener) {
        this.onUpgradeListener = onUpgradeListener;
        return this;
    }

    public interface OnDeleteListener {
        void onDelete();
    }

    public interface OnAddDiagnosisListener{
        void onAddDiagnosis();
    }

    public interface OnUpgradeListener{
        void onUpgrade();
    }

}