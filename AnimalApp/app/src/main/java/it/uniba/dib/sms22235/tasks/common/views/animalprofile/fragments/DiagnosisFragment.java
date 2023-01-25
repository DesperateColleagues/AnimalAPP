package it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.DiagnosisAdapter;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;

public class DiagnosisFragment extends Fragment {

    private final String animal;
    private RecyclerView diagnosisRecyclerView;
    private DiagnosisAdapter adapter;
    private DiagnosisFragmentListener listener;

    public DiagnosisFragment(String animal) {
        this.animal = animal;
    }

    public interface DiagnosisFragmentListener {
        void getAnimalDiagnosis(DiagnosisAdapter adapter, RecyclerView recyclerView, String animal);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (DiagnosisFragment.DiagnosisFragmentListener) context;
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
        return inflater.inflate(R.layout.fragment_simple_vertical_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnAddAnimalOperation = view.findViewById(R.id.btnAddAnimalOperation);
        if ((getActivity()) instanceof VeterinarianNavigationActivity) {
            btnAddAnimalOperation.setVisibility(View.VISIBLE);
            btnAddAnimalOperation.setText(getResources().getString(R.string.aggiungi_diagnosi));
            btnAddAnimalOperation.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Inserimento nuova diagnosi", Toast.LENGTH_SHORT).show();
            });
        } else if ((getActivity()) instanceof PassionateNavigationActivity){
            btnAddAnimalOperation.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "Non dovresti essere qui.", Toast.LENGTH_SHORT).show();
            btnAddAnimalOperation.setVisibility(View.GONE);
        }

        diagnosisRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter = new DiagnosisAdapter();
        listener.getAnimalDiagnosis(adapter, diagnosisRecyclerView, animal);
        diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

    }
}
