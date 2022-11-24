package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;


public class ProfileFragment extends Fragment {

    public interface ProfileFragmentListener{
        /**
         * This callback is used to retrieve the passionate's animals from the DB and to
         * set the recycler view.
         *
         * @param recyclerView the recycler view where the Adapter has to be attached
         * @param rootView the root view of the fragment which calls this method
         * */
        void retrieveUserAnimals(RecyclerView recyclerView, View rootView);
    }

    private ProfileFragmentListener listener;
    private RecyclerView animalRecycleView;
    private View rootView;

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

        rootView = inflater.inflate(R.layout.fragment_passionate_profile, container, false);
        animalRecycleView = rootView.findViewById(R.id.animalList);
        listener.retrieveUserAnimals(animalRecycleView, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}