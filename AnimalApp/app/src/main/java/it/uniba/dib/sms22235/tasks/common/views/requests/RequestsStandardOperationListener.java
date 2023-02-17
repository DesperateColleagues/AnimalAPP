package it.uniba.dib.sms22235.tasks.common.views.requests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.commonoperations.RequestAdapter;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.common.dialogs.requests.BsdDialogQr;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This interface describes the standards operations of the requests management system
 * */
public interface RequestsStandardOperationListener {

    /**
     * This method is used to perform an action to the animal request
     *
     * @param request the request
     * @param db the db
     * @param childFragmentManager the manager of the Dialog to be shown
     * */
    default void manageAnimalRequest(@NonNull Request request, @NonNull FirebaseFirestore db,
                                     FragmentManager childFragmentManager) {
        db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, request.getAnimal().split(" - ")[1])
                .get()
                .addOnSuccessListener(query -> {
                    List<DocumentSnapshot> snapshots = query.getDocuments();

                    if (snapshots.size() > 0) {
                        Animal animal = Animal.loadAnimal(snapshots.get(0));
                        BsdDialogQr bsdDialogQr = new BsdDialogQr(animal.getMicrochipCode(), animal.getName(), animal.getOwner(), true);
                        bsdDialogQr.show(childFragmentManager, "BsdDialogQr");
                    }
                });
    }

    /**
     * This method is used to update the animal temporary residence
     *
     * @param residence the residence of the animal
     * @param context the context of the application
     * @param db the instance of the database
     * */
    default void updateAnimalResidence(@NonNull AnimalResidence residence,
                                       Context context, @NonNull FirebaseFirestore db) {
        String id = residence.getAnimal() + "_" + residence.getResidenceOwner() + "_" + residence.getStartDate();

        db.collection(KeysNamesUtils.CollectionsNames.RESIDENCE)
                .document(id).set(residence)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(context, context.getResources().getString(R.string.nuova_backbench_inserita_successo),
                                Toast.LENGTH_SHORT).show());
    }
    /**
     * This method is used to load all the request and to initialize the view with all
     * the request of the app users, without the ones of the current logged user
     *
     * @param reqList the array list of the request
     * @param subRequestsList the array list of the filtered request
     * @param db the instance of the database
     * @param auth the instance of the authentication object
     * @param adapter the adapter of the request's list
     * @param recyclerView the recycler
     * */
    default void loadRequest(ArrayList<Request> reqList, ArrayList<Request> subRequestsList,
                             @NonNull FirebaseFirestore db, FirebaseAuth auth,
                             RequestAdapter adapter, RecyclerView recyclerView, Context context) {
        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshots = task.getResult();

                        if (!snapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : snapshots.getDocuments()){
                                Request request = Request.loadRequest(documentSnapshot);
                                reqList.add(request);
                            }

                            // Load the other's user request
                            subRequestsList.clear();

                            // Scan global request to create the sub list
                            for (Request r : reqList) {
                                if (!(context instanceof PassionateNavigationActivity)) {
                                    if (
                                            !r.getUserEmail().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail()) &&
                                            !r.getIsCompleted() &&
                                            r.getRequestType().equals(KeysNamesUtils.RequestFields.R_TYPE_HELP_OFFER)
                                    ) {
                                        subRequestsList.add(r);
                                    }
                                } else {
                                    if (!r.getUserEmail().equals(Objects.requireNonNull(auth.getCurrentUser()).getEmail()) && !r.getIsCompleted()) {
                                        subRequestsList.add(r);
                                    }
                                }
                            }

                            // Update data set
                            adapter.setRequestsList(subRequestsList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(
                                    context, LinearLayoutManager.VERTICAL, false));
                        }
                    }
                });
    }

    /**
     * This method is used to perform the operation to save the request into db and update
     * the view
     *
     * @param request the request to add
     * @param animalMicrochip the microchip of the animal
     * @param requestsList the array list of the request
     * @param subRequestsList the array list of the filtered request
     * @param db the instance of the database
     * @param auth the instance of the authentication object
     * @param adapter the adapter of the request's list
     * */
    @SuppressLint("NotifyDataSetChanged")
    default void onRequestAdded(@NonNull Request request, String animalMicrochip,
                                @NonNull FirebaseAuth auth, @NonNull FirebaseFirestore db,
                                @NonNull ArrayList<Request> requestsList, ArrayList<Request> subRequestsList,
                                Context context, RequestAdapter adapter, boolean isMine) {

        String email = Objects.requireNonNull(auth.getCurrentUser()).getEmail();
        request.setUserEmail(email);
        request.setAnimal(animalMicrochip);

        requestsList.add(request);

        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                .document(request.getId())
                .set(request)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(context, context.getResources().getString(R.string.nuova_richiesta_inserita_successo), Toast.LENGTH_SHORT).show());

        if (isMine) {
            subRequestsList.add(request);
            adapter.setRequestsList(subRequestsList);
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * This method is used to perform an action to the animal request
     *
     * @param request the request
     * @param db the db
     * @param context the context of the app
     * @param adapter the adapter of request's list
     * */
    @SuppressLint("NotifyDataSetChanged")
    default void onRequestConfirmed(@NonNull Request request, @NonNull FirebaseFirestore db,
                                    RequestAdapter adapter, Context context) {
        db.collection(KeysNamesUtils.CollectionsNames.REQUESTS)
                .document(request.getId())
                .set(request)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context,
                            context.getResources().getString(R.string.aggiornamento_completo),
                            Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                });
    }
}
