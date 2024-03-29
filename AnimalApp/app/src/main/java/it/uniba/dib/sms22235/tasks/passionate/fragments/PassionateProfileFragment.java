package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
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

import com.google.android.material.snackbar.Snackbar;
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

/**
 * This fragment shows the passionate profile. From here passionate can access a various number
 * of functionalities such as: requests, reports, list of organizations and veterinarians, list
 * of booked reservation and poke links. The fragment shows the list of the animal owned by the
 * passionate.
 * */
public class PassionateProfileFragment extends Fragment implements
        DialogAddAnimalFragment.DialogAddAnimalFragmentListener,
        DialogEditAnimalDataFragment.DialogEditAnimalDataFragmentListener,
        AnimalProfile.AnimalProfileEditListener {

    private PassionateProfileFragment.ProfileFragmentListener listener;
    private String username;
    private transient NavController controller;
    private TextView animalListPassionateLabel;
    private RecyclerView animalRecycleView;
    private TextView nothingHereTextView;

    // Manage Qr scanning
    private final ActivityResultLauncher<ScanOptions> qrDecodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Snackbar snackbar = Snackbar.make(getView(),getResources().getString(R.string.camera_permission),Snackbar.LENGTH_LONG);
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
                            bundle.putSerializable("UserObject",((NavigationActivityInterface) requireActivity()).getUser());
                            bundle.putInt("ViewMode", KeysNamesUtils.AnimalInformationViewModeFields.VIEW_ONLY);
                            controller.navigate(R.id.action_passionate_profile_to_animalProfile, bundle);
                        }
                    });
        }
    });


    /**
     * Operation of the fragment
     * */
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
        animalRecycleView = rootView.findViewById(R.id.animalList);
        nothingHereTextView = rootView.findViewById(R.id.nothingHereTextView);
        animalListPassionateLabel = rootView.findViewById(R.id.animalListPassionateLabel);

        username = ((PassionateNavigationActivity) requireActivity())
                .getUserId();

        String title = getString(R.string.benvenuto) + ", " + username;
        ((TextView) rootView.findViewById(R.id.txtPassionateWelcome)).setText(title);

        LinkedHashSet<Animal> animalSet =
                ((PassionateNavigationActivity) requireActivity()).getAnimalSet();

        if (animalSet != null) {

            if (animalSet.size() == 0)
            {
                animalListPassionateLabel.setVisibility(View.GONE);
                nothingHereTextView.setVisibility(View.VISIBLE);
                animalRecycleView.setVisibility(View.GONE);
            }
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

                        if (((PassionateNavigationActivity) requireActivity()).isConnectionEnabled()) {

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
                                options.setPrompt(getString(R.string.scannerizza_qr_code));
                                options.setBeepEnabled(false);
                                options.setBarcodeImageEnabled(true);
                                options.setOrientationLocked(false);

                                qrDecodeLauncher.launch(options);
                            } else if (selection.equals("notNow")) {
                                notNowDialog(
                                        username + " " + getResources().getString(R.string.not_now),
                                        getResources().getString(R.string.not_now_message),
                                        getResources().getString(R.string.not_now_button)
                                );
                            } else if (selection.equals("ascanio")) {
                                notNowDialog(
                                        username + " " + getResources().getString(R.string.ascanio),
                                        getResources().getString(R.string.ascanio_message),
                                        getResources().getString(R.string.ascanio_button)
                                );
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.error_offline), Toast.LENGTH_SHORT).show();
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
                    bundle.putInt(KeysNamesUtils.BundleKeys.VIEW_MODE, KeysNamesUtils.AnimalInformationViewModeFields.PHOTO_ONLY);
                    controller.navigate(R.id.action_passionate_profile_to_animalProfile, bundle);
                }

                @Override
                public void onLongClick(View view, int position) {
                    // small help for the user.
                    Snackbar snackbar = Snackbar.make(getView(),getResources().getString(R.string.animal_profile_help),Snackbar.LENGTH_LONG);
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
        InfoMessage qrScan = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_QR_Scan), R.drawable.ic_baseline_qr_code_scanner_24, KeysNamesUtils.RolesNames.ANIMAL);
        InfoMessage showVeterinarians = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_vet_list_text), R.drawable.fra_rrc_doctor_no_green, KeysNamesUtils.RolesNames.VETERINARIAN);
        InfoMessage showOrganizations = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_org_list_text), R.drawable.fra_rrc_organization_no_green, KeysNamesUtils.RolesNames.PRIVATE_ORGANIZATION);
        InfoMessage recentReservations = new InfoMessage(getResources().getString(R.string.tutti_appuntamenti_recenti), R.drawable.recent_reservations, KeysNamesUtils.CollectionsNames.RESERVATIONS);
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
        animalListPassionateLabel.setVisibility(View.VISIBLE);
        nothingHereTextView.setVisibility(View.GONE);
        animalRecycleView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal) {
        animalListAdapter.remove(selectedAnimal);
        animalListAdapter.addAnimal(selectedAnimal);
        animalListAdapter.notifyItemInserted(animalListAdapter.getItemCount());
    }

    private void notNow(ArrayList<InfoMessage> messages) {
        Random random = new Random(Duration.between(Instant.EPOCH,Instant.now()).toMillis());
        int number = random.nextInt(50);
        if(number >= 34 && number <= 36) {
            InfoMessage notNow = new InfoMessage(
                    username + " " + getResources().getString(R.string.not_now),
                    R.drawable.waterstone,
                    "notNow");
            messages.add(notNow);
        }
        if(number >= 40 && number <= 42) {
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