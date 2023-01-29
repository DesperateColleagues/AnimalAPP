package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.ReportsAdapter;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.CustomBsdDialog;
import it.uniba.dib.sms22235.tasks.common.dialogs.reports.DialogMap;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class ReportsListFragment extends Fragment {

    private final boolean isMine;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final transient NavController navController;

    private ArrayList<Report> reportsList;
    private ArrayList<Report> reportsListFiltered;

    private int counterTouchKm;
    private final double [] distanceMeters = {2000, 5000, 10000, 25000};

    private Location mLocation;
    private FusedLocationProviderClient client;

    private ReportsAdapter reportsAdapter;
    private ReportsAdapter reportsAdapterFiltered;
    private RecyclerView recyclerView;

    ReportsListFragment(boolean isMine, NavController navController) {
        this.isMine = isMine;
        this.navController = navController;
        reportsList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();
        auth = ((NavigationActivityInterface) requireActivity()).getAuthInstance();

        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        return inflater.inflate(R.layout.fragment_reports_list, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportsAdapter = new ReportsAdapter();
        reportsAdapter.setContext(requireContext());
        recyclerView = view.findViewById(R.id.recyclerReports);

        Button btnAddReport = view.findViewById(R.id.btnAddReport);

        btnAddReport.setOnClickListener(v ->
                navController.navigate(R.id.action_reportsDashboardFragment_to_reportDetailsFragment));

        if (!isMine) {
            btnAddReport.setVisibility(View.GONE);

            Button btnDistance = view.findViewById(R.id.btnDistance);
            btnDistance.setOnClickListener(v -> {
                reportsAdapterFiltered = new ReportsAdapter();
                reportsListFiltered = new ArrayList<>();

                String textBtn = "Tutte";

                if (counterTouchKm == 4) {
                    counterTouchKm = 0;
                    recyclerView.setAdapter(reportsAdapter);
                } else {
                    textBtn = (distanceMeters[counterTouchKm] / 1000) + getResources().getString(R.string.chilometri);
                    filterByCurrentLocation();
                    counterTouchKm++;
                }

                btnDistance.setText(textBtn);
            });

        } else {
            view.findViewById(R.id.linearLocationFilter).setVisibility(View.GONE);
        }

        // Manage reports of the current logged user
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

                            // Set a listener that specify what to do when a mine reports is clicked
                            reportsAdapter.setOnItemClickListener(report -> {
                                CustomBsdDialog customBsdDialog = new CustomBsdDialog();

                                // Manage the update of a request by opening the ReportAddNewFragment
                                // with the value of the report that's about to be modifies
                                customBsdDialog.setOnUpdateRequestListener(() -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(KeysNamesUtils.BundleKeys.REPORT_UPDATE, report);
                                    bundle.putBoolean(KeysNamesUtils.BundleKeys.REPORT_MODE_ADD, false);
                                    navController.navigate(R.id.action_reportsDashboardFragment_to_reportDetailsFragment, bundle);
                                    customBsdDialog.dismiss();
                                });

                                // Manage report's confirmation by updating its reference of the FireStore
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
            // Manage community's reports
            db.collection(KeysNamesUtils.CollectionsNames.REPORTS)
                    .whereNotEqualTo(KeysNamesUtils.ReportsFields.REPORTER, Objects.requireNonNull(auth.getCurrentUser()).getEmail())
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                        List<DocumentSnapshot> reportsSnapshot = queryDocumentSnapshots.getDocuments();
                        reportsList = new ArrayList<>();

                        if (reportsSnapshot.size() > 0) {
                            for (DocumentSnapshot snapshot : reportsSnapshot) {
                                reportsList.add(Report.loadReport(snapshot));
                            }

                            reportsAdapter.setReportsList(reportsList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                    RecyclerView.VERTICAL, false));
                            recyclerView.setAdapter(reportsAdapter);

                            // Set a listener that specify what to do when a community reports is clicked
                            reportsAdapter.setOnItemClickListener(report -> {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(KeysNamesUtils.BundleKeys.REPORT_SHOW, report);
                                navController.navigate(R.id.action_reportsDashboardFragment_to_reportDetailFragment, bundle);
                            });
                        }

                    });
        }
    }

    @SuppressLint({"MissingPermission", "NotifyDataSetChanged"})
    private void filterByCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled get last location

            client.getLastLocation().addOnCompleteListener(task -> {
                // Initialize location
                mLocation = task.getResult();

                // If the location is retrieved by the callback displays the DialogMap
                if (mLocation == null) {
                    // Send a request to find the current location
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10000)
                            .setFastestInterval(1000)
                            .setNumUpdates(1);

                    // Use a location callback to access the fetch results
                    LocationCallback locationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            mLocation = locationResult.getLastLocation();
                        }
                    };

                    // Request location updates
                    client.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper());
                }

                // Fill the report filtered ArrayList
                filter(mLocation);

                if (reportsListFiltered.size() > 0) {
                    reportsAdapterFiltered.setReportsList(reportsListFiltered);
                    recyclerView.setAdapter(reportsAdapterFiltered);
                }
            });
        } else {
            // When location service is not enabled open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void filter(Location currentLocation) {
        if (counterTouchKm != 4) {
            for (Report report : reportsList) {
                Location reportLocation = new Location(LocationManager.GPS_PROVIDER);

                reportLocation.setLatitude(report.getLat());
                reportLocation.setLongitude(report.getLon());

                double distance = reportLocation.distanceTo(currentLocation);

                if (distance < distanceMeters[counterTouchKm]) {
                    reportsListFiltered.add(report);
                }
            }
        }
    }
}
