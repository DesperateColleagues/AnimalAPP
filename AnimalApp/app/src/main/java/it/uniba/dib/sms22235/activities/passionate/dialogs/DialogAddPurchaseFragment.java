package it.uniba.dib.sms22235.activities.passionate.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Purchase;

public class DialogAddPurchaseFragment extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    public interface DialogAddPurchaseFragmentListener {
        void onDialogAddPurchaseFragmentDismissed(Purchase purchase);
    }

    private DialogAddPurchaseFragmentListener listener;
    private EditText txtInputDatePurchase;

    public void setListener(DialogAddPurchaseFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_add_purchase, null);

        builder.setView(root);
        builder.setTitle("Inserimento spesa");

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText txtInputProductName= view.findViewById(R.id.txtInputProductName);
        EditText txtInputCost = view.findViewById(R.id.txtInputCost);
        EditText txtInputAmount = view.findViewById(R.id.txtInputAmount);
        txtInputDatePurchase = view.findViewById(R.id.txtInputDatePurchase);

        txtInputDatePurchase.setOnClickListener(v -> {
            DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment(this);
            datePickerDialogFragment.show(requireActivity().getSupportFragmentManager(), "DatePickerDialogFragment");
        });

        Spinner spinnerAnimals = view.findViewById(R.id.spinnerAnimals);

        Button btnConfirmPurchase = view.findViewById(R.id.btnConfirmPurchase);

        btnConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float cost = -1;
                int amount = -1;

                String inputProductName = txtInputProductName.getText().toString();
                String inputCost = txtInputCost.getText().toString();
                String inputAmount = txtInputAmount.getText().toString();
                String inputDatePurchase = txtInputDatePurchase.getText().toString();
                String animal = spinnerAnimals.getSelectedItem().toString();

                boolean isEmpty = inputProductName.equals("") || inputCost.equals("")
                        || inputAmount.equals("") || inputDatePurchase.equals("");

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

                    if (isCorrectInput && cost > 0 && amount > 0){
                        listener.onDialogAddPurchaseFragmentDismissed(new Purchase(
                                animal, inputProductName, null, cost, amount
                        ));
                    }
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(mCalendar.getTime());
        txtInputDatePurchase.setText(selectedDate);
    }
}
