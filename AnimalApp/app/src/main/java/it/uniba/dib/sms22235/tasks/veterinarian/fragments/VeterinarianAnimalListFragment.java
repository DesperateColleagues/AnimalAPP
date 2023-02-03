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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

public class VeterinarianAnimalListFragment extends Fragment {

    private RecyclerView assistedAnimalRecyclerView;
    private AnimalListAdapter adapter;
    private transient NavController controller;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {

        controller = Navigation.findNavController(container);

        View rootView = inflater.inflate(R.layout.fragment_animal_list, container, false);

        assistedAnimalRecyclerView = rootView.findViewById(R.id.assistedAnimalList);

        adapter = new AnimalListAdapter(RecyclerView.VERTICAL);

        adapter.setContext(getContext());

        listener.getAssistedAnimals(adapter, assistedAnimalRecyclerView);

        assistedAnimalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        assistedAnimalRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), assistedAnimalRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, adapter.getAnimalAtPosition(position));
                bundle.putInt("ViewMode", 1);
                controller.navigate(R.id.action_veterinarian_animal_list_to_animalProfile, bundle);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.VISIBLE);
        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);
        ((NavigationActivityInterface) requireActivity()).getFab().setOnClickListener(v -> {
            Toast.makeText(getContext(),"Still nothing, but with animals",Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }
}