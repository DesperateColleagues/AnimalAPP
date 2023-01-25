package it.uniba.dib.sms22235.tasks.common.views.requests;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.CustomBsdDialog;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.DialogAddRequest;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.DialogRequestBackbench;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.adapters.RequestAdapter;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.BsdDialogQr;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class RequestsFragment extends Fragment implements DialogAddRequest.DialogAddRequestListener,
        DialogRequestBackbench.DialogRequestBackbenchListener {

    private ArrayList<Request> requestsList;
    private ArrayList<Request> subRequestsList;
    private RecyclerView requestsRecyclerView;
    private RequestAdapter adapter;
    private transient NavController controller;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private boolean isMine = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adapter = new RequestAdapter();
        subRequestsList = new ArrayList<>();
        requestsList = new ArrayList<>();

        if (container != null) {
            controller = Navigation.findNavController(container);
        }

        // Get the instances of firebase objects
        auth = FirebaseAuth.getInstance();
        db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();

        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

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

        // initially load requests
        loadRequest();

        Button btnChangeViewRequest = view.findViewById(R.id.btnChangeViewRequest);
        btnChangeViewRequest.setOnClickListener(view1 -> {
            // Check if the visualised data set the one with users' requests
            if (!isMine) {
                btnChangeViewRequest.setText(getResources().getString(R.string.richieste_mie));
                // Load the logged user request
                loadMineRequests();
                isMine = true;
            } else {
                btnChangeViewRequest.setText(getResources().getString(R.string.richieste_altri));
                // Load all users' request
                loadOtherRequests();
                isMine = false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRequestAdded(@NonNull Request request, String animalMicrochip) {

        String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        request.setUserEmail(email);
        request.setAnimal(animalMicrochip);

        requestsList.add(request);

        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                .document(request.getId())
                .set(request)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(getContext(), "Richiesta aggiunta correttamente", Toast.LENGTH_SHORT).show());

        if (isMine) {
            subRequestsList.add(request);
            adapter.setRequestsList(subRequestsList);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * This method is used to load all the request and to initialize the view with all
     * the request of the app users, without the ones of the current logged user
     * */
    private void loadRequest() {
        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();

                        if (!snapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : snapshots.getDocuments()){
                                Request request = Request.loadRequest(documentSnapshot);
                                requestsList.add(request);
                            }
                            loadOtherRequests();
                        }
                    }
                });
    }

    /**
     * Create a sub list from the loaded request list excluding the ones of the
     * current logged user
     * */
    private void loadOtherRequests() {
        subRequestsList.clear();

        // Scan global request to create the sub list
        for (Request r : requestsList) {
            if (!r.getUserEmail().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail()) && !r.getIsCompleted()) {
                subRequestsList.add(r);
            }
        }

        // Update data set
        adapter.setRequestsList(subRequestsList);

        requestsRecyclerView.setAdapter(adapter);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false
        ));

        adapter.setOnItemClickListener(request -> {
            // Intent here. Messaging app intent here. Independent from the type.
            Bundle bundle = new Bundle();
            bundle.putSerializable(KeysNamesUtils.CollectionsNames.REQUESTS, request);

            if (requireActivity() instanceof PassionateNavigationActivity) {
                controller.navigate(R.id.request_detail, bundle);
            } else if (requireActivity() instanceof VeterinarianNavigationActivity) {
                controller.navigate(R.id.request_detail, bundle);
            }
        });
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

        requestsRecyclerView.setAdapter(adapter);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false
        ));

        // Manage the click on logged user's requests
        adapter.setOnItemClickListener(request -> {
            if (!request.getIsCompleted() && request.getRequestType().equals("Aiuto")) {
                confirmRequest(request);
            }

            if (!request.getIsCompleted() && request.getRequestType().equals("Offerta stallo")) {
                manageBackbenchRequest(request);
            }

            if (!request.getIsCompleted() && request.getRequestType().equals("Offerta animale")) {
                manageAnimalRequest(request);
            }

        });
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
                db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                        .document(request.getId())
                        .set(request)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Aggiornamento completato con successo", Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        });
            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            builder.show();
    }

    private void manageBackbenchRequest(Request request) {
        CustomBsdDialog bsdDialogRequest = new CustomBsdDialog();

        bsdDialogRequest.setOnUpdateRequestListener(() -> {
            DialogRequestBackbench dialogRequestBackbench = new DialogRequestBackbench(request);
            dialogRequestBackbench.setListener(this);
            dialogRequestBackbench.show(getChildFragmentManager(), "DialogRequestBackbench");
        });

        bsdDialogRequest.setOnConfirmRequestListener(() -> {
            confirmRequest(request);
        });

        bsdDialogRequest.show(getChildFragmentManager(), "BsdDialogRequest");
    }

    private void manageAnimalRequest(@NonNull Request request) {
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, request.getAnimal().split(" - ")[1])
                .get()
                .addOnSuccessListener(query -> {
                    List<DocumentSnapshot> snapshots = query.getDocuments();

                    if (snapshots.size() > 0) {
                        Animal animal = Animal.loadAnimal(snapshots.get(0));
                        BsdDialogQr bsdDialogQr = new BsdDialogQr(animal.getMicrochipCode(), animal.getName(), animal.getOwner());
                        bsdDialogQr.show(getChildFragmentManager(), "BsdDialogQr");
                    }
                });
    }
    @Override
    public void onValueAdded(@NonNull AnimalResidence residence, Request request) {
        String id = residence.getAnimal() + "_" + residence.getResidenceOwner() + "_" + residence.getStartDate();

         db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                .document(id).set(residence)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Residenza temporanea registrata con successo",
                            Toast.LENGTH_SHORT).show();
                });
    }
}
