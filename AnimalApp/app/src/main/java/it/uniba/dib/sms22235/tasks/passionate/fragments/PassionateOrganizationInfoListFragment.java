package it.uniba.dib.sms22235.tasks.passionate.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.info.EntityInfoAdapter;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.tasks.passionate.PassionateNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

/**
 * This fragment is used to show a list of organizations that can be filtered by a search of
 * specific info using its search bar. Every single element of the list provides two operations:
 * 1) call a phone number
 * 2) text email
 * */
public class PassionateOrganizationInfoListFragment extends Fragment {

    private ArrayList<Organization> organizationsList;
    private ArrayList<Organization> filteredOrganizationList;

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((PassionateNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);
        ((PassionateNavigationActivity) requireActivity()).setNavViewVisibility(View.GONE);

        Bundle arguments = getArguments();

        if (arguments != null) {
            organizationsList = (ArrayList<Organization>) arguments.getSerializable(KeysNamesUtils.BundleKeys.ORGANIZATIONS_LIST);
        }

        return inflater.inflate(R.layout.fragment_passionate_organization_info_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EntityInfoAdapter adapter = new EntityInfoAdapter(EntityInfoAdapter.AdapterMode.ORG);
        adapter.setContext(requireContext());
        adapter.setOrganizationList(organizationsList);

        RecyclerView recyclerView = view.findViewById(R.id.organizationListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchViewOrg);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                filteredOrganizationList = new ArrayList<>();

                if (newText.equals("")) {
                    adapter.setOrganizationList(organizationsList);
                    filteredOrganizationList.clear();
                } else {

                    for (Organization organization : organizationsList) {
                        if (organization.getOrgAddress().contains(" " + newText + " ")) {
                            filteredOrganizationList.add(organization);
                        }
                    }
                    adapter.setOrganizationList(filteredOrganizationList);
                }

                adapter.notifyDataSetChanged();

                return true;
            }
        });

    }
}
