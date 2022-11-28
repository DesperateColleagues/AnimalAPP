package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.activities.passionate.dialogs.DialogAddAnimalFragment;
import it.uniba.dib.sms22235.entities.users.Animal;

public class ProfileFragment extends Fragment implements DialogAddAnimalFragment.DialogAddAnimalFragmentListener {

    public interface ProfileFragmentListener {
        /**
         * This callback is used to retrieve the passionate's animals from the DB and to
         * set the recycler view.
         *
         * @param recyclerView the recycler view where the Adapter has to be attached
         * */
        void retrieveUserAnimals(RecyclerView recyclerView);

        /**
         * This callback is used to register an animal to Firestore and to update or
         * initialize the recycler view
         *
         * @param animal the animal to register
         * @param recyclerView the view to initialize or update following the db save
         * */
        void onAnimalRegistered(Animal animal, RecyclerView recyclerView);
    }

    private ProfileFragmentListener listener;
    private RecyclerView animalRecycleView;
    private DialogAddAnimalFragment dialogAddAnimalFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (ProfileFragment.ProfileFragmentListener) context;
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
        animalRecycleView = rootView.findViewById(R.id.animalList);
        listener.retrieveUserAnimals(animalRecycleView);

        String title = "Benvenuto " + ((PassionateNavigationActivity) requireActivity())
                .getPassionateUsername();

        ((TextView) rootView.findViewById(R.id.txtWelcome)).setText(title);

        ((PassionateNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            dialogAddAnimalFragment = new DialogAddAnimalFragment();
            dialogAddAnimalFragment.setListener(this);
            dialogAddAnimalFragment.show(getParentFragmentManager(), "DialogAddAnimalFragment");
        });

        return rootView;
    }

    @Override
    public void onDialogAddAnimalDismissed(Animal animal) {
        listener.onAnimalRegistered(animal, animalRecycleView);
    }

}