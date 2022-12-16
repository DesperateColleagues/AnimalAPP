package it.uniba.dib.sms22235.activities.passionate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Purchase;

public class DialogAddPurchaseFragment extends DialogFragment
        implements android.app.DatePickerDialog.OnDateSetListener, DialogAddCategoryFragment.DialogAddCategoryFragmentListener {

    public interface DialogAddPurchaseFragmentListener {
        void onDialogAddPurchaseFragmentDismissed(Purchase purchase);
    }

    private DialogAddPurchaseFragmentListener listener;

    private EditText txtInputDatePurchase;
    private EditText txtInputCategory;

    private final ArrayList<String> animaList;

    private String dateSql;

    private Spinner spinnerAnimals;

    public DialogAddPurchaseFragment(ArrayList<String>animaList) {
        this.animaList = animaList;
    }

    public void setListener(DialogAddPurchaseFragmentListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),
                R.style.AnimalCardRoundedDialog);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_purchase, null);

        builder.setView(root);
        builder.setTitle("Inserimento spesa");

        // Set the spinner items
        spinnerAnimals = root.findViewById(R.id.spinnerAnimalsAddPurchase);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, animaList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnimals.setAdapter(spinnerAdapter);

        txtInputDatePurchase = root.findViewById(R.id.txtInputDatePurchase);
        txtInputCategory = root.findViewById(R.id.txtInputCategory);

        txtInputDatePurchase.setOnClickListener(v -> {
            DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment(this);
            datePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "DatePickerDialogFragment");
        });

        txtInputCategory.setOnClickListener(v -> {
            DialogAddCategoryFragment dialogAddCategoryFragment = new DialogAddCategoryFragment(this);
            dialogAddCategoryFragment.show(requireActivity().getSupportFragmentManager(), "DialogAddCategoryFragment");
        });

        EditText txtInputProductName= root.findViewById(R.id.txtInputProductName);
        EditText txtInputCost = root.findViewById(R.id.txtInputCost);
        EditText txtInputAmount = root.findViewById(R.id.txtInputAmount);

        Button btnConfirmPurchase = root.findViewById(R.id.btnConfirmPurchase);

        btnConfirmPurchase.setOnClickListener(v -> {
            float cost = -1;
            int amount = -1;

            String inputProductName = txtInputProductName.getText().toString();
            String inputCost = txtInputCost.getText().toString();
            String inputAmount = txtInputAmount.getText().toString();
            String inputDatePurchase = txtInputDatePurchase.getText().toString();
            String inputCategory = txtInputCategory.getText().toString();
            String animal = spinnerAnimals.getSelectedItem().toString();

            boolean isEmpty = inputProductName.equals("") || inputCost.equals("")
                    || inputAmount.equals("") || inputDatePurchase.equals("")
                    || inputCategory.equals("");

            if(!isEmpty) {
                boolean isCorrectInput = true;

                try {
                    cost = Float.parseFloat(inputCost);
                    amount = Integer.parseInt(inputAmount);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(),
                            "Il campo quantità o costo deve essere numerico",
                            Toast.LENGTH_SHORT).show();
                    isCorrectInput = false;
                }

                if (isCorrectInput && cost > 0 && amount > 0) {
                    String microchip = animal.split(" - ")[1];
                    listener.onDialogAddPurchaseFragmentDismissed(new Purchase(
                            microchip, inputProductName,
                            dateSql, inputCategory, cost, amount
                    ));

                    dismiss();
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDateSet(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
        year = view.getYear();
        month = view.getMonth();
        dayOfMonth = view.getDayOfMonth();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat dateToDisplayFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dateToDisplay = dateToDisplayFormat.format(calendar.getTime());

        txtInputDatePurchase.setText(dateToDisplay);

        SimpleDateFormat dateSqlFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateSql = dateSqlFormat.format(calendar.getTime());
    }

    @Override
    public void onDialogAddCategoryFragmentListener(String categoryName) {
        txtInputCategory.setText(categoryName);
    }
}
