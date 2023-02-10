package it.uniba.dib.sms22235.tasks.common.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.uniba.dib.sms22235.R;

public class ReportsBSDialog extends BottomSheetDialogFragment {

    private OnUpdateRequestListener onUpdateRequestListener;
    private OnConfirmRequestListener onConfirmRequestListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bsdialog_request, container, false);
        TextView editTextView = root.findViewById(R.id.txtCustomBSDialogEdit);
        editTextView.setText(getResources().getString(R.string.modifica_segnalazione));

        root.findViewById(R.id.btnConfirmRequestD).setOnClickListener(v -> {
            onConfirmRequestListener.onConfirm();
        });

        root.findViewById(R.id.btnUpdateInfoRequest).setOnClickListener(v -> {
            onUpdateRequestListener.onUpdate();
        });

        return root;
    }

    public interface OnUpdateRequestListener {
        void onUpdate();
    }

    public interface OnConfirmRequestListener {
        void onConfirm();
    }

    public void setOnConfirmRequestListener(OnConfirmRequestListener onConfirmRequestListener) {
        this.onConfirmRequestListener = onConfirmRequestListener;
    }

    public void setOnUpdateRequestListener(OnUpdateRequestListener onUpdateRequestListener) {
        this.onUpdateRequestListener = onUpdateRequestListener;
    }
}
