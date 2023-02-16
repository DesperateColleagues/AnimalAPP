package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This interface is used to notify the events that occur when a new report is added
 * */
public interface ReportAdditionListener {

    /**
     * This method is used to add a new report to firebase
     *
     * @param report the report to be added
     * @param db the db reference
     * @param controller the controller to perform a pop to the back stack
     * @param context the context of the app
     * */
    default void addBaseReport(@NonNull Report report,
                               @NonNull FirebaseFirestore db,
                               NavController controller,
                               Context context) {
        db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                .document(report.getReportId())
                .set(report)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, context.getString(R.string.segnalazione_inserita_successo), Toast.LENGTH_SHORT).show();
                    controller.popBackStack();
                });
    }

    /**
     * This method is used to add a new report to firebase with an image
     *
     * @param report the report to be added
     * @param ownerEmail the owner email
     * @param db the db reference
     * @param storage the storage reference where the pic will be saved
     * @param controller the controller to perform a pop to the back stack
     * @param context the context of the app
     * */
    default void addBaseReportWithImage(@NonNull Report report,
                                        String ownerEmail,
                                        @NonNull FirebaseFirestore db,
                                        @NonNull FirebaseStorage storage,
                                        NavController controller,
                                        Context context) {

        String fileName = report.getReportId();

        // Create the storage tree structure
        String fileReference = KeysNamesUtils.FileDirsNames.REPORT_POST +
                "/" + KeysNamesUtils.FileDirsNames.reportPic(ownerEmail) + "/" + fileName;

        StorageReference storageReference = storage.getReference(fileReference);

        // Give to the user a feedback to wait
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.Widget_App_ProgressDialog);
        progressDialog.setMessage(context.getString(R.string.salvando_immagine));
        progressDialog.show();

        // Start the upload task
        UploadTask uploadTask = storageReference.putFile(Uri.parse(report.getReportHelpPictureUri()));

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage()
                        .getDownloadUrl().addOnCompleteListener(taskUri -> {
                            report.setReportHelpPictureUri(taskUri.getResult().toString());

                            db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                                    .document(report.getReportId())
                                    .set(report)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, context.getString(R.string.segnalazione_inserita_successo), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        controller.popBackStack();
                                    });
                        });
            }
        });

    }
}
