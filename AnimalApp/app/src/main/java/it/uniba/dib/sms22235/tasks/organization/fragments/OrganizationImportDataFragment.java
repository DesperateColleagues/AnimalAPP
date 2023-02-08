package it.uniba.dib.sms22235.tasks.organization.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.entities.users.ImportedAnimal;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class OrganizationImportDataFragment extends Fragment {

    private ArrayList<ImportedAnimal> importedAnimalsList;
    private JSONArray importedAnimalsJSONArray;
    private RecyclerView organizationImportRecyclerView;
    private TextView nothingHereTextView;
    private AnimalListAdapter adapter;
    private FloatingActionButton fab;
    private int isFetched = 0;

    private OrganizationImportDataFragment.OrganizationImportDataFragmentListener listener;

    public interface OrganizationImportDataFragmentListener {
        /**
         * This callback is used to register an animal to Firestore and to update or
         * initialize the recycler view
         *
         * @param animal the animal to register
         * */
        void onAnimalRegistered(Animal animal);
        void uploadPhotos(ImportedAnimal animal);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OrganizationNavigationActivity activity = (OrganizationNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (OrganizationImportDataFragment.OrganizationImportDataFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_organization_import_data, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        importedAnimalsList = new ArrayList<>();
        importedAnimalsJSONArray = null;

        organizationImportRecyclerView = rootView.findViewById(R.id.organizationImportAnimalList);
        nothingHereTextView = rootView.findViewById(R.id.nothingHereTextView);
        adapter = new AnimalListAdapter(RecyclerView.VERTICAL);

        fab = ((OrganizationNavigationActivity) requireActivity()).getFab();

        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_baseline_import_export_24);
        fab.setOnClickListener(v -> {
            if (isFetched == 0) {
                fetchAnimals();
                fab.setImageResource(R.drawable.ic_baseline_check_24);
            } else if (isFetched == 1) {
                importAnimals();
                fab.setVisibility(View.GONE);
                nothingHereTextView.setVisibility(View.VISIBLE);
                organizationImportRecyclerView.setVisibility(View.GONE);
            } else {
                Toast.makeText(requireContext(), getResources().getString(R.string.importazione_fallita), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void fetchAnimals() {
        String url = "https://firebasestorage.googleapis.com/v0/b/animalapp-69a61.appspot.com/o/json%2Fanimals.json?alt=media&token=9ecf0c1b-e551-451f-b1fe-a9db550cbcf7";

        parseJsonFromUrl(url);
        loadAnimals();

        adapter.setContext(getContext());

        if (importedAnimalsList.size() > 0) {
            organizationImportRecyclerView.setVisibility(View.VISIBLE);
            nothingHereTextView.setVisibility(View.GONE);
            for (Animal animal : importedAnimalsList) {
                adapter.addAnimal(animal);
            }
        } else {
            organizationImportRecyclerView.setVisibility(View.GONE);
            nothingHereTextView.setVisibility(View.VISIBLE);
        }

        organizationImportRecyclerView.setAdapter(adapter);

        organizationImportRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }

    private void importAnimals() {
        for (ImportedAnimal animal : importedAnimalsList) {
            listener.onAnimalRegistered(animal.getBaseAnimal());
            listener.uploadPhotos(animal);
        }

        Toast.makeText(getContext(), getResources().getString(R.string.importazione_avvenuta), Toast.LENGTH_LONG).show();
    }

    private void parseJsonFromUrl(String uri) {
        try {
            Scanner sn = new Scanner(new URL(uri).openStream(), "UTF-8");
            importedAnimalsJSONArray = new JSONArray(sn.useDelimiter("\\A").next());
        } catch (Exception e) {
            Toast.makeText(getContext(), getResources().getString(R.string.importazione_fallita), Toast.LENGTH_LONG).show();
            Log.wtf("Animal Import", e.getMessage());
        }
    }

    private void loadAnimals() {
            try {
                for (int i = 0; i < importedAnimalsJSONArray.length(); i++) {
                    JSONObject object = importedAnimalsJSONArray.getJSONObject(i);

                    ImportedAnimal animal = new ImportedAnimal(
                            object.getString(KeysNamesUtils.AnimalFields.NAME),
                            object.getString(KeysNamesUtils.AnimalFields.ANIMAL_SPECIES),
                            object.getString(KeysNamesUtils.AnimalFields.RACE),
                            object.getString(KeysNamesUtils.AnimalFields.MICROCHIP_CODE),
                            object.getString(KeysNamesUtils.AnimalFields.BIRTH_DATE),
                            object.getString("profilePhoto"),
                            object.getString("photos")
                    );
                    animal.setOwner(object.getString(KeysNamesUtils.AnimalFields.OWNER));
                    animal.setHeight(object.getDouble(KeysNamesUtils.AnimalFields.HEIGHT));
                    animal.setWeight(object.getDouble(KeysNamesUtils.AnimalFields.WEIGHT));
                    animal.setNature(object.getString(KeysNamesUtils.AnimalFields.NATURE));
                    importedAnimalsList.add(animal);
                }
                isFetched++;
            } catch (Exception e) {
                Toast.makeText(getContext(), getResources().getString(R.string.importazione_fallita), Toast.LENGTH_LONG).show();
                Log.wtf("Animal Loading", e.getMessage());
        }
    }
}
