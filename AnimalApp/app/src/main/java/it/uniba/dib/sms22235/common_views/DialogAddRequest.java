package it.uniba.dib.sms22235.common_views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Request;

public class DialogAddRequest extends DialogFragment {

    private String opType = "Offerta";
    private DialogAddRequestListener listener;

    interface DialogAddRequestListener {
        void onRequestAdded(Request request);
    }

    public void setListener(DialogAddRequestListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AnimalCardRoundedDialog);

        LayoutInflater inflater = requireActivity().getLayoutInflater();//get the layout inflater
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_request_add, null);//inflate the layout of the view with this new layout
        builder.setView(root);

        // todo add title

        final String [] requestTypes = {"Animale", "Aiuto", "Stallo"};

        ChipGroup requestsParamsChipGroup = root.findViewById(R.id.requestsParamsChipGroupDialog);

        SeekBar seekBar = root.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               switch (progress) {
                   case 0:
                       opType = "Offerta";
                       break;
                   case 1:
                       opType = "Richiesta";
                       break;
               }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        EditText txtRequestTitleInput = root.findViewById(R.id.txtRequestTitleInput);
        EditText txtRequestBodyInput = root.findViewById(R.id.txtRequestBodyInput);

        Button btnConfirmRequest = root.findViewById(R.id.btnConfirmRequest);
        btnConfirmRequest.setOnClickListener(v -> {
            String requestTitle = txtRequestTitleInput.getText().toString();
            String requestBody = txtRequestBodyInput.getText().toString();
            String requestType = "";

            for (int i = 0; i < requestsParamsChipGroup.getChildCount(); i++) {
                Chip chip = (Chip) requestsParamsChipGroup.getChildAt(i);

                if (chip.isChecked()) {
                    requestType = chip.getText().toString();
                }
            }

            boolean isEmptyInput = requestBody.equals("") || requestTitle.equals("") || requestType.equals("");

            if (!isEmptyInput) {
                listener.onRequestAdded(new Request(
                        "",
                        requestTitle,
                        requestBody,
                        opType,
                        requestType));
                dismiss();
            } else {
                Toast.makeText(getContext(), "Campi vuoti", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
