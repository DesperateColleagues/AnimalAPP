package it.uniba.dib.sms22235.tasks.passionate.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.purchases.GridViewCategoryAdapter;

/**
 * This dialog is used to show purchases categories
 * */
public class DialogAddCategoryFragment extends DialogFragment {

    /**
     * Specify the action that occur in category selection
     * */
    public interface DialogAddCategoryFragmentListener {
        /**
         * This callback is called when a category is selected
         *
         * @param categoryName the name of the category
         * */
        void onDialogAddCategoryFragmentListener(String categoryName);
    }

    private final DialogAddCategoryFragmentListener listener;

    public DialogAddCategoryFragment(DialogAddCategoryFragmentListener listener) {
        this.listener = listener;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogTheme);

        LayoutInflater inflater = requireActivity().getLayoutInflater();//get the layout inflater
        View root = inflater.inflate(R.layout.fragment_dialog_add_category, null);//inflate the layout of the view with this new layout

        GridView gridView = root.findViewById(R.id.gridViewCategories);//find the grid view
        builder.setView(root);

        @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(R.string.seleziona_categoria);

        // Instantiate the grid view adapter
        GridViewCategoryAdapter gridViewCategoryAdapter = new GridViewCategoryAdapter(getContext());
        gridView.setAdapter(gridViewCategoryAdapter); // Set the adapter for the grid view

        gridView.setOnItemClickListener((parent, view, position, id) -> {

                listener.onDialogAddCategoryFragmentListener(
                        (String) gridViewCategoryAdapter.getItem(position)
                );

                dismiss();
        });

        return builder.create();
    }
}
