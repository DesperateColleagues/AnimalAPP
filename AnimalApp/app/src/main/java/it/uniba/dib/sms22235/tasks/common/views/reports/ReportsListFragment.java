package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.ReportsAdapter;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.CustomBsdDialog;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class ReportsListFragment extends Fragment {

    private boolean isMine;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private transient NavController navController;

    private Button btnAddReport;

    ReportsListFragment(boolean isMine, NavController navController) {
        this.isMine = isMine;
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();
        auth = ((NavigationActivityInterface) requireActivity()).getAuthInstance();

        return inflater.inflate(R.layout.fragment_reports_list, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAddReport = view.findViewById(R.id.btnAddReport);

        btnAddReport.setOnClickListener(v ->
                navController.navigate(R.id.action_reportsDashboardFragment_to_reportDetailsFragment));

        if (!isMine) {
            btnAddReport.setVisibility(View.GONE);
        }

        ReportsAdapter reportsAdapter = new ReportsAdapter();
        reportsAdapter.setContext(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerReports);

        if (isMine) {
            db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                    .whereEqualTo(KeysNamesUtils.ReportsFields.REPORTER, Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                        List<DocumentSnapshot> reportsSnapshot = queryDocumentSnapshots.getDocuments();
                        ArrayList<Report> reportsList = new ArrayList<>();

                        if (reportsSnapshot.size() > 0) {
                            for (DocumentSnapshot snapshot : reportsSnapshot) {
                                reportsList.add(Report.loadReport(snapshot));
                            }

                            reportsAdapter.setReportsList(reportsList);

                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                            recyclerView.setAdapter(reportsAdapter);

                            reportsAdapter.setOnItemClickListener(report -> {
                                CustomBsdDialog customBsdDialog = new CustomBsdDialog();

                                customBsdDialog.setOnUpdateRequestListener(() -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.REPORT_UPDATE, report);
                                    bundle.putBoolean(KeysNamesUtils.BundleKeys.REPORT_MODE_ADD, false);
                                    navController.navigate(R.id.action_reportsDashboardFragment_to_reportDetailsFragment, bundle);
                                    customBsdDialog.dismiss();
                                });

                                customBsdDialog.setOnConfirmRequestListener(() -> {
                                        report.setCompleted(true);

                                        db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                                                .document(report.getReportId())
                                                .set(report)
                                                .addOnSuccessListener(unused -> {
                                                    reportsAdapter.notifyDataSetChanged();
                                                });

                                });

                                customBsdDialog.show(getChildFragmentManager(), "CustomBsdDialog");
                            });
                        }
                    });
        } else {
            db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                    .whereNotEqualTo(KeysNamesUtils.ReportsFields.REPORTER, Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                        List<DocumentSnapshot> reportsSnapshot = queryDocumentSnapshots.getDocuments();
                        ArrayList<Report> reportsList = new ArrayList<>();

                        if (reportsSnapshot.size() > 0) {
                            for (DocumentSnapshot snapshot : reportsSnapshot) {
                                reportsList.add(Report.loadReport(snapshot));
                            }

                            reportsAdapter.setReportsList(reportsList);

                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));

                            recyclerView.setAdapter(reportsAdapter);
                        }
                    });
        }
    }
}
