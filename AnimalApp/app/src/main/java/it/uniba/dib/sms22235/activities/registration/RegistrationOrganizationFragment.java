package it.uniba.dib.sms22235.activities.registration;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Organization;

public class RegistrationOrganizationFragment extends Fragment {

    public interface RegistrationOrganizationFragmentListener {
        void onOrganizationRegistered(Organization org, String pwd);
    }

    RegistrationOrganizationFragmentListener listener;

    public RegistrationOrganizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        RegistrationActivity activity = (RegistrationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (RegistrationOrganizationFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                            + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_organization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnRegistration = view.findViewById(R.id.btnConfirmOrgRegistration);

        btnRegistration.setOnClickListener(v -> {
            // Retrieve values from input fields
            String organizationName = ((EditText) view.findViewById(R.id.txtInputOrgName))
                    .getText().toString();

            String email = ((EditText) view.findViewById(R.id.txtInputOrgEmail)).getText().toString();

            String phoneNumber = ((EditText) view.findViewById(R.id.txtInputOrgPhoneNumber))
                    .getText().toString();

            String password = ((EditText)view.findViewById(R.id.txtInputOrgPassword))
                    .getText().toString();

            String purpose = (String) ((Spinner) view.findViewById(R.id.spinnerPurpose))
                    .getSelectedItem();

            // Check if the input is empty or not
            boolean isEmptyInput = organizationName.equals("") || email.equals("")
                    || phoneNumber.equals("") || purpose.equals("") || password.equals("");

            // TODO: check email and phone number pattern and password must contain up to 6 chars
            boolean isInputCorrect;

            // Communicate to the RegistrationActivity that the organization can be registered
            if (!isEmptyInput) {
                listener.onOrganizationRegistered(
                        new Organization(organizationName, purpose, email, phoneNumber), password
                );
            }

            if (isEmptyInput) {
                Toast.makeText(getActivity(), "Alcuni campi sono vuoti!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}