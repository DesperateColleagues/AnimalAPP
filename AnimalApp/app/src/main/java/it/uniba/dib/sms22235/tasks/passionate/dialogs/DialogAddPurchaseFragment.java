package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        // Set dialog title
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(R.string.inserimento_spesa);
        builder.setCustomTitle(titleView);

        // Set the spinner items
        spinnerAnimals = root.findViewById(R.id.spinnerAnimalsAddPurchase);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, animaList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnimals.setAdapter(spinnerAdapter);

        txtInputDatePurchase = root.findViewById(R.id.txtInputDatePurchase);
        txtInputCategory = root.findViewById(R.id.txtInputCategory);

        // Start the date picker dialog
        txtInputDatePurchase.setOnClickListener(v -> {
            DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment(this);
            datePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "DatePickerDialogFragment");
        });

        // Start the dialog to insert a category
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
            long distance = -1;

            String inputProductName = txtInputProductName.getText().toString();
            String inputCost = txtInputCost.getText().toString();
            String inputAmount = txtInputAmount.getText().toString();
            String inputDatePurchase = txtInputDatePurchase.getText().toString();
            String inputCategory = txtInputCategory.getText().toString();
            String animal = spinnerAnimals.getSelectedItem().toString();

            SimpleDateFormat dateSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
            try {
                Date d1 = dateSDF.parse(inputDatePurchase);
                Date d2 = dateSDF.parse(dateSDF.format(new Date()));
                distance = d2.getTime() - d1.getTime();
                Log.e("AnimalAPP - Date Parsing", "DialogAddPurchaseFragment:119 - Current date: " + d2.getTime());
                Log.e("AnimalAPP - Date Parsing","DialogAddPurchaseFragment:120 - Product date: " + d1.getTime());
            } catch (ParseException e) {
                Log.e("AnimalAPP - Date Parsing Error","DialogAddPurchaseFragment:122" + e.getMessage());
            }

            boolean isEmpty = inputProductName.equals("") || inputCost.equals("")
                    || inputAmount.equals("") || inputDatePurchase.equals("")
                    || inputCategory.equals("");

            if(!isEmpty) {
                boolean isCorrectInput = true;
                // Check numeric fields correctness
                try {
                    cost = Float.parseFloat(inputCost);
                    amount = Integer.parseInt(inputAmount);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(),
                            getString(R.string.costo_quantita_non_numero),
                            Toast.LENGTH_SHORT).show();
                    isCorrectInput = false;
                }

                if (isCorrectInput && cost > 0 && amount > 0 && distance >= 0) {
                    // Extract the microhip code from the string
                    String microchip = animal.split(" - ")[1];
                    listener.onDialogAddPurchaseFragmentDismissed(new Purchase(
                            microchip, inputProductName,
                            dateSql, inputCategory, cost, amount
                    ));
                    dismiss();
                } else if (distance < 0) {
                    Toast.makeText(getContext(), getString(R.string.error_date), Toast.LENGTH_LONG).show();
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

        SimpleDateFormat dateSqlFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateSql = dateSqlFormat.format(calendar.getTime());
        txtInputDatePurchase.setText(dateSql);
    }

    @Override
    public void onDialogAddCategoryFragmentListener(String categoryName) {
        txtInputCategory.setText(categoryName);
    }
}
