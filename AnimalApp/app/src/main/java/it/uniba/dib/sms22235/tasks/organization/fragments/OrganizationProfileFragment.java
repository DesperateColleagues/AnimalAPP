package it.uniba.dib.sms22235.tasks.organization.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.adapters.animals.AnimalListAdapter;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.utils.RecyclerTouchListener;

public class OrganizationProfileFragment extends Fragment {

    private RecyclerView assistedAnimalRecyclerView;
    private AnimalListAdapter adapter;
    private OrganizationProfileFragmentListener listener;
    private transient NavController controller;

    public interface OrganizationProfileFragmentListener {
        void getAssistedAnimals(AnimalListAdapter adapter, RecyclerView recyclerView);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        OrganizationNavigationActivity activity = (OrganizationNavigationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (OrganizationProfileFragment.OrganizationProfileFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_organization_profile, container, false);
        controller = Navigation.findNavController(container);

        TextView organizationWelcome = rootView.findViewById(R.id.txtOrganizationWelcome);
        organizationWelcome.setText("Benvenuto, " + ((OrganizationNavigationActivity) requireActivity()).getUserId());

        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.VISIBLE);
        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);

        assistedAnimalRecyclerView = rootView.findViewById(R.id.assistedAnimalList);

        adapter = new AnimalListAdapter(RecyclerView.VERTICAL);
        adapter.setContext(getContext());

        listener.getAssistedAnimals(adapter, assistedAnimalRecyclerView);

        assistedAnimalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        assistedAnimalRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), assistedAnimalRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(KeysNamesUtils.BundleKeys.ANIMAL, adapter.getAnimalAtPosition(position));
                controller.navigate(R.id.action_organization_profile_to_animalProfile, bundle);
            }

            @Override
            public void onLongClick(View view, int position) {
                // small help for the user.
                Snackbar snackbar = Snackbar.make(getView(),getResources().getString(R.string.animal_profile_help),Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                TypedValue value = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.windowBackground, value, true);
                snackbarView.setBackgroundColor(value.data);
                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        textView.setTextColor(Color.WHITE);
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        textView.setTextColor(Color.BLACK);
                        break;
                }
                textView.setTextSize(15);
                snackbar.show();
            }
        }));

        return rootView;
    }
}
