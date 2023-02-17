package it.uniba.dib.sms22235.tasks.veterinarian.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.tasks.organization.fragments.OrganizationProfileFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

/**
 * Class to show the veterinarian profile section
 * */
public class VeterinarianProfileFragment extends Fragment {

    public interface VeterinarianProfileFragmentListener {
        /**
         * This method is called to load all the assisted animal. The animal must be
         * displayed using the adapter and the recycler provided
         *
         * @param adapter the animal list adapter
         * @param recyclerView the recycler where to show data represented by the adapter
         * */
        void getAssistedAnimals(AnimalListAdapter adapter, RecyclerView recyclerView);
    }

    private VeterinarianProfileFragmentListener listener;
    private transient NavController controller;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        VeterinarianNavigationActivity activity = (VeterinarianNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (VeterinarianProfileFragment.VeterinarianProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_veterinarian_profile, container, false);
        controller = Navigation.findNavController(container);

        String title = getString(R.string.benvenuto) + ", " + ((VeterinarianNavigationActivity) requireActivity())
                .getVeterinarianFullName();

        ((TextView) rootView.findViewById(R.id.txtVeterinarianWelcome)).setText(title);

        ((VeterinarianNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);

        rootView.findViewById(R.id.cardViewVeterinarian).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(container) ;
            navController.navigate(R.id.action_passionate_profile_to_reportsDashboardFragment);
        });

        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.VISIBLE);

        RecyclerView assistedAnimalRecyclerView = rootView.findViewById(R.id.assistedAnimalList);

        AnimalListAdapter adapter = new AnimalListAdapter(RecyclerView.VERTICAL);
        adapter.setContext(getContext());

        listener.getAssistedAnimals(adapter, assistedAnimalRecyclerView);

        assistedAnimalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        assistedAnimalRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), assistedAnimalRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, adapter.getAnimalAtPosition(position));
                bundle.putSerializable("UserObject",((NavigationActivityInterface) requireActivity()).getUser());
                bundle.putInt(KeysNamesUtils.BundleKeys.VIEW_MODE, KeysNamesUtils.AnimalInformationViewModeFields.VET);
                controller.navigate(R.id.action_veterinarian_profile_to_animalProfile, bundle);
        }

        @Override
        public void onLongClick(View view, int position) {
                // not needed
        }
    }));

        return rootView;
    }
}