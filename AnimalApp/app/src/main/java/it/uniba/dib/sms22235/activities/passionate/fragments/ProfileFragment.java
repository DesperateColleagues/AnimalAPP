package it.uniba.dib.sms22235.activities.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.adapters.AnimalListAdapter;
import it.uniba.dib.sms22235.entities.users.Animal;


public class ProfileFragment extends Fragment {

    public interface ProfileFragmentListener{
        void retrieveUserAnimals(RecyclerView recyclerView, View rootView);
    }

    private ProfileFragmentListener listener;
    private ArrayList<Animal> a;
    private View rootView;

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();


        try {
            // Attach the listener to the Fragment
            listener = (ProfileFragment.ProfileFragmentListener) context;
            a = new ArrayList<>();
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


        RecyclerView animalRecycleView = rootView.findViewById(R.id.animalList);
        listener.retrieveUserAnimals(animalRecycleView, rootView);

        //if(!a.isEmpty()){

        //} else {
            //animalRecycleView.setVisibility(View.INVISIBLE);
        //}

        return rootView;
    }

    public void setA(ArrayList<Animal> a) {
        this.a = a;
    }
}