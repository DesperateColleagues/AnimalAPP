package it.uniba.dib.sms22235.common_dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Request;

public class BsdDialogRequest extends BottomSheetDialogFragment {

    private final Request request;

    private OnDeleteRequestListener onDeleteRequestListener;
    private OnUpdateRequestListener onUpdateRequestListener;
    private OnConfirmRequestListener onConfirmRequestListener;

    public BsdDialogRequest(Request request) {
        this.request = request;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bsdialog_request, container, false);

        root.findViewById(R.id.btnConfirmRequestD).setOnClickListener(v -> {
            onConfirmRequestListener.onConfirm();
        });

        root.findViewById(R.id.btnUpdateInfoRequest).setOnClickListener(v -> {
            onUpdateRequestListener.onUpdate();
        });

        root.findViewById(R.id.btnOnDeleteRequest).setOnClickListener(v -> {
            onDeleteRequestListener.onDelete();
        });

        if (request.getRequestType().equals("Offerta stallo")) {
            root.findViewById(R.id.btnOnDeleteRequest).setVisibility(View.GONE);
        }

        return root;
    }

    public interface OnDeleteRequestListener {
        void onDelete();
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

    public void setOnDeleteRequestListener(OnDeleteRequestListener onDeleteRequestListener) {
        this.onDeleteRequestListener = onDeleteRequestListener;
    }

    public void setOnUpdateRequestListener(OnUpdateRequestListener onUpdateRequestListener) {
        this.onUpdateRequestListener = onUpdateRequestListener;
    }
}
