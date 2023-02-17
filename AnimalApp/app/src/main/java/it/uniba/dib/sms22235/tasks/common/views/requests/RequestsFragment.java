package it.uniba.dib.sms22235.tasks.common.views.requests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.RequestsBSDialog;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.DialogAddRequest;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.DialogRequestBackbench;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.adapters.commonoperations.RequestAdapter;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment is used to support all the operations of the requests. Requests can be
 * added to the list and be filtered.
 * */
public class RequestsFragment extends Fragment implements
        DialogAddRequest.DialogAddRequestListener,
        DialogRequestBackbench.DialogRequestBackbenchListener {

    private ArrayList<Request> requestsList;
    private ArrayList<Request> subRequestsList;
    private ArrayList<Request> subRequestsListFiltered;

    private RecyclerView requestsRecyclerView;
    private RequestAdapter adapter;

    private transient NavController controller;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private boolean isMine = false;
    private int selectedChip = 0;

    private RequestsStandardOperationListener listener;

    private final RequestAdapter.OnItemClickListener mineRequestsClickListener = (request) -> {
        if (!request.getIsCompleted() && request.getRequestType().equals(KeysNamesUtils.RequestFields.R_TYPE_HELP_OFFER)) {
            confirmRequest(request);
        }

        if (!request.getIsCompleted() && request.getRequestType().equals(KeysNamesUtils.RequestFields.R_TYPE_BACKBENCH_OFFER)) {
            manageBackbenchRequest(request);
        }

        if (!request.getIsCompleted() && request.getRequestType().equals(KeysNamesUtils.RequestFields.R_TYPE_ANIMAL_OFFER)) {
            manageAnimalRequest(request);
        }
    };

    private final RequestAdapter.OnItemClickListener otherRequestsClickListener = (request) -> {
        // Intent here. Messaging app intent here. Independent from the type.
        Bundle bundle = new Bundle();
        bundle.putSerializable(KeysNamesUtils.CollectionsNames.REQUESTS, request);

        if (requireActivity() instanceof PassionateNavigationActivity) {
            controller.navigate(R.id.request_detail, bundle);
        } else if (requireActivity() instanceof VeterinarianNavigationActivity) {
            controller.navigate(R.id.request_detail, bundle);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (RequestsStandardOperationListener) context;
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
        adapter = new RequestAdapter();
        subRequestsList = new ArrayList<>();
        requestsList = new ArrayList<>();

        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.VISIBLE);
        ((NavigationActivityInterface) requireActivity()).getFab().setImageResource(R.drawable.ic_baseline_add_24);


        if (container != null) {
            controller = Navigation.findNavController(container);
        }

        // Get the instances of firebase objects
        auth = FirebaseAuth.getInstance();
        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();

        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.VISIBLE);

        ((NavigationActivityInterface) requireActivity()).getFab().setOnClickListener(v -> {
            DialogAddRequest dialogAddRequest = new DialogAddRequest();
            dialogAddRequest.setListener(this);
            dialogAddRequest.show(getChildFragmentManager(), "DialogAddRequest");
        });

        ChipGroup requestsParamsChipGroup = view.findViewById(R.id.requestsParamsChipGroup);
        requestsRecyclerView = view.findViewById(R.id.requestsRecyclerList);
        adapter.setContext(requireContext());

        if (!(requireActivity() instanceof PassionateNavigationActivity)) {
            requestsParamsChipGroup.setVisibility(View.GONE);
        }

        requestsParamsChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {

            if (checkedIds.size() > 0) {
                selectedChip = checkedIds.get(0);

                subRequestsListFiltered = new ArrayList<>();

                Chip chip = view.findViewById(selectedChip);

                for (Request request : subRequestsList) {
                    if (request.getRequestType().equals(chip.getText().toString())) {
                        subRequestsListFiltered.add(request);
                    }
                }

                adapter.setRequestsList(subRequestsListFiltered);
                adapter.notifyDataSetChanged();

            } else {
                Log.e("AnimalAPP - Richieste & Offerte", "RequestsFragment:161 - Filtri cancellati");
                adapter.setRequestsList(subRequestsList);
                adapter.notifyDataSetChanged();
                subRequestsListFiltered.clear();
            }
        });

        // Initially load requests
        listener.loadRequest(requestsList, subRequestsList, db, auth,
                adapter, requestsRecyclerView, getContext());
        // Set the default click listener
        adapter.setOnItemClickListener(otherRequestsClickListener);

        Button btnChangeViewRequest = view.findViewById(R.id.btnChangeViewRequest);
        btnChangeViewRequest.setOnClickListener(view1 -> {
            // If is mine is false it means that communities' requests are being showed
            // so the data set will change to the request of the current logged user.
            // Otherwise communities will be shows (is mine = true)
            if (!isMine) {
                btnChangeViewRequest.setText(getResources().getString(R.string.richieste_mie));
                // Load the logged user request
                loadMineRequests();
                isMine = true;

                // Set the correct adapter
                // Manage the click on logged user's requests
                adapter.setOnItemClickListener(mineRequestsClickListener);
                requestsParamsChipGroup.setVisibility(View.GONE);
            } else {
                btnChangeViewRequest.setText(getResources().getString(R.string.richieste_altri));
                // Load all users' request
                loadOtherRequests();
                if (!(requireActivity() instanceof PassionateNavigationActivity)) {
                    requestsParamsChipGroup.setVisibility(View.GONE);
                } else {
                    requestsParamsChipGroup.setVisibility(View.VISIBLE);
                }
                isMine = false;


                // Set the correct adapter
                // Manage the click on other's users requests
                adapter.setOnItemClickListener(otherRequestsClickListener);

            }
            requestsRecyclerView.setAdapter(adapter);
            requestsRecyclerView.setLayoutManager(new LinearLayoutManager(
                    getContext(), LinearLayoutManager.VERTICAL, false
            ));
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRequestAdded(@NonNull Request request, String animalMicrochip) {
        listener.onRequestAdded(request, animalMicrochip,
                auth, db,
                requestsList, subRequestsList,
                getContext(), adapter, isMine);
    }

    /**
     * Create a sub list from the loaded request list excluding the ones of the
     * current logged user
     * */
    private void loadOtherRequests() {
        subRequestsList.clear();

        // Scan global request to create the sub list
        for (Request r : requestsList) {
            if (!(requireActivity() instanceof PassionateNavigationActivity)) {
                if (!r.getUserEmail().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail()) &&
                        !r.getIsCompleted() &&
                        r.getRequestType().equals(KeysNamesUtils.RequestFields.R_TYPE_HELP_OFFER)) {
                    subRequestsList.add(r);
                }
            } else {
                if (!r.getUserEmail().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail()) &&
                        !r.getIsCompleted()) {
                    subRequestsList.add(r);
                }
            }
        }

        // Update data set
        adapter.setRequestsList(subRequestsList);
    }

    /**
     * Create a sub list from the loaded request list excluding the ones app users
     * */
    private void loadMineRequests() {
        subRequestsList.clear();

        // Scan global request to create the sub list
        for (Request r : requestsList) {
            if (r.getUserEmail().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail())) {
                subRequestsList.add(r);
            }
        }

        // Update data set
        adapter.setRequestsList(subRequestsList);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void confirmRequest(@NonNull Request request) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getResources().getString(R.string.conferma_richiesta_messaggio));
            // Set dialog title
            @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title,
                    null);

            TextView titleText = titleView.findViewById(R.id.dialog_title);
            titleText.setText(getResources().getString(R.string.conferma_richiesta_titolo));

            builder.setCustomTitle(titleView);

            builder.setPositiveButton(getResources().getString(R.string.conferma), (dialog, id) -> {
                request.setIsCompleted(true);
                listener.onRequestConfirmed(request, db, adapter, getContext());
            }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());

            builder.show();
    }

    private void manageBackbenchRequest(Request request) {
        RequestsBSDialog bsdDialogRequest = new RequestsBSDialog();

        bsdDialogRequest.setOnUpdateRequestListener(() -> {
            DialogRequestBackbench dialogRequestBackbench = new DialogRequestBackbench(request);
            dialogRequestBackbench.setListener(this);
            dialogRequestBackbench.show(getChildFragmentManager(), "DialogRequestBackbench");
        });

        bsdDialogRequest.setOnConfirmRequestListener(() -> confirmRequest(request));

        bsdDialogRequest.show(getChildFragmentManager(), "BsdDialogRequest");
    }

    private void manageAnimalRequest(@NonNull Request request) {
        listener.manageAnimalRequest(request, db, getChildFragmentManager());
    }

    @Override
    public void onValueAdded(@NonNull AnimalResidence residence, Request request) {
        listener.updateAnimalResidence(residence, getContext(), db);
    }
}
