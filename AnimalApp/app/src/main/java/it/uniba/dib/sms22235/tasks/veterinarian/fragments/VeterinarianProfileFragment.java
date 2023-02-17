package it.uniba.dib.sms22235.tasks.veterinarian.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.veterinarian.VeterinarianNavigationActivity;

/**
 * Class to show the veterinarian profile section
 * */
public class VeterinarianProfileFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_veterinarian_profile, container, false);

        String title = getString(R.string.benvenuto) + ", " + ((VeterinarianNavigationActivity) requireActivity())
                .getVeterinarianFullName();

        ((TextView) rootView.findViewById(R.id.txtVeterinarianWelcome)).setText(title);

        ((VeterinarianNavigationActivity) requireActivity()).getFab().setVisibility(View.GONE);


        rootView.findViewById(R.id.cardViewVeterinarian).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(container) ;
            navController.navigate(R.id.action_passionate_profile_to_reportsDashboardFragment);
        });

        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.VISIBLE);
        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.VISIBLE);

        return rootView;
    }
}