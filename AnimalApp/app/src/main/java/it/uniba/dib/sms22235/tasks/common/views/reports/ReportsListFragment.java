package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.commonoperations.ReportsAdapter;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;

/**
 * This fragment is used to show a list of reports. The class manages whether the user selects
 * his own reports or the communities ones.
 *
 * @author Giacomo Detomaso - DesperateColleagues2.5
 * */
public class ReportsListFragment extends Fragment {

    private boolean isMine;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private ArrayList<Report> communityReportsList;
    private ArrayList<Report> reportsListFiltered;

    private int counterTouchKm;
    private final double [] distanceMeters = {2000, 5000, 10000, 25000};

    private Location mLocation;
    private FusedLocationProviderClient client;

    private ReportsAdapter reportsAdapter;
    private ReportsAdapter reportsAdapterFiltered;

    private RecyclerView recyclerView;

    private ManageNavigationReports manageNavigationReports;

    private ReportsListFragmentListener listener;

    public void setManageNavigationReports(ManageNavigationReports manageNavigationReports) {
        this.manageNavigationReports = manageNavigationReports;
    }

    /**
     * This interface is used to perform navigation to various fragments of the reports section.
     * The class that implements this interface must perform the navigation indicates by the contract.
     * */
    public interface ManageNavigationReports {
        /**
         * This method is used to navigate to the report detail fragment
         *
         * @param bundle a bundle of data to pass to the fragment
         * */
        void navigateToReportDetail(Bundle bundle);

        /**
         * This method is used to navigate to the fragment that allows user to add a new report
         * */
        void navigateToAddNewReport();

        /**
         * This method is used to navigate to the fragment that allows user to add a new report
         *
         * @param bundle a bundle of data to pass to the fragment
         * */
        void navigateToAddNewReport(Bundle bundle);
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            this.isGranted = true;
        } else {
            Snackbar snackbar = Snackbar.make(getView(),getResources().getString(R.string.location_permission),Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            TypedValue value = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.windowBackground, value, true);
            snackbarView.setBackgroundColor(value.data);
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    textView.setTextColor(Color.WHITE);
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    textView.setTextColor(Color.BLACK);
                    break;
            }
            textView.setTextSize(15);
            snackbar.show();
            this.isGranted = false;
        }
    });

    private boolean isGranted;

    public ReportsListFragment() {
        communityReportsList = new ArrayList<>();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (ReportsListFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();
        auth = ((NavigationActivityInterface) requireActivity()).getAuthInstance();

        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        Bundle arguments = getArguments();

        if (arguments != null) {
            isMine = arguments.getBoolean("isMine");
        }

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
                manageNavigationReports.navigateToAddNewReport());

        // Manage community requests
        if (!isMine) {
            btnAddReport.setVisibility(View.GONE);

            Button btnDistance = view.findViewById(R.id.btnDistance);
            LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

            String permissionAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
            String permissionAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;

            btnDistance.setOnClickListener(v -> {
                // Before doing any kind of operations that requires position access, it is
                // needed to check if the permissions are granted. It is used the classic pattern
                // for permission's requests, with the rationale too. The permissions requested are
                // ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION
                if (ActivityCompat.checkSelfPermission(getContext(), permissionAccessFineLocation) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getContext(), permissionAccessCoarseLocation) == PackageManager.PERMISSION_GRANTED || isGranted) {

                    // If the permission is granted and GPS provider is enabled, it is possible
                    // to perform filtering
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        reportsAdapterFiltered = new ReportsAdapter();
                        reportsAdapterFiltered.setContext(requireContext());

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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                        @SuppressLint("InflateParams")
                        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
                        TextView titleText = titleView.findViewById(R.id.dialog_title);
                        titleText.setText("Abilita posizione");
                        builder.setCustomTitle(titleView);
                        builder.setMessage("Per accedere a questa funzione, è necessario abilitare la posizione");
                        builder.setPositiveButton("Conferma", (dialog, which) -> {
                            // When location service is not enabled open location setting
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            dialog.dismiss();
                        }).setNegativeButton("Annulla", ((dialog, which) -> {
                            dialog.dismiss();
                        }));
                        builder.create().show();
                    }

                } else if (shouldShowRequestPermissionRationale(permissionAccessFineLocation)) {
                    // Prepare the explanation message
                    String info = "Il permesso <b>POSIZIONE</b> è essenziale per poter gestire tutte" +
                            " le informazioni relative alle segnalazione. Senza di esso non ti sarà possibile:<br> " +
                            "<br>• Inserire una nuova segnalazione" +
                            "<br>• Filtrare le segnalazioni esistenti";

                    @SuppressLint("InflateParams")
                    View titleView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialogs_title, null);
                    TextView titleText = titleView.findViewById(R.id.dialog_title);
                    titleText.setText("Perchè accettare il permesso");

                    DialogEntityDetailsFragment dialogEntityDetailsFragment = new DialogEntityDetailsFragment(info);

                    dialogEntityDetailsFragment.setTitleView(titleView);
                    dialogEntityDetailsFragment.setPositiveButton("Chiudi", (dialog, which) -> dialog.dismiss());
                    dialogEntityDetailsFragment.setNegativeButton("Chiedi permesso", ((dialog, which) -> {
                        requestPermissionLauncher.launch(permissionAccessFineLocation);
                        dialog.dismiss();
                    }));

                    dialogEntityDetailsFragment.show(getChildFragmentManager(), "DialogEntityDetailsFragment");

                } else {
                    requestPermissionLauncher.launch(permissionAccessFineLocation);
                }
            });
       } else {
            view.findViewById(R.id.linearLocationFilter).setVisibility(View.GONE);
        }

        if (isMine) {
            // Manage reports of the current logged user
            listener.onPersonalRequestMode(
                    db,
                    auth.getCurrentUser().getEmail(),
                    reportsAdapter,
                    recyclerView,
                    getChildFragmentManager(),
                    getContext(),
                    manageNavigationReports
            );
        } else {
            // Manage community's reports
            communityReportsList = new ArrayList<>();

            listener.onCommunityRequestMode(
                    db,
                    auth.getCurrentUser().getEmail(),
                    communityReportsList,
                    reportsAdapter,
                    recyclerView,
                    getContext(),
                    manageNavigationReports
            );
        }
    }

    /**
     * This method is used to filter the reports by the current's user location
     * */
    @SuppressLint({"MissingPermission", "NotifyDataSetChanged"})
    public void filterByCurrentLocation() {
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

                        // Fill the report filtered ArrayList
                        filter(mLocation);
                    }
                };

                // Request location updates
                client.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper());
                } else {
                    filter(mLocation);
                }

                if (reportsListFiltered.size() > 0) {
                    reportsAdapterFiltered.setReportsList(reportsListFiltered);
                    recyclerView.setAdapter(reportsAdapterFiltered);
                }
            });
    }

    /**
     * This method is used to perform filtering based on current location
     *
     * @param currentLocation the current location of the user
     * */
    public void filter(Location currentLocation) {
        if (counterTouchKm != 4) {
            for (Report report : communityReportsList) {
                Location reportLocation = new Location(LocationManager.GPS_PROVIDER);

                reportLocation.setLatitude(report.getLat());
                reportLocation.setLongitude(report.getLon());

                if (reportLocation != null) {
                    double distance = reportLocation.distanceTo(currentLocation);

                    if (distance < distanceMeters[counterTouchKm]) {
                        reportsListFiltered.add(report);
                    }
                } else {
                    Toast.makeText(getContext(), "Impossibile eseguire il filtraggio. Provare a riavviare l'applicazione", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
