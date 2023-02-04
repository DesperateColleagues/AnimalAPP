package it.uniba.dib.sms22235.tasks.common.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import it.uniba.dib.sms22235.R;

public class DialogEntityDetailsFragment extends DialogFragment{

    private final String info;
    private View titleView = null;

    private String positiveButtonText;
    private String negativeButtonText;

    private boolean isPositiveActionSet = false;
    private boolean isNegativeActionSet = false;

    private DialogInterface.OnClickListener positiveAction;
    private DialogInterface.OnClickListener negativeAction;

    /**
     * This method is used to set a custom title to the dialog
     *
     * @param titleView the title
     * */
    public void setTitleView(View titleView) {
        this.titleView = titleView;
    }

    /**
     * This method is used to set a positive action to the button
     *
     * @param text the text of the button
     * @param action the action that the button will perform
     */
    public void setPositiveButton(String text, DialogInterface.OnClickListener action) {
        positiveAction = action;
        positiveButtonText = text;
        isPositiveActionSet = true;
    }

    /**
     * This method is used to set a negative action to the button
     *
     * @param text the text of the button
     * @param action the action that the button will perform
     */
    public void setNegativeButton(String text, DialogInterface.OnClickListener action) {
        negativeButtonText = text;
        negativeAction = action;
        isNegativeActionSet = true;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public DialogEntityDetailsFragment(String info) {
        this.info = info;
    }

    @SuppressLint("InflateParams")
    @NonNull
    public Dialog onCreateDialog(Bundle SavedInstanceBundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                getContext(),
                R.style.AlertDialogTheme);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_dialog_entity_details, null);

        builder.setView(root);

        // Set dialog title
        if (titleView == null) {
            titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_title);
            titleText.setText("Dettagli");
        }

        builder.setCustomTitle(titleView);

        if (isPositiveActionSet) {
            builder.setPositiveButton(positiveButtonText, positiveAction);
        }

        if (isNegativeActionSet) {
            builder.setNegativeButton(negativeButtonText, negativeAction);
        }

        TextView txtEntityInfo = root.findViewById(R.id.txtEntityInfo);

        txtEntityInfo.setText((Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY)));

        return builder.create();
    }

}