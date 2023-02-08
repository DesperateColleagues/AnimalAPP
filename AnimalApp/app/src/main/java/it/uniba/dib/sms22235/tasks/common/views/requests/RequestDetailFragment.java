package it.uniba.dib.sms22235.tasks.common.views.requests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.views.backbenches.BackBenchFragment;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class RequestDetailFragment extends Fragment {

    private Request request;
    private transient NavController navController;
    private RequestsAnimalTransferOperationsListener listener;

    // Manage Qr scanning
    private final ActivityResultLauncher<ScanOptions> qrDecodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            FirebaseStorage storage = ((NavigationActivityInterface) requireActivity()).getStorageInstance();
            FirebaseFirestore db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();

            // Take the result of QR intent
            String [] split = result.getContents().split(" - ");

            String microchip = split[0];
            String animalName = split[1];
            String oldOwner = split[2];

            String newOwner = ((NavigationActivityInterface) requireActivity()).getUserId();

            // Start the change owner operations by updating the DB entry that corresponds
            // to the decoded QR fields

            if (microchip.equals(request.getAnimal().split(" - ")[1])) {
                listener.transferOperations(
                        db, storage, newOwner, microchip, animalName,
                        oldOwner, getResources().getString(R.string.ricarica_sessione),
                        getContext(), requireActivity(), request);
            } else {
                Toast.makeText(getContext(), "L'animale scansionato non corrisponde alla richiesta!" +
                                "Riprovare inquadrando il codice QR corretto.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (RequestsAnimalTransferOperationsListener) context;
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
        Bundle bundle = getArguments();

        if (bundle != null) {
            request = (Request) bundle.getSerializable(KeysNamesUtils.CollectionsNames.REQUESTS);
        }

        assert container != null;
        navController = Navigation.findNavController(container);

        return inflater.inflate(R.layout.fragment_request_details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);
        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.GONE);

        Button sendMail = view.findViewById(R.id.btnSendMail);

        TextView txtRequestDetailsTitle = view.findViewById(R.id.txtRequestDetailsTitle);
        TextView txtRequestDetailsEntityType = view.findViewById(R.id.txtRequestDetailsEntityType);
        TextView txtRequestDetailsBody = view.findViewById(R.id.txtRequestDetailsBody);

        txtRequestDetailsTitle.setText(request.getRequestTitle());
        txtRequestDetailsBody.setText(request.getRequestBody());

        // Setup request details messages
        txtRequestDetailsEntityType.setText(
                new StringBuilder()
                        .append(getContext().getResources().getString(R.string.operation))
                        .append(": ")
                        .append(request.getRequestType())
        );

        sendMail.setOnClickListener(v -> {
            composeEmail(new String [] {request.getUserEmail()}, request.getRequestTitle());
        });

        // Manage backbenches requests
        if (request.getRequestType().equals("Offerta stallo")) {

            FrameLayout frameLayout = view.findViewById(R.id.frameBackbench);
            frameLayout.setVisibility(View.VISIBLE);

            view.findViewById(R.id.infoDivider).setVisibility(View.VISIBLE);
            view.findViewById(R.id.txtRequestDetailsTitle2).setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.frameBackbench, new BackBenchFragment(request.getUserEmail()));
            transaction.commit();
        }

        // Manage animal request
        if (request.getRequestType().equals("Offerta animale")) {
            Button btnShowAnimalProfile = view.findViewById(R.id.btnShowAnimalProfile);
            btnShowAnimalProfile.setOnClickListener(v -> {
                ((NavigationActivityInterface) requireActivity()).getFireStoreInstance()
                        .collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                        .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, request.getAnimal().split(" - ")[1])
                        .get()
                        .addOnSuccessListener(query -> {
                            Animal animal = Animal.loadAnimal(query.getDocuments().get(0));
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, animal);
                            bundle.putInt("ViewMode", 1);
                            navController.navigate(R.id.action_request_detail_to_animalProfile, bundle);
                        });
            });

            Button btnConfirmAnimalRequestQr = view.findViewById(R.id.btnConfirmAnimalRequestQr);
            btnConfirmAnimalRequestQr.setOnClickListener(v -> {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Scannerizza il QR code"); // todo translate
                options.setBeepEnabled(false);
                options.setBarcodeImageEnabled(true);
                options.setOrientationLocked(false);

                qrDecodeLauncher.launch(options);
            });

            // Set Buttons visibility
            btnShowAnimalProfile.setVisibility(View.VISIBLE);
            btnConfirmAnimalRequestQr.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void composeEmail(String[] addresses, String subject) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);

            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Impossibile procedere, non è installata un'app di gestione mail", Toast.LENGTH_LONG).show();
        }
    }


}
