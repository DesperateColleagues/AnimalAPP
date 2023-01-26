package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.PokAnimalAdapter;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAddPokeLink;

public class PassionatePokAnimalList extends Fragment implements DialogAddPokeLink.DialogAddPokeLinkListener {

    public interface PassionatePokAnimalListListener {
        void loadOtherAnimal(Spinner spinner);
        void savePokeLink(String myCode, String otherCode, String type, String description, PokAnimalAdapter adapter);
        void loadPokeLinks(PokAnimalAdapter adapter);
    }

    private PassionatePokAnimalListListener listener;
    private PokAnimalAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        PassionateNavigationActivity activity = (PassionateNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (PassionatePokAnimalListListener) context;
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

        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);
        ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_passionate_pokanimal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new PokAnimalAdapter();
        adapter.setContext(requireContext());

        listener.loadPokeLinks(adapter);

        view.findViewById(R.id.btnAddPokeLink).setOnClickListener(v -> {
            DialogAddPokeLink dialogAddPokeLink = new DialogAddPokeLink();
            dialogAddPokeLink.setListener(this);
            dialogAddPokeLink.show(getChildFragmentManager(), "DialogAddPokeLink");
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPokemon);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void loadOtherAnimals(Spinner spinner) {
        listener.loadOtherAnimal(spinner);
    }

    @Override
    public void onLinkAdded(String myCode, String otherCode, String type, String description) {
        listener.savePokeLink(myCode, otherCode, type, description, adapter);
    }
}
