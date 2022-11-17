package it.uniba.dib.sms22235.activities.registration;

import it.uniba.dib.sms22235.utils.InputFieldCheck;

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

/**
 * This fragment contains the logic of an organization's registration
 *
 * @author Giacomo Detomaso
 * */
public class RegistrationOrganizationFragment extends Fragment {

    public interface RegistrationOrganizationFragmentListener {
        /**
         * Triggered when the organization completes its sign up process
         *
         * @param org the organization to register
         * @param pwd the password of the user
         * */
        void onOrganizationRegistered(Organization org, String pwd);
    }

    private RegistrationOrganizationFragmentListener listener;

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
            boolean isEmptyInput = organizationName.equals("") || email.equals("") ||
                    phoneNumber.equals("") || purpose.equals("") || password.equals("");

            boolean isInputCorrect = InputFieldCheck.isEmailValid(email) &&
                    InputFieldCheck.isNumberValid(phoneNumber) &&
                    InputFieldCheck.isPasswordValid(password) ;

            // Communicate to the RegistrationActivity that the organization can be registered
            if (!isEmptyInput && isInputCorrect) {
                listener.onOrganizationRegistered(
                        new Organization(organizationName, purpose, email, phoneNumber), password
                );
            }

            if (!InputFieldCheck.isEmailValid(email)) {
                Toast.makeText(getActivity(), "Email non valida.", Toast.LENGTH_SHORT).show();
            }

            if(!InputFieldCheck.isNumberValid(phoneNumber)) {
                Toast.makeText(getActivity(), "Numero di telefono non valido.", Toast.LENGTH_SHORT).show();
            }

            if(!InputFieldCheck.isPasswordValid(password)) {
                Toast.makeText(getActivity(), "Password troppo corta, minimo 6 caratteri.", Toast.LENGTH_SHORT).show();
            }

            if (isEmptyInput) {
                Toast.makeText(getActivity(), "Alcuni campi sono vuoti!", Toast.LENGTH_SHORT).show();
            }

        });
    }
}