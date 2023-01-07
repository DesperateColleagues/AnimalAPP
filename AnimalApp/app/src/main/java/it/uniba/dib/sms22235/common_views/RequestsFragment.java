package it.uniba.dib.sms22235.common_views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.ActivityInterface;
import it.uniba.dib.sms22235.entities.operations.Request;

public class RequestsFragment extends Fragment {

    private ArrayList<Request> requestsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ActivityInterface) requireActivity()).getFab().setOnClickListener(v -> {
            Toast.makeText(getContext(),"Still nothing, but with requests",Toast.LENGTH_SHORT).show();
        });

        ChipGroup requestsParamsChipGroup = view.findViewById(R.id.requestsParamsChipGroup);
        RecyclerView requestsRecyclerView = view.findViewById(R.id.requestsRecyclerList);
    }
}
