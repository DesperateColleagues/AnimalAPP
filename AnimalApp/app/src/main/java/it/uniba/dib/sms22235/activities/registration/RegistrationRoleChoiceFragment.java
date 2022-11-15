package it.uniba.dib.sms22235.activities.registration;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Objects;

import it.uniba.dib.sms22235.R;

public class RegistrationRoleChoiceFragment extends Fragment {

    public RegistrationRoleChoiceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the view of the fragment
        View root =  inflater.inflate(R.layout.fragment_registration_role_choice, container,
                false);

        NavController controller = Navigation.findNavController(container);

        TextView txtRegUser = root.findViewById(R.id.txtRegUser);
        TextView txtRegVeterinary = root.findViewById(R.id.txtRegVeterinary);
        TextView txtRegOrganization = root.findViewById(R.id.txtRegOrganization);

        // Set 3 onClickListeners to perform navigation through registration's fragments

        txtRegUser.setOnClickListener(v ->
                controller.navigate(R.id.registrationUserFragment));

        txtRegVeterinary.setOnClickListener(v ->
                controller.navigate(R.id.registrationVeterinaryFragment));

        txtRegOrganization.setOnClickListener(v ->
                controller.navigate(R.id.registrationOrganizationFragment));

        return root;
    }
}