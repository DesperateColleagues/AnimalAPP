package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAddAnimalFragment;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogEditAnimalDataFragment;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.adapters.MessageListAdapter;
import it.uniba.dib.sms22235.entities.operations.InfoMessage;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

public class PassionateProfileFragment extends Fragment implements
        DialogAddAnimalFragment.DialogAddAnimalFragmentListener,
        DialogEditAnimalDataFragment.DialogEditAnimalDataFragmentListener,
        AnimalProfile.AnimalProfileEditListener {

    private PassionateProfileFragment.ProfileFragmentListener listener;
    private String username;
    private transient NavController controller;

    // Manage Qr scanning
    private final ActivityResultLauncher<ScanOptions> qrDecodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(getContext(), "Operazione non andata a buon fine. Controllare i permessi.", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore db = ((NavigationActivityInterface) requireActivity()).getFireStoreInstance();

            // Take the result of QR intent
            String [] split = result.getContents().split(" - ");

            String microchip = split[0];
            String animalName = split[1];
            String oldOwner = split[2];

            // Start the change owner operations by updating the DB entry that corresponds
            // to the decoded QR fields
            db.collection(KeysNamesUtils.CollectionsNames.ANIMALS)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.MICROCHIP_CODE, microchip)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.NAME, animalName)
                    .whereEqualTo(KeysNamesUtils.AnimalFields.OWNER, oldOwner)
                    .get()
                    .addOnSuccessListener(query -> {
                        if (query.size() > 0) {
                            Animal animal = Animal.loadAnimal(query.getDocuments().get(0));

                            Bundle bundle = new Bundle();
                            bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, animal);
                            bundle.putInt("ViewMode", 1);

                            controller.navigate(R.id.action_passionate_profile_to_animalProfile, bundle);
                        }
                    });
        }
    });


    public interface ProfileFragmentListener {
        /**
         * This callback is used to register an animal to Firestore and to update or
         * initialize the recycler view
         *
         * @param animal the animal to register
         * */
        void onAnimalRegistered(Animal animal);
    }

    private AnimalListAdapter animalListAdapter;
    private DialogAddAnimalFragment dialogAddAnimalFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (PassionateProfileFragment.ProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        controller = Navigation.findNavController(container);

        View rootView = inflater.inflate(R.layout.fragment_passionate_profile, container, false);

        RecyclerView messageRecyclerView = rootView.findViewById(R.id.messagesList);
        RecyclerView animalRecycleView = rootView.findViewById(R.id.animalList);
        username = ((PassionateNavigationActivity) requireActivity())
                .getUserId();

        String title = "Benvenuto, " + username;
        ((TextView) rootView.findViewById(R.id.txtPassionateWelcome)).setText(title);

        LinkedHashSet<Animal> animalSet =
                ((PassionateNavigationActivity) requireActivity()).getAnimalSet();

        if (animalSet != null) {
            // Path to internal storage
            String path =
                    KeysNamesUtils.FileDirsNames.BASE_PATH +
                            KeysNamesUtils.FileDirsNames.ROOT_PREFIX +
                            KeysNamesUtils.FileDirsNames.PROFILE_IMAGES;

            // Init the recycler
            animalListAdapter = new AnimalListAdapter(RecyclerView.HORIZONTAL);

            for (Animal animal : animalSet) {
                // Set the animals in the adapter
                animalListAdapter.addAnimal(animal);

                // Load the profile pic preview
                Bitmap image = DataManipulationHelper.loadBitmapFromStorage(path,
                        animal.getMicrochipCode() + ".png");

                // Add the profile pic preview to the adapter
                animalListAdapter.addPic(image);
            }

            ArrayList<InfoMessage> messages = new ArrayList<>();

            // Setting up the recyclerView to show app messages on the profile page
            MessageListAdapter messageListAdapter = new MessageListAdapter(buildStandardMessages(messages));
            messageRecyclerView.setAdapter(messageListAdapter);
            messageRecyclerView.setLayoutManager(new LinearLayoutManager(
                    getContext(), RecyclerView.HORIZONTAL, false));

            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(messageRecyclerView);

            messageListAdapter.setOnItemClickListener(message -> {

            });

            messageRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), messageRecyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    String selection = messageListAdapter.getMessageAtPosition(position).getType();

                    if (selection.equals(KeysNamesUtils.CollectionsNames.RESERVATIONS)) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(
                                KeysNamesUtils.BundleKeys.PASSIONATE_RESERVATIONS,
                                ((PassionateNavigationActivity) requireActivity())
                                        .getPassionateReservationsList());
                        controller.navigate(R.id.action_passionate_profile_to_BookedReservationsFragment, bundle);
                    } else if (selection.equals(KeysNamesUtils.CollectionsNames.REPORTS)) {
                        controller.navigate(R.id.action_passionate_profile_to_reportsDashboardFragment);
                    } else if (selection.equals(KeysNamesUtils.RolesNames.VETERINARIAN)) {
                        Bundle bundle = new Bundle();

                        List<Veterinarian> veterinarians = ((PassionateNavigationActivity) requireActivity()).getVeterinarianList();
                        bundle.putSerializable(KeysNamesUtils.BundleKeys.VETERINARIANS_LIST,
                                (Serializable) veterinarians);

                        controller.navigate(R.id.action_passionate_profile_to_passionateVeterinarianListFragment, bundle);
                    } else if (selection.equals(KeysNamesUtils.RolesNames.PRIVATE_ORGANIZATION)) {
                        Bundle bundle = new Bundle();

                        List<Organization> organizations = ((PassionateNavigationActivity) requireActivity()).getOrganizationList();
                        bundle.putSerializable(KeysNamesUtils.BundleKeys.ORGANIZATIONS_LIST, (Serializable) organizations);

                        controller.navigate(R.id.action_passionate_profile_to_passionateOrganizationListFragment, bundle);
                    } else if (selection.equals(KeysNamesUtils.CollectionsNames.POKE_LINK)) {
                        controller.navigate(R.id.action_passionate_profile_to_passionatePokAnimalList);
                    } else if (selection.equals(KeysNamesUtils.RolesNames.ANIMAL)) {
                        ScanOptions options = new ScanOptions();
                        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                        options.setPrompt("Scannerizza il QR code"); // todo translate
                        options.setBeepEnabled(false);
                        options.setBarcodeImageEnabled(true);
                        options.setOrientationLocked(false);

                        qrDecodeLauncher.launch(options);
                    }
                    else if (selection.equals("notNow")){
                        notNowDialog(
                                username + " " + getResources().getString(R.string.not_now),
                                getResources().getString(R.string.not_now_message),
                                getResources().getString(R.string.not_now_button)
                                );
                    } else if (selection.equals("ascanio")){
                        notNowDialog(
                                username + " " + getResources().getString(R.string.ascanio),
                                getResources().getString(R.string.ascanio_message),
                                getResources().getString(R.string.ascanio_button)
                        );
                    }
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            animalRecycleView.setAdapter(animalListAdapter);
            animalRecycleView.setLayoutManager(new LinearLayoutManager(
                    getContext(), RecyclerView.HORIZONTAL, false));

            DialogEditAnimalDataFragment dialogEditAnimalDataFragment = new DialogEditAnimalDataFragment();
            dialogEditAnimalDataFragment.setListener(this);

            animalRecycleView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), animalRecycleView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, animalListAdapter.getAnimalAtPosition(position));
                    bundle.putInt("ViewMode", 0);
                    controller.navigate(R.id.action_passionate_profile_to_animalProfile, bundle);
                }

                @Override
                public void onLongClick(View view, int position) {
                    //not needed
                }
            }));

            ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.VISIBLE);
            ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
                dialogAddAnimalFragment = new DialogAddAnimalFragment();
                dialogAddAnimalFragment.setListener(this);
                dialogAddAnimalFragment.show(getParentFragmentManager(), "DialogAddAnimalFragment");
            });
        }

        return rootView;
    }

    @NonNull
    @Contract("_ -> param1")
    private ArrayList<InfoMessage> buildStandardMessages(@NonNull ArrayList<InfoMessage> messages) {
        InfoMessage findings = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_reports_text), R.drawable.warningsign, KeysNamesUtils.CollectionsNames.REPORTS);
        InfoMessage qrScan = new InfoMessage("Scannerizza QR", R.drawable.ic_baseline_qr_code_scanner_24, KeysNamesUtils.RolesNames.ANIMAL);
        InfoMessage showVeterinarians = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_vet_list_text), R.drawable.fra_rrc_doctor_no_green, KeysNamesUtils.RolesNames.VETERINARIAN);
        InfoMessage showOrganizations = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_org_list_text), R.drawable.fra_rrc_organization_no_green, KeysNamesUtils.RolesNames.PRIVATE_ORGANIZATION);
        InfoMessage recentReservations = new InfoMessage(getResources().getString(R.string.tutti_appuntamenti_recenti), 0, KeysNamesUtils.CollectionsNames.RESERVATIONS);
        InfoMessage pokeLinks = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_pokelinks_text), R.drawable.pokanimal_logo, KeysNamesUtils.CollectionsNames.POKE_LINK);

        messages.add(findings);
        messages.add(showVeterinarians);
        messages.add(showOrganizations);
        messages.add(qrScan);
        messages.add(pokeLinks);
        messages.add(recentReservations);

        notNow(messages);
        return messages;
    }

    @Override
    public void onDialogAddAnimalDismissed(Animal animal) {
        // Add the new animal to the adapter
        animalListAdapter.addAnimal(animal);
        // When the animal is added no profile pic is provided
        animalListAdapter.addPic(null);
        animalListAdapter.notifyItemInserted(animalListAdapter.getItemCount());
        // Execute listener method to perform data saving
        listener.onAnimalRegistered(animal);
    }

    @Override
    public void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal) {
        animalListAdapter.remove(selectedAnimal);
        animalListAdapter.addAnimal(selectedAnimal);
        animalListAdapter.notifyItemInserted(animalListAdapter.getItemCount());
    }

    private void notNow(ArrayList<InfoMessage> messages) {
        Random random = new Random(Duration.between(Instant.EPOCH,Instant.now()).toMillis());
        int number = random.nextInt(100);
        if(number == 69) {
            InfoMessage notNow = new InfoMessage(
                    username + " " + getResources().getString(R.string.not_now),
                    R.drawable.waterstone,
                    "notNow");
            messages.add(notNow);
        }
        if(number == 81) {
            InfoMessage ascanio = new InfoMessage(
                    username + " " + getResources().getString(R.string.ascanio),
                    R.drawable.ascanio,
                    "ascanio");
            messages.add(ascanio);
        }
    }

    private void notNowDialog(String title, String message, String buttonMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(message);
        // Set dialog title
        @SuppressLint("InflateParams")
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(title);
        builder.setCustomTitle(titleView);
        builder.setNeutralButton(buttonMessage, (dialog, id) -> {});
        builder.show();
    }

    @Override
    public List<Veterinarian> getVeterinarianList() {
        throw new UnsupportedOperationException();
    }
}