package it.uniba.dib.sms22235.common_views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.ActivityInterface;
import it.uniba.dib.sms22235.adapters.RequestAdapter;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class RequestsFragment extends Fragment implements DialogAddRequest.DialogAddRequestListener {

    private ArrayList<Request> requestsList;
    private RecyclerView requestsRecyclerView;
    private RequestAdapter adapter;
    private transient NavController controller;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RequestAdapter();
        requestsList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        controller = Navigation.findNavController(container);
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ActivityInterface) requireActivity()).getFab().setOnClickListener(v -> {
            DialogAddRequest dialogAddRequest = new DialogAddRequest();
            dialogAddRequest.setListener(this);
            dialogAddRequest.show(getChildFragmentManager(), "DialogAddRequest");
        });


        final String [] requestTypes = {"Animale", "Aiuto", "Stallo"};

        ChipGroup requestsParamsChipGroup = view.findViewById(R.id.requestsParamsChipGroup);
        requestsRecyclerView = view.findViewById(R.id.requestsRecyclerList);

        // Fill chip
        for (String s : requestTypes) {
            @SuppressLint("InflateParams") Chip chip = (Chip) getLayoutInflater()
                    .inflate(R.layout.item_chip_fragment_filter, null);
            chip.setText(s);
            chip.setCloseIcon(null);
            chip.setOnClickListener(v -> chip.setSelected(true));
            requestsParamsChipGroup.addView(chip);
        }

        loadRequest();

        adapter.setOnItemClickListener(request -> {
            // Intent here. Messaging app intent here. Indipendent from the type.
            Bundle bundle = new Bundle();
            bundle.putSerializable("Richiesta", request);
            controller.navigate(R.id.action_passionate_requests_to_passionate_request_detail, bundle);

            //if (request.getRequestType().equals("Animale")) {
                /* if RequestType is animal, we need to visualize a QR code that
                   is used to "metaphorically send" an animal to a new owner.
                   The QR code will be used as a checksum. Hopefully it will contain
                   all the animal data. It will be scanned by another istance of the app
                   (e.g. the phone of the new owner) and checked.
                 */
            /*} else if (request.getRequestType().equals("Aiuto")) {

            } else if (request.getRequestType().equals("Stallo")) {
            }*/
        });
    }

    @Override
    public void onRequestAdded(Request request) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String email = auth.getCurrentUser().getEmail();
        request.setUserEmail(email);

        requestsList.add(request);
        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Richiesta aggiunta correttamente", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadRequest() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();

                        if (!snapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : snapshots.getDocuments()){
                                requestsList.add(Request.loadRequest(documentSnapshot));
                            }
                            adapter.setRequestsList(requestsList);

                            requestsRecyclerView.setAdapter(adapter);
                            requestsRecyclerView.setLayoutManager(new LinearLayoutManager(
                                    getContext(), LinearLayoutManager.VERTICAL, false
                            ));
                        }
                    }
                });
    }
}
