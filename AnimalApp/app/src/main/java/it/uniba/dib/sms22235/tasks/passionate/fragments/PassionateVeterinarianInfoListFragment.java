package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.info.EntityInfoAdapter;
import it.uniba.dib.sms22235.entities.users.Veterinarian;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment is used to show a list of veterinarians. that can be filtered by a search of
 * specific info using its search bar. Every single element of the list provides two operations:
 * 1) call a phone number
 * 2) text email
 * */
public class PassionateVeterinarianInfoListFragment extends Fragment {

    private ArrayList<Veterinarian> veterinarianList;
    private ArrayList<Veterinarian> filteredVeterinarianList;

    @Nullable
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);
        ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);

        Bundle arguments = getArguments();

        if (arguments != null) {
            veterinarianList = (ArrayList<Veterinarian>) arguments.getSerializable(KeysNamesUtils.BundleKeys.VETERINARIANS_LIST);
        }

        return inflater.inflate(R.layout.fragment_passionate_veterinarian_info_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EntityInfoAdapter adapter = new EntityInfoAdapter(EntityInfoAdapter.AdapterMode.VET);
        adapter.setContext(requireContext());
        adapter.setVeterinarianList(veterinarianList);

        RecyclerView recyclerView = view.findViewById(R.id.veterinarianListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchViewVet);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {

                filteredVeterinarianList = new ArrayList<>();

                if (newText.equals("")) {
                    adapter.setVeterinarianList(veterinarianList);
                    filteredVeterinarianList.clear();
                } else {

                    for (Veterinarian veterinarian : veterinarianList) {
                        if (veterinarian.getClinicAddress().contains(" " + newText + " ")) {
                            filteredVeterinarianList.add(veterinarian);
                        }
                    }
                    adapter.setVeterinarianList(filteredVeterinarianList);
                }

                adapter.notifyDataSetChanged();

                return true;
            }
        });
    }
}
