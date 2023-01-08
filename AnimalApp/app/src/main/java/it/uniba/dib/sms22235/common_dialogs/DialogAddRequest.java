package it.uniba.dib.sms22235.common_dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class DialogAddRequest extends DialogFragment {

    private String opType;
    private DialogAddRequestListener listener;

    public interface DialogAddRequestListener {
        void onRequestAdded(Request request, String animalMicrochip);
    }

    public void setListener(DialogAddRequestListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AnimalCardRoundedDialog);

        if (requireActivity() instanceof PassionateNavigationActivity) {
            opType = KeysNamesUtils.RequestFields.R_TYPE_REQUEST;
        } else {
            opType = KeysNamesUtils.RequestFields.R_TYPE_OFFER;
        }


        LayoutInflater inflater = requireActivity().getLayoutInflater();//get the layout inflater
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_add_request, null);//inflate the layout of the view with this new layout

        // todo: add ente
        if (requireActivity() instanceof VeterinarianNavigationActivity ) {
            root.findViewById(R.id.requestTypeRadio).setVisibility(View.GONE);
        }

        builder.setView(root);

        Spinner spinnerRequestAnimal = root.findViewById(R.id.spinnerRequestAnimal);

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText("Aggiunta nuova richiesta/offerta");
        builder.setCustomTitle(titleView);
        builder.setView(root);

        // todo add title

        final String [] requestTypes = {"Animale", "Aiuto", "Stallo"};

        RadioGroup requestTypeRadioGroup = root.findViewById(R.id.requestTypeRadio);
        requestTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {

            // find the radiobutton by returned id
            String selectedRequestType = (String) ((RadioButton) root.findViewById(radioGroup.getCheckedRadioButtonId())).getText();
            if (selectedRequestType.equals(KeysNamesUtils.RequestFields.R_TYPE_REQUEST)) {
                opType = getResources().getString(R.string.richiesta);
            }
            else if (selectedRequestType.equals(KeysNamesUtils.RequestFields.R_TYPE_OFFER)) {
                opType = getResources().getString(R.string.offerta);
            }
        });

        ChipGroup requestsParamsChipGroup = root.findViewById(R.id.requestsParamsChipGroupDialog);

        // Manage spinner visibility by the checked chip
        requestsParamsChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            try {
                String selectedChip = (String) ((Chip) root.findViewById(group.getCheckedChipId())).getText();
                if ((selectedChip.equals("Animale") || selectedChip.equals("Stallo")) && opType.equals(KeysNamesUtils.RequestFields.R_TYPE_OFFER)) {

                    // The spinner could be visible only if the Activity is the one of the Passionate
                    if (requireActivity() instanceof PassionateNavigationActivity) {
                        LinkedHashSet<Animal> animalSet = ((PassionateNavigationActivity) requireActivity()).getAnimalSet();

                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                                android.R.layout.simple_spinner_dropdown_item, buildSpinnerEntries(animalSet));
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerRequestAnimal.setAdapter(spinnerAdapter);
                        spinnerRequestAnimal.setVisibility(View.VISIBLE);
                    }
                } else {
                    spinnerRequestAnimal.setVisibility(View.GONE);
                }
            } catch(NullPointerException n) {
                spinnerRequestAnimal.setVisibility(View.GONE);
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

            String animalMicrochip = "";

            if (spinnerRequestAnimal.getVisibility() == View.VISIBLE) {
                animalMicrochip = (String) spinnerRequestAnimal.getSelectedItem();
            }

            if (!isEmptyInput) {
                listener.onRequestAdded(new Request(
                        requestTitle,
                        requestBody,
                        opType,
                        requestType), animalMicrochip);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Campi vuoti", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    @NonNull
    private ArrayList<String> buildSpinnerEntries(@NonNull LinkedHashSet<Animal> animals) {
        ArrayList<String> list = new ArrayList<>();

        for (Animal animal : animals) {
            list.add(animal.toString());
        }

        return list;
    }
}
