package it.uniba.dib.sms22235.tasks.veterinarian.dialogs;
import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;

    import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

    import it.uniba.dib.sms22235.R;

/**
 * Bottom sheet dialog to edit exams
 * */
public class BSDialogEditExamFragment extends BottomSheetDialogFragment {

    private OnDeleteListener onDeleteListener;
    private OnUpgradeListener onUpgradeListener;
    private OnShowListener onShowListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bsdialog_exam_veterinarian, container, false);
        root.findViewById(R.id.btnOnDeleteExam).setOnClickListener(v -> {
            onDeleteListener.onDelete();
        });
        root.findViewById(R.id.btnOnUpgradeExam).setOnClickListener(v -> {
            onUpgradeListener.onUpgrade();
        });
        root.findViewById(R.id.btnOnShowExam).setOnClickListener(v -> {
            onShowListener.onShow();
        });
        return root;
    }

    public BSDialogEditExamFragment setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
        return this;
    }

    public BSDialogEditExamFragment setOnUpgradeListener(OnUpgradeListener onUpgradeListener) {
        this.onUpgradeListener = onUpgradeListener;
        return this;
    }

    public BSDialogEditExamFragment setOnShowListener(OnShowListener onShowListener) {
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