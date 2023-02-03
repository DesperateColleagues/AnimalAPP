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

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddDiagnosisFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddExamFragment;

public class ExamsFragment extends Fragment implements
DialogAddExamFragment.DialogAddExamFragmentListener {

    private final String animal;
    private final String owner;

    private RecyclerView examsRecyclerView;
    private AnimalExamsAdapter adapter;

    private FirebaseAuth mAuth;

    public ExamsFragment(String animal, String owner) {
        this.animal = animal;
        this.owner = owner;
    }

    public interface ExamsFragmentListener {
        void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal);
    }


    private ExamsFragment.ExamsFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (ExamsFragment.ExamsFragmentListener) context;
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

        Button btnAddAnimalOperation = view.findViewById(R.id.btnAddAnimalOperation);

        if ((getActivity()) instanceof VeterinarianNavigationActivity) {
            btnAddAnimalOperation.setVisibility(View.VISIBLE);
            btnAddAnimalOperation.setText(getResources().getString(R.string.aggiungi_esame));
            btnAddAnimalOperation.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Inserimento nuovo esame", Toast.LENGTH_SHORT).show();

                DialogAddExamFragment dialogAddExamFragment = new DialogAddExamFragment();
                dialogAddExamFragment.setListener(this);
                dialogAddExamFragment.show(getParentFragmentManager(), "DialogAddExamFragment");
            });
        } else {
            btnAddAnimalOperation.setVisibility(View.GONE);
        }

        examsRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter = new AnimalExamsAdapter();
        adapter.setContext(getContext());
        listener.getAnimalExams(adapter, examsRecyclerView, animal);

        examsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
    }


    @Override
    public void onDialogAddExamDismissed(Exam exam) {

    }

}