package it.uniba.dib.sms22235.tasks.veterinarian.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.AnimalListAdapter;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;

public class VeterinarianAnimalListFragment extends Fragment {

    private RecyclerView assistedAnimalRecyclerView;
    private AnimalListAdapter adapter;

    public interface VeterinarianAnimalListFragmentListener {
        void getAssistedAnimals(AnimalListAdapter adapter, RecyclerView recyclerView);
    }

    private VeterinarianAnimalListFragment.VeterinarianAnimalListFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        VeterinarianNavigationActivity activity = (VeterinarianNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (VeterinarianAnimalListFragment.VeterinarianAnimalListFragmentListener) context;
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
        View rootView = inflater.inflate(R.layout.fragment_veterinarian_animal_list, container, false);

        assistedAnimalRecyclerView = rootView.findViewById(R.id.veterinarianAssistedAnimalList);

        adapter = new AnimalListAdapter(RecyclerView.VERTICAL);

        adapter.setContext(getContext());

        listener.getAssistedAnimals(adapter, assistedAnimalRecyclerView);

        assistedAnimalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        ((VeterinarianNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);
        ((VeterinarianNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            Toast.makeText(getContext(),"Still nothing, but with animals",Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }
}