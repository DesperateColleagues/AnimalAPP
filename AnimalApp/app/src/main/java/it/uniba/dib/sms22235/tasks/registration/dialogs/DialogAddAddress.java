package it.uniba.dib.sms22235.tasks.registration.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;

public class DialogAddAddress extends DialogFragment {

    public interface DialogAddAddressListener {
        void onAddressConfirmed(String address);
    }

    private DialogAddAddressListener listener;

    public void setListener(DialogAddAddressListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AnimalCardRoundedDialog);

        LayoutInflater inflater = requireActivity().getLayoutInflater(); //get the layout inflater
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_registration_add_address, null);

        builder.setView(root);

        EditText txtInputAddress = root.findViewById(R.id.txtInputAddress);
        EditText txtInputCivicNumber = root.findViewById(R.id.txtInputCivicNumber);
        EditText txtInputPostalNumber = root.findViewById(R.id.txtInputPostalNumber);
        EditText txtInputProv = root.findViewById(R.id.txtInputProv);
        EditText txtInputTown = root.findViewById(R.id.txtInputTown);

        root.findViewById(R.id.btnConfirmLocation).setOnClickListener(v -> {
            String address = txtInputAddress.getText().toString();
            String civicNumber = txtInputCivicNumber.getText().toString();
            String postalNumber = txtInputPostalNumber.getText().toString();
            String prov = txtInputProv.getText().toString();
            String town = txtInputTown.getText().toString();

            boolean isEmptyInput = address.equals("") || civicNumber.equals("")
                    || postalNumber.equals("") || prov.equals("") || town.equals("");

            if (!isEmptyInput) {
                boolean isNumber = true;
                int civicNumberInt = -1;
                int postalNumberInt = -1;

                try {
                    civicNumberInt = Integer.parseInt(civicNumber);
                    postalNumberInt = Integer.parseInt(postalNumber);
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Il campo CAP o numero civico, non è un numero INTERO", Toast.LENGTH_SHORT).show();
                    isNumber = false;
                }

                if (isNumber) {
                    String completeAddress = address + ", " + civicNumberInt  + ", " + postalNumberInt + ", "
                            + town + "  " + prov.toUpperCase();
                    listener.onAddressConfirmed(completeAddress);
                    dismiss();
                }

            } else {
                Toast.makeText(requireContext(), "Campi vuoti!", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

}
