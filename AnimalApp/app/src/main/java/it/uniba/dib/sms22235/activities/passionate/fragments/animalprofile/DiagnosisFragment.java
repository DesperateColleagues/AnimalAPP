package it.uniba.dib.sms22235.activities.passionate.fragments.animalprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.users.Animal;

public class DiagnosisFragment extends Fragment {

    public interface DiagnosisFragmentListener {
        public List<Diagnosis> getAnimalDiagnosis(Animal animal);
    }

    private NavController controller;
    private ArrayList<Diagnosis> animalDiagnosis;
    private DiagnosisFragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //controller = Navigation.findNavController(container);

        //animalDiagnosis = listener.getAnimalDiagnosis();
        return inflater.inflate(R.layout.fragment_passionate_diagnosis_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // RecyclerView passionateDiagnosisRecyclerView = view.findViewById(R.id.passionateDiagnosisList);

        // adapter

        ArrayAdapter<Diagnosis> adapter = new ArrayAdapter<Diagnosis>(getContext(), R.layout.item_fragment_reservation_single_card, animalDiagnosis);

        /*passionateDiagnosisRecyclerView.setAdapter(adapter);*/

        /*passionateDiagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));*/

    }
}
