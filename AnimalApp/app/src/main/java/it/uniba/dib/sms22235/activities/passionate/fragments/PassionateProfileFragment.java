package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddAnimalFragment;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAnimalCardFragment;
import it.uniba.dib.sms22235.adapters.AnimalListAdapter;
import it.uniba.dib.sms22235.adapters.MessageListAdapter;
import it.uniba.dib.sms22235.entities.operations.InfoMessage;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

public class PassionateProfileFragment extends Fragment implements DialogAddAnimalFragment.DialogAddAnimalFragmentListener {

    public interface ProfileFragmentListener {
        /**
         * This callback is used to register an animal to Firestore and to update or
         * initialize the recycler view
         *
         * @param animal the animal to register
         * */
        void onAnimalRegistered(Animal animal);
    }

    private ProfileFragmentListener listener;
    private MessageListAdapter messageListAdapter;
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

        View rootView = inflater.inflate(R.layout.fragment_passionate_profile, container, false);

        String title = "Benvenuto, " + ((PassionateNavigationActivity) requireActivity())
                .getPassionateUsername();
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
            animalListAdapter = new AnimalListAdapter();

            for (Animal animal : animalSet) {
                // Set the animals in the adapter
                animalListAdapter.addAnimal(animal);

                // Load the profile pic preview
                Bitmap image = DataManipulationHelper.loadBitmapFromStorage(path,
                        animal.getMicrochipCode() + ".png");

                // Add the profile pic preview to the adapter
                animalListAdapter.addPic(image);
            }

            RecyclerView messageRecyclerView = rootView.findViewById(R.id.messagesList);
            RecyclerView animalRecycleView = rootView.findViewById(R.id.animalList);

            ArrayList<InfoMessage> messages = new ArrayList<>();
            messageListAdapter = new MessageListAdapter(buildStandardMessages(messages));
            messageRecyclerView.setAdapter(messageListAdapter);
            messageRecyclerView.setLayoutManager(new LinearLayoutManager(
                    getContext(), RecyclerView.HORIZONTAL, false));


            animalRecycleView.setAdapter(animalListAdapter);
            animalRecycleView.setLayoutManager(new LinearLayoutManager(
                    getContext(), RecyclerView.HORIZONTAL, false));

            animalRecycleView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), animalRecycleView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    // This method is used to request storage permission to the user
                    // with that we can save animal images not only on firebase,
                    // but also locally, to retrieve them more easily
                    ((PassionateNavigationActivity) requireActivity()).requestPermission();

                    // This code obtains the selected Animal info and it shows them in a specific built Dialog
                    DialogAnimalCardFragment dialogAnimalCardFragment = new DialogAnimalCardFragment(
                            animalListAdapter.getAnimalAtPosition(position));
                    dialogAnimalCardFragment.show(requireActivity().getSupportFragmentManager(),
                            "DialogAnimalCardFragment");
                }

                @Override
                public void onLongClick(View view, int position) {
                    // We do not need this method atm
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

    private ArrayList<InfoMessage> buildStandardMessages(ArrayList<InfoMessage> messages) {
        InfoMessage findings = new InfoMessage(R.string.passionate_profile_cardlayout_text, R.drawable.warningsign);
        InfoMessage recentReservations = new InfoMessage(R.string.tutti_appuntamenti_recenti, 0);

        messages.add(findings);
        messages.add(recentReservations);

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

}