package it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments;

import android.annotation.SuppressLint;
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

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.BSDialogEditDiagnosisFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddDiagnosisFragment;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;

public class DiagnosisFragment extends Fragment implements
        DialogAddDiagnosisFragment.DialogAddDiagnosisFragmentListener {

    private final String animal;
    private final String owner;

    private RecyclerView diagnosisRecyclerView;
    private AnimalDiagnosisAdapter adapter;

    private DiagnosisFragmentListener listener;

    private FirebaseAuth mAuth;
    private InterfacesOperationsHelper helper;

    public DiagnosisFragment(String animal, String owner) {
        this.animal = animal;
        this.owner = owner;
    }

    public interface DiagnosisFragmentListener {
        void getAnimalDiagnosis(AnimalDiagnosisAdapter adapter, RecyclerView recyclerView, String animal, AnimalDiagnosisAdapter.OnItemClickListener onClickListener);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();
        helper = new InterfacesOperationsHelper(getContext());
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
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_simple_vertical_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        diagnosisRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter = new AnimalDiagnosisAdapter();

        Button btnAddAnimalOperation = view.findViewById(R.id.btnAddAnimalOperation);

        if ((getActivity()) instanceof VeterinarianNavigationActivity) {
            btnAddAnimalOperation.setVisibility(View.VISIBLE);
            btnAddAnimalOperation.setText(getResources().getString(R.string.aggiungi_diagnosi));
            btnAddAnimalOperation.setOnClickListener(v -> {
                DialogAddDiagnosisFragment dialogAddDiagnosisFragment = new DialogAddDiagnosisFragment();
                dialogAddDiagnosisFragment.setListener(this);
                dialogAddDiagnosisFragment.show(getParentFragmentManager(), "DialogAddDiagnosisFragment");
            });
            listener.getAnimalDiagnosis(adapter, diagnosisRecyclerView, animal, onClickEditListener);
        } else {
            btnAddAnimalOperation.setVisibility(View.GONE);
            listener.getAnimalDiagnosis(adapter, diagnosisRecyclerView, animal, onClickViewListener);
        }

        diagnosisRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogAddDiagnosisDismissed(@NonNull Diagnosis diagnosis) {
        SimpleDateFormat dateSDF = new SimpleDateFormat("dd/MM/yy", Locale.ITALY);
        String dateAdded = dateSDF.format(new Date());
        SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String timeAdded = timeSDF.format(new Date());

        diagnosis.setAnimal(animal);
        diagnosis.setDateAdded(dateAdded);
        diagnosis.setTimeAdded(timeAdded);

        adapter.remove(diagnosis);
        adapter.addDiagnosis(diagnosis);
        adapter.notifyDataSetChanged();

        helper.registerDiagnosis(diagnosis);
    }

    private final AnimalDiagnosisAdapter.OnItemClickListener onClickEditListener = diagnosis -> {
        new BSDialogEditDiagnosisFragment()
                .setOnUpgradeListener(() -> {
                    DialogAddDiagnosisFragment dialogAddDiagnosisFragment = new DialogAddDiagnosisFragment(diagnosis);
                    dialogAddDiagnosisFragment.setListener(this);
                    dialogAddDiagnosisFragment.show(getParentFragmentManager(), "DialogAddDiagnosisFragment");
                })
                .setOnDeleteListener(() -> {
                    Toast.makeText(getContext(), "Eliminazione diagnosi", Toast.LENGTH_SHORT).show();
                })
                .setOnShowListener(() -> {
                    Toast.makeText(getContext(), "Visualizzazione diagnosi", Toast.LENGTH_SHORT).show();
                })
                .show(getParentFragmentManager(), "Modifica diagnosi");
    };

    private final AnimalDiagnosisAdapter.OnItemClickListener onClickViewListener = diagnosis -> {
            String info = "• <b>" +
                    "Animale" +
                    ": </b>"+
                    diagnosis.getAnimal() +
                    "\n<br>" +
                    "• <b>" +
                    "Descrizione" +
                    ": </b>"+
                    diagnosis.getDescription() +
                    "\n<br>" +
                    "• <b>" +
                    "Data aggiunta al sistema" +
                    ": </b>" +
                    diagnosis.getDateAdded();
            DialogEntityDetailsFragment entityDetailsFragment = new DialogEntityDetailsFragment(info);
            entityDetailsFragment.show(getParentFragmentManager(), "DialogEntityDetailsFragment");
    };


}
