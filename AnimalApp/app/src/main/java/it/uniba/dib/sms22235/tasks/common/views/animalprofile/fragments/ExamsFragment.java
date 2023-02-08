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
import it.uniba.dib.sms22235.adapters.animals.AnimalExamsAdapter;
import it.uniba.dib.sms22235.entities.operations.Exam;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.BSDialogEditExamFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddExamFragment;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;

public class ExamsFragment extends Fragment implements
DialogAddExamFragment.DialogAddExamFragmentListener {

    private final String animal;
    private final String owner;

    private RecyclerView examsRecyclerView;
    private AnimalExamsAdapter adapter;

    private FirebaseAuth mAuth;

    private InterfacesOperationsHelper helper;

    public ExamsFragment(String animal, String owner) {
        this.animal = animal;
        this.owner = owner;
    }

    public interface ExamsFragmentListener {
        void getAnimalExams(AnimalExamsAdapter adapter, RecyclerView recyclerView, String animal, AnimalExamsAdapter.OnItemClickListener onClickListener);
    }


    private ExamsFragment.ExamsFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        NavigationActivityInterface activity = (NavigationActivityInterface) getActivity();
        helper = new InterfacesOperationsHelper(getContext());
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

        examsRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter = new AnimalExamsAdapter();
        adapter.setContext(getContext());

        Button btnAddAnimalOperation = view.findViewById(R.id.btnAddAnimalOperation);

        if ((getActivity()) instanceof VeterinarianNavigationActivity) {
            btnAddAnimalOperation.setVisibility(View.VISIBLE);
            btnAddAnimalOperation.setText(getResources().getString(R.string.aggiungi_esame));
            btnAddAnimalOperation.setOnClickListener(v -> {

                DialogAddExamFragment dialogAddExamFragment = new DialogAddExamFragment();
                dialogAddExamFragment.setListener(this);
                dialogAddExamFragment.show(getParentFragmentManager(), "DialogAddExamFragment");
            });
            listener.getAnimalExams(adapter, examsRecyclerView, animal, onClickEditListener);
        } else {
            btnAddAnimalOperation.setVisibility(View.GONE);
            listener.getAnimalExams(adapter, examsRecyclerView, animal, onClickViewListener);
        }

        examsRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter.setContext(getContext());

        examsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogAddExamDismissed(Exam exam) {
        SimpleDateFormat dateSDF = new SimpleDateFormat("dd/MM/yy", Locale.ITALY);
        String dateAdded = dateSDF.format(new Date());
        SimpleDateFormat timeSDF = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String timeAdded = timeSDF.format(new Date());

        exam.setAnimal(animal);
        exam.setDateAdded(dateAdded);
        exam.setTimeAdded(timeAdded);

        adapter.remove(exam);
        adapter.addExam(exam);
        adapter.notifyDataSetChanged();

        InterfacesOperationsHelper.AnimalHealthOperations helper = new InterfacesOperationsHelper.AnimalHealthOperations(
                getContext(),
                ((NavigationActivityInterface) requireActivity()).getFireStoreInstance());
        helper.registerExam(exam);
    }

    private final AnimalExamsAdapter.OnItemClickListener onClickEditListener = exam -> {
        new BSDialogEditExamFragment()
                .setOnUpgradeListener(() -> {
                    DialogAddExamFragment dialogAddExamFragment = new DialogAddExamFragment(exam);
                    dialogAddExamFragment.setListener(this);
                    dialogAddExamFragment.show(getParentFragmentManager(), "DialogAddExamFragment");
                })
                .setOnDeleteListener(() -> {
                    Toast.makeText(getContext(), getResources().getString(R.string.elimina_esame), Toast.LENGTH_SHORT).show();
                })
                .setOnShowListener(() -> {
                    Toast.makeText(getContext(), getResources().getString(R.string.mostra_esame), Toast.LENGTH_SHORT).show();
                })
                .setOnShowListener(() -> {
                    showInfo(exam);
                })
                .show(getParentFragmentManager(), getResources().getString(R.string.modifica_esame));
    };

    private final AnimalExamsAdapter.OnItemClickListener onClickViewListener = this::showInfo;

    private void showInfo(Exam exam) {
        String info = "• <b>" +
                getResources().getString(R.string.animale) +
                ": </b>"+
                exam.getAnimal() +
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.esito_esame) +
                ": </b>"+
                exam.getOutcome() +
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.tipo_esame) +
                ": </b>" +
                exam.getType()+
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.descrizione) +
                ": </b>" +
                exam.getDescription();
        DialogEntityDetailsFragment entityDetailsFragment = new DialogEntityDetailsFragment(info);
        entityDetailsFragment.show(getParentFragmentManager(), "DialogEntityDetailsFragment");
    }

}