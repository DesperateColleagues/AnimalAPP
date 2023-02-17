package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.PokAnimalAdapter;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAddPassionateAnimalToPokeLink;
import it.uniba.dib.sms22235.tasks.passionate.dialogs.DialogAddPokeLink;

/**
 * This fragment is used to display and add the poke links
 * */
public class PassionatePokAnimalList extends Fragment implements
        DialogAddPokeLink.DialogAddPokeLinkListener,
        DialogAddPassionateAnimalToPokeLink.DialogAddPassionateAnimalToPokeLinkListener {

    /**
     * The operations of this fragment
     * */
    public interface PassionatePokAnimalListListener {
        /**
         * This method is used to load into a spinner all the animals
         * that do not belongs to the current logged passionate
         *
         * @param spinner the spinner where to load the animals
         * @param username to username of the animal's owner
         * */
        void loadOtherAnimal(Spinner spinner, String username, DialogFragment dialogFragment);

        /**
         * Callback called when a new link is added
         *
         * @param myCode the microchip of the passionate animal
         * @param otherCode the microchip of the other animal
         * @param type the type chosen
         * @param description the description of the poke link
         * */
        void savePokeLink(String myCode, String otherCode, String type, String description, PokAnimalAdapter adapter);

        void deletePokeLink(String id, PokAnimalAdapter adapter);

        /**
         * This method is used to load all saved poke links of a passionate
         *
         * @param adapter adapter used to update the view
         * */
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

        //set blue border
        TextView textViewShadow = view.findViewById(R.id.textViewShadowId);
        textViewShadow.getPaint().setStrokeWidth(20);
        textViewShadow.getPaint().setStyle(Paint.Style.STROKE);

        adapter = new PokAnimalAdapter();
        adapter.setContext(requireContext());

        listener.loadPokeLinks(adapter);

        view.findViewById(R.id.btnAddPokeLink).setOnClickListener(v -> {
            DialogAddPassionateAnimalToPokeLink dialogAddPassionateAnimalToPokeLink = new DialogAddPassionateAnimalToPokeLink();
            dialogAddPassionateAnimalToPokeLink.setListener(this);
            dialogAddPassionateAnimalToPokeLink.show(getChildFragmentManager(), "DialogAddPassionateAnimalToPokeLink");
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPokemon);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        recyclerView.setAdapter(adapter);

        adapter.setListener((pokeLink) -> {
            String info = getString(R.string.conferma_elimina_pokelink);
            DialogEntityDetailsFragment dialogEntityDetailsFragment = new DialogEntityDetailsFragment(info);

            // Set dialog title
            @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
            TextView titleText = titleView.findViewById(R.id.dialog_title);
            titleText.setText(getString(R.string.eliminazione_pokelink));
            dialogEntityDetailsFragment.setTitleView(titleView);
            dialogEntityDetailsFragment.show(getChildFragmentManager(), "DialogEntityDetailsFragment");

            dialogEntityDetailsFragment.setPositiveButton(getString(R.string.cancella), (dialog, which) -> {
                listener.deletePokeLink(pokeLink.getId(), adapter);
                dialog.dismiss();
            });

            dialogEntityDetailsFragment.setNegativeButton(getString(R.string.no), ((dialog, which) -> dialog.dismiss()));

        });
    }

    @Override
    public void loadOtherAnimals(Spinner spinner, String username, DialogFragment dialog) {
        listener.loadOtherAnimal(spinner, username, dialog);
    }

    @Override
    public void onLinkAdded(String myCode, String otherCode, String type, String description) {
        listener.savePokeLink(myCode, otherCode, type, description, adapter);
    }

    @Override
    public void onFriendAdded(String username) {
        DialogAddPokeLink dialogAddPokeLink = new DialogAddPokeLink();
        dialogAddPokeLink.setListener(this);
        dialogAddPokeLink.setFriendId(username);
        dialogAddPokeLink.show(getChildFragmentManager(), "DialogAddPokeLink");
    }
}
