package it.uniba.dib.sms22235.tasks.registration.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.dib.sms22235.R;

/**
 * This fragment is used to navigate through the fragments that compose the RegistrationActivity.
 * From here it is possible to access the registration form of the three actor of the app.
 *
 * @author Giacomo Detomaso
 * */
public class RegistrationRoleChoiceFragment extends Fragment {

    private NavController controller;

    public RegistrationRoleChoiceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controller = Navigation.findNavController(container);

        return inflater.inflate(R.layout.fragment_registration_role_choice, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtRegUser = view.findViewById(R.id.txtRegUser);
        TextView txtRegVeterinary = view.findViewById(R.id.txtRegVeterinary);
        TextView txtRegOrganization = view.findViewById(R.id.txtRegOrganization);

        // Set 3 onClickListeners to perform navigation through registration's fragments

        txtRegUser.setOnClickListener(v ->
                controller.navigate(R.id.registrationUserFragment));

        txtRegVeterinary.setOnClickListener(v ->
                controller.navigate(R.id.registrationVeterinaryFragment));

        txtRegOrganization.setOnClickListener(v ->
                controller.navigate(R.id.registrationOrganizationFragment));
    }
}