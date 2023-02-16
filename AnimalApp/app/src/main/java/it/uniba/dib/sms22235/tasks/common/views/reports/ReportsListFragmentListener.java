package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.adapters.commonoperations.ReportsAdapter;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.tasks.common.dialogs.ReportsBSDialog;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This interface is used to perform default operations that require Firebase in
 * the reports list fragment
 * */
public interface ReportsListFragmentListener {

    /**
     * This method prepares the UI to show community reports
     *
     * @param db an instance of firestore database
     * @param currentUser the current user logged email
     * @param reportsList the list of report to fill
     * @param reportsAdapter used to set up the view
     * @param recyclerView the view where requests will be showed
     * @param context the context of the application
     * @param manageNavigationReports used to perform the navigation when requested by the user
     * */
    default void onCommunityRequestMode(@NonNull FirebaseFirestore db,
                                        String currentUser,
                                        ArrayList<Report> reportsList,
                                        ReportsAdapter reportsAdapter,
                                        RecyclerView recyclerView,
                                        Context context,
                                        ReportsListFragment.ManageNavigationReports manageNavigationReports) {
        db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                .whereNotEqualTo(KeysNamesUtils.ReportsFields.REPORTER, currentUser)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> reportsSnapshot = queryDocumentSnapshots.getDocuments();

                    if (reportsSnapshot.size() > 0) {
                        for (DocumentSnapshot snapshot : reportsSnapshot) {
                            Report report = Report.loadReport(snapshot);

                            if (!report.getCompleted()) {
                                reportsList.add(report);
                            }
                        }

                        // Setup adapter
                        reportsAdapter.setReportsList(reportsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context,
                                RecyclerView.VERTICAL, false));
                        recyclerView.setAdapter(reportsAdapter);

                        // Set a listener that specify what to do when a community reports is clicked
                        reportsAdapter.setOnItemClickListener(report -> {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KeysNamesUtils.BundleKeys.REPORT_SHOW, report);
                            manageNavigationReports.navigateToReportDetail(bundle);
                        });
                    }
                });

    }

    /**
     * This method prepares the UI to show personal reports
     *
     * @param db an instance of firestore database
     * @param currentUser the current user logged email
     * @param reportsAdapter used to set up the view
     * @param recyclerView the view where requests will be showed
     * @param context the context of the application
     * @param fragmentManager fragment manager used to call the update dialog
     * @param navManager used to perform the navigation when requested by the user
     * */
    @SuppressLint("NotifyDataSetChanged")
    default void onPersonalRequestMode(@NonNull FirebaseFirestore db,
                                       String currentUser,
                                       ReportsAdapter reportsAdapter,
                                       RecyclerView recyclerView,
                                       FragmentManager fragmentManager,
                                       Context context,
                                       ReportsListFragment.ManageNavigationReports navManager) {
        db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                .whereEqualTo(KeysNamesUtils.ReportsFields.REPORTER, currentUser)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> reportsSnapshot = queryDocumentSnapshots.getDocuments();
                    ArrayList<Report> reportsList = new ArrayList<>();

                    if (reportsSnapshot.size() > 0) {
                        for (DocumentSnapshot snapshot : reportsSnapshot) {
                            reportsList.add(Report.loadReport(snapshot));
                        }

                        reportsAdapter.setReportsList(reportsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context,
                                RecyclerView.VERTICAL, false));
                        recyclerView.setAdapter(reportsAdapter);

                        // Set a listener that specify what to do when a mine reports is clicked
                        reportsAdapter.setOnItemClickListener(report -> {
                            ReportsBSDialog reportsBSDialog = new ReportsBSDialog();

                            // Action must be performed only for the reports that are not completed
                            if (!report.getCompleted()) {
                                // Manage the update of a request by opening the ReportAddNewFragment
                                // with the value of the report that's about to be modifies
                                reportsBSDialog.setOnUpdateRequestListener(() -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.REPORT_UPDATE, report);
                                    bundle.putBoolean(KeysNamesUtils.BundleKeys.REPORT_MODE_ADD, false);
                                    navManager.navigateToAddNewReport(bundle);
                                    reportsBSDialog.dismiss();
                                });

                                // Manage report's confirmation by updating its reference of the FireStore
                                reportsBSDialog.setOnConfirmRequestListener(() -> {
                                    report.setCompleted(true);

                                    db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                                            .document(report.getReportId())
                                            .set(report)
                                            .addOnSuccessListener(unused -> {
                                                reportsAdapter.notifyDataSetChanged();
                                                reportsBSDialog.dismiss();
                                            });

                                });

                                reportsBSDialog.show(fragmentManager, "CustomBsdDialog");
                            }
                        });
                    }
                });

    }
}
