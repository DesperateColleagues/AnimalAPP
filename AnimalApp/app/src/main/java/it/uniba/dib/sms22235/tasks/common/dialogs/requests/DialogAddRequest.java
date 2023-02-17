package it.uniba.dib.sms22235.tasks.common.dialogs.requests;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * Dialog used to add new request info
 * */
public class DialogAddRequest extends DialogFragment {

    private String opType;
    private DialogAddRequestListener listener;

    /**
     * Interface with operations that occur when the request is added
     * */
    public interface DialogAddRequestListener {
        /**
         * This method is called when the request is added
         *
         * @param request the request added
         * @param animalMicrochip the microchip of the animal
         * */
        void onRequestAdded(Request request, String animalMicrochip);
    }

    public void setListener(DialogAddRequestListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogTheme);

        if (requireActivity() instanceof PassionateNavigationActivity) {
            opType = KeysNamesUtils.RequestFields.R_TYPE_REQUEST;
        } else {
            opType = KeysNamesUtils.RequestFields.R_TYPE_OFFER;
        }

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        //inflate the layout of the view with this new layout
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_add_request, null);
        manageChipVisibilityByRole(root);

        Spinner spinnerRequestAnimal = root.findViewById(R.id.spinnerRequestAnimal);

        // Set dialog title
        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(R.string.aggiunta_nuova_richiesta);
        builder.setCustomTitle(titleView);
        builder.setView(root);

        ChipGroup requestsParamsChipGroup = root.findViewById(R.id.requestsParamsChipGroupDialog);

        // Manage spinner visibility by the checked chip
        requestsParamsChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            try {
                String selectedChip = (String) ((Chip) root.findViewById(group.getCheckedChipId())).getText();
                if ((selectedChip.equals("Offerta animale"))) {

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

    private void manageChipVisibilityByRole(View root) {
        if (requireActivity() instanceof  PassionateNavigationActivity) {
            root.findViewById(R.id.chipHome).setVisibility(View.GONE);
        } else {
            root.findViewById(R.id.chipHome).setVisibility(View.VISIBLE);
        }

        if (requireActivity() instanceof VeterinarianNavigationActivity) {
            root.findViewById(R.id.chipAnimal).setVisibility(View.GONE);
        } else {
            root.findViewById(R.id.chipAnimal).setVisibility(View.VISIBLE);
        }

    }
}
