package it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalDiagnosisAdapter;
import it.uniba.dib.sms22235.entities.operations.Diagnosis;
import it.uniba.dib.sms22235.entities.users.AbstractPersonUser;
import it.uniba.dib.sms22235.entities.users.Animal;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.common.dialogs.DialogEntityDetailsFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.BSDialogEditDiagnosisFragment;
import it.uniba.dib.sms22235.tasks.veterinarian.dialogs.DialogAddDiagnosisFragment;
import it.uniba.dib.sms22235.utils.InterfacesOperationsHelper;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment is used to display diagnosis
 * */
public class DiagnosisFragment extends Fragment implements
        DialogAddDiagnosisFragment.DialogAddDiagnosisFragmentListener {

    private transient FirebaseFirestore db;
    private final Animal animal;
    private final AbstractPersonUser user;
    private AnimalDiagnosisAdapter adapter;
    private DiagnosisFragmentListener listener;
    private final int viewMode;

    public DiagnosisFragment(Animal animal, AbstractPersonUser user, int viewMode) {
        this.animal = animal;
        this.user = user;
        this.viewMode = viewMode;
    }

    /**
     * This interface describes the actions of the diagnosis on this fragment
     * */
    public interface DiagnosisFragmentListener {
        /**
         * This method is used to load the existing diagnosis of an animal
         *
         * @param adapter the adapter to manage retrieved data
         * @param recyclerView the view where data will be displayed
         * @param animal the animal microchip
         * @param onClickListener the listener of the selected element of the recycler
         * */
        void loadAnimalDiagnosis(AnimalDiagnosisAdapter adapter,
                                 RecyclerView recyclerView,
                                 String animal,
                                 AnimalDiagnosisAdapter.OnItemClickListener onClickListener);
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
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_simple_vertical_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView diagnosisRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter = new AnimalDiagnosisAdapter();

        Button btnAddAnimalOperation = view.findViewById(R.id.btnAddAnimalOperation);

        if ((getActivity()) instanceof VeterinarianNavigationActivity && viewMode != KeysNamesUtils.AnimalInformationViewModeFields.VIEW_ONLY) {
            btnAddAnimalOperation.setVisibility(View.VISIBLE);
            btnAddAnimalOperation.setText(getResources().getString(R.string.aggiungi_diagnosi));
            btnAddAnimalOperation.setOnClickListener(v -> {
                DialogAddDiagnosisFragment dialogAddDiagnosisFragment = new DialogAddDiagnosisFragment();
                dialogAddDiagnosisFragment.setListener(this);
                dialogAddDiagnosisFragment.show(getParentFragmentManager(), "DialogAddDiagnosisFragment");
            });
            listener.loadAnimalDiagnosis(adapter, diagnosisRecyclerView, animal.getMicrochipCode(), onClickEditListener);
        } else {
            btnAddAnimalOperation.setVisibility(View.GONE);
            listener.loadAnimalDiagnosis(adapter, diagnosisRecyclerView, animal.getMicrochipCode(), onClickViewListener);
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

        diagnosis.setAnimal(animal.getMicrochipCode());
        diagnosis.setDateAdded(dateAdded);
        diagnosis.setTimeAdded(timeAdded);

        adapter.remove(diagnosis);
        adapter.addDiagnosis(diagnosis);
        adapter.notifyDataSetChanged();

        InterfacesOperationsHelper.AnimalHealthOperations helper = new InterfacesOperationsHelper.AnimalHealthOperations(
                getContext(),
                ((NavigationActivityInterface) requireActivity()).getFireStoreInstance());

        helper.registerDiagnosis(diagnosis);
    }

    private final AnimalDiagnosisAdapter.OnItemClickListener onClickEditListener = diagnosis -> {
        BSDialogEditDiagnosisFragment diagnosisOptionsFragment = new BSDialogEditDiagnosisFragment();

        diagnosisOptionsFragment
                .setOnUpgradeListener(() -> {
                    DialogAddDiagnosisFragment dialogAddDiagnosisFragment = new DialogAddDiagnosisFragment(diagnosis);
                    dialogAddDiagnosisFragment.setListener(this);
                    dialogAddDiagnosisFragment.show(getParentFragmentManager(), "DialogAddDiagnosisFragment");
                })
                .setOnDeleteListener(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                    // Set dialog title
                    @SuppressLint("InflateParams")
                    View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
                    TextView titleText = titleView.findViewById(R.id.dialog_title);

                    titleText.setText(getString(R.string.delete) + " " + diagnosis.getId().split("-")[0]);
                    builder.setCustomTitle(titleView);
                    builder.setMessage(R.string.delete_confirm_diagnosis);

                    builder.setPositiveButton(R.string.elimina_def, ((dialog, which) ->{

                        db.collection(KeysNamesUtils.CollectionsNames.DIAGNOSIS)
                                .document(diagnosis.getId()).delete()
                                .addOnSuccessListener(unused -> {
                                    adapter.remove(diagnosis);
                                    adapter.notifyDataSetChanged();
                                    diagnosisOptionsFragment.dismiss();
                                }); }));

                    builder.setNegativeButton(R.string.annulla, ((dialog, which) -> dialog.dismiss()));

                    builder.create().show();
                })
                .setOnShowListener(() -> {
                    showInfo(diagnosis);
                })
                .show(getParentFragmentManager(), getResources().getString(R.string.modifica_diagnosi));
    };

    private final AnimalDiagnosisAdapter.OnItemClickListener onClickViewListener = this::showInfo;

    private void showInfo(Diagnosis diagnosis) {
        String info = "• <b>" +
                getResources().getString(R.string.animale) +
                ": </b>"+
                diagnosis.getAnimal() +
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.descrizione) +
                ": </b>"+
                diagnosis.getDescription() +
                "\n<br>" +
                "• <b>" +
                getResources().getString(R.string.diagnosis_date_added_system) +
                ": </b>" +
                diagnosis.getDateAdded();
        DialogEntityDetailsFragment entityDetailsFragment = new DialogEntityDetailsFragment(info);
        entityDetailsFragment.show(getParentFragmentManager(), "DialogEntityDetailsFragment");
    }


}
