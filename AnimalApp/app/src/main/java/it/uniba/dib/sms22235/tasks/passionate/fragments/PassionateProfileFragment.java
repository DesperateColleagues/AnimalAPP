package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import org.jetbrains.annotations.Contract;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.common.views.animalprofile.AnimalProfile;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAddAnimalFragment;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogEditAnimalDataFragment;
import it.uniba.dib.sms22235.adapters.AnimalListAdapter;
import it.uniba.dib.sms22235.adapters.MessageListAdapter;
import it.uniba.dib.sms22235.entities.operations.InfoMessage;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

public class PassionateProfileFragment extends Fragment implements
        DialogAddAnimalFragment.DialogAddAnimalFragmentListener,
        DialogEditAnimalDataFragment.DialogEditAnimalDataFragmentListener,
        AnimalProfile.UpdateVeterinarianNameOnChoose {

    private PassionateProfileFragment.ProfileFragmentListener listener;
    private DialogEditAnimalDataFragment dialogEditAnimalDataFragment;
    private String username;
    private transient NavController controller;


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
                    if (messageListAdapter.getMessageAtPosition(position).getType().equals(KeysNamesUtils.CollectionsNames.RESERVATIONS)) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(
                                KeysNamesUtils.BundleKeys.PASSIONATE_RESERVATIONS,
                                ((PassionateNavigationActivity) requireActivity())
                                        .getPassionateReservationsList());
                        controller.navigate(R.id.action_passionate_profile_to_BookedReservationsFragment, bundle);
                    } else if (messageListAdapter.getMessageAtPosition(position).getType().equals(KeysNamesUtils.CollectionsNames.REPORTS)) {
                        controller.navigate(R.id.action_passionate_profile_to_reportsDashboardFragment);
                    } else if (messageListAdapter.getMessageAtPosition(position).getType().equals("notNow")){
                        notNowDialog(
                                username + " " + getResources().getString(R.string.not_now),
                                getResources().getString(R.string.not_now_message),
                                getResources().getString(R.string.not_now_button)
                                );
                    } else if (messageListAdapter.getMessageAtPosition(position).getType().equals("ascanio")){
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

            dialogEditAnimalDataFragment = new DialogEditAnimalDataFragment();
            dialogEditAnimalDataFragment.setListener(this);

            animalRecycleView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), animalRecycleView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    /*// This method is used to request storage permission to the user
                    // with that we can save animal images not only on firebase,
                    // but also locally, to retrieve them more easily
                    ((PassionateNavigationActivity) requireActivity()).requestPermission();

                    // This code obtains the selected Animal info and it shows them in a specific built Dialog
                    DialogAnimalCardFragment dialogAnimalCardFragment = new DialogAnimalCardFragment(
                            animalListAdapter.getAnimalAtPosition(position));
                    dialogAnimalCardFragment.show(requireActivity().getSupportFragmentManager(),
                            "DialogAnimalCardFragment");*/

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, animalListAdapter.getAnimalAtPosition(position));
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
        InfoMessage findings = new InfoMessage(getResources().getString(R.string.passionate_profile_cardlayout_text), R.drawable.warningsign, KeysNamesUtils.CollectionsNames.REPORTS);
        InfoMessage recentReservations = new InfoMessage(getResources().getString(R.string.tutti_appuntamenti_recenti), 0, KeysNamesUtils.CollectionsNames.RESERVATIONS);

        messages.add(findings);
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
    public void onDialogChoosedVeterinarian(@NonNull Animal selectedAnimal, String selectedVeterinarian) {
        animalListAdapter.remove(selectedAnimal);
        selectedAnimal.setVeterinarian(selectedVeterinarian);
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
        View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
        TextView titleText = titleView.findViewById(R.id.dialog_title);
        titleText.setText(title);
        builder.setCustomTitle(titleView);
        builder.setNeutralButton(buttonMessage, (dialog, id) -> {});
        builder.show();
    }

}