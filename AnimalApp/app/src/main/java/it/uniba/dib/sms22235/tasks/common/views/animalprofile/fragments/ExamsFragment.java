package it.uniba.dib.sms22235.tasks.common.views.animalprofile.fragments;

import android.content.Context;
import android.content.Intent;
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
import it.uniba.dib.sms22235.adapters.ExamsAdapter;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;

public class ExamsFragment extends Fragment {

    private String animal;
    private RecyclerView examsRecyclerView;
    private ExamsAdapter adapter;

    public ExamsFragment(String animal) {
        this.animal = animal;
    }

    public interface ExamsFragmentListener {
        void getAnimalExams(ExamsAdapter adapter, RecyclerView recyclerView, String animal);
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
            });
        } else if ((getActivity()) instanceof PassionateNavigationActivity){
            btnAddAnimalOperation.setVisibility(View.GONE);
        } else {
            Toast.makeText(getContext(), "Non dovresti essere qui.", Toast.LENGTH_SHORT).show();
            btnAddAnimalOperation.setVisibility(View.GONE);
        }

        examsRecyclerView = view.findViewById(R.id.recyclerVerticalList);
        adapter = new ExamsAdapter();
        adapter.setContext(getContext());
        listener.getAnimalExams(adapter, examsRecyclerView, animal);

        examsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

    }

}