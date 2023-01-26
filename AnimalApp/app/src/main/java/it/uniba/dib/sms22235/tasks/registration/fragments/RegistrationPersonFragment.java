package it.uniba.dib.sms22235.tasks.registration.fragments;

import it.uniba.dib.sms22235.tasks.registration.RegistrationActivity;
import it.uniba.dib.sms22235.tasks.registration.dialogs.DialogAddAddress;
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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.security.NoSuchAlgorithmException;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;
import it.uniba.dib.sms22235.entities.users.Passionate;
import it.uniba.dib.sms22235.entities.users.Veterinarian;

/**
 * This fragment contains the logic of a person registration.
 * A person can be a user or a veterinary. This class menages the visibility of its
 * layout components, according to the role of the person who is registering.
 *
 * @author Giacomo Detomaso
 * */
public class RegistrationPersonFragment extends Fragment implements DialogAddAddress.DialogAddAddressListener {

    public interface RegistrationPersonFragmentListener {
        /**
         * Triggered when the user completes its sign up process
         *
         * @param passionate the user to register
         * // @param pwd the password of the user
         * */
        void onPassionateRegistered(Passionate passionate);

        /**
         * Triggered when the veterinary completes its sign up process
         *
         * @param veterinarian the veterinary to register
         * // @param pwd the password of the veterinary
         * */
        void onVeterinaryRegistered(Veterinarian veterinarian);
    }

    // The role of the actor who is registering: it can be a user or a veterinary
    private String role;
    private String address;

    // The listener used to communicate with the RegistrationActivity
    private RegistrationPersonFragmentListener listener;

    // Role's text input layouts to make visible or gone according the role of the actor
    private TextInputLayout layoutTxtInputClinicName;
    private TextInputLayout layoutTxtInputPhoneNumber;
    private TextInputLayout layoutTxtInputUsername;
    private TextInputLayout layoutTxtInputAddress;

    private EditText txtInputAddress;

    public RegistrationPersonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        RegistrationActivity activity = (RegistrationActivity) getActivity();

        try {
            // Attach the listener to the Fragment
            listener = (RegistrationPersonFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    (activity != null ? activity.toString() : null)
                    + "Must implement the interface");
        }

        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration_person, container, false);

        layoutTxtInputUsername = view.findViewById(R.id.layoutTxtInputUsername);
        layoutTxtInputPhoneNumber = view.findViewById(R.id.layoutTxtInputPhoneNumber);
        layoutTxtInputClinicName = view.findViewById(R.id.layoutTxtInputClinicName);
        layoutTxtInputAddress = view.findViewById(R.id.layoutTxtInputAddress);

        txtInputAddress = view.findViewById(R.id.txtInputAddress);

        // Get the arguments passed by the navigation object
        Bundle arguments = getArguments();

        // Retrieve the role from the Bundle
        if (arguments != null) {
            role = arguments.getString("Role");
        }

        // Set the correct visibility according to the role
        manageTxtInputVisibility();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.txtInputAddress).setOnClickListener(v -> {
            DialogAddAddress dialogAddAddress = new DialogAddAddress();
            dialogAddAddress.setListener(this);
            dialogAddAddress.show(getChildFragmentManager(), "DialogAddAddress");
        });

        Button btnConfirmRegistration = view.findViewById(R.id.btnConfirmRegistration);

        // Add the onClickListener to the Button
        btnConfirmRegistration.setOnClickListener(v -> {
            // Retrieve the values from the commons EdiText
            String name = ((EditText) view.findViewById(R.id.txtInputName)).getText().toString();

            String email = ((EditText) view.findViewById(R.id.txtInputEmail)).getText().toString();

            String password = ((EditText) view.findViewById(R.id.txtInputPassword))
                    .getText().toString();

            // Role's fields
            String username, clinicName, phoneNumber;

            // Evaluate this expression to check if the three common fields are empty or not
            // The flag isEmptyInput will be true if the three strings are empty, false otherwise
            boolean isEmptyInput = name.equals("") || email.equals("") || password.equals("") || address.equals("");

            boolean isInputCorrect = InputFieldCheck.isEmailValid(email) && InputFieldCheck.isPasswordValid(password);


            // If the input is not empty complete registration process
            if (!isEmptyInput && isInputCorrect) {

                // Encode the password before saving it
                try {
                    password = InputFieldCheck.encodePassword(password);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                if (role.equals(KeysNamesUtils.RolesNames.COMMON_USER)) {
                    // Retrieve user specific field
                    username = ((EditText) view.findViewById(R.id.txtInputUsername))
                            .getText().toString();

                    isEmptyInput = username.equals("");

                    if (!isEmptyInput) {
                        // Delegate the Activity to register the user on the FireStore
                        listener.onPassionateRegistered(new Passionate(name, email, username, password, role));
                    }

                } else {
                    // Retrieve veterinary specifics fields
                    clinicName = ((EditText) view.findViewById(R.id.txtInputClinicName))
                            .getText().toString();

                    phoneNumber = ((EditText) view.findViewById(R.id.txtInputPhoneNumber))
                            .getText().toString();

                    isEmptyInput = clinicName.equals("") || phoneNumber.equals("");

                    isInputCorrect = InputFieldCheck.isNumberValid(phoneNumber);

                    if (!isEmptyInput && isInputCorrect) {
                        // Delegate the Activity to register the veterinary on the FireStore
                        listener.onVeterinaryRegistered(
                                new Veterinarian(name, email, clinicName, phoneNumber, password, role, address));
                    }

                    if (!InputFieldCheck.isNumberValid(phoneNumber)){
                        Toast.makeText(getActivity(), "Numero di telefono non valido.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            // Display error message if some fields are empty
            if (isEmptyInput) {
                Toast.makeText(getActivity(), "Alcuni campi sono vuoti!", Toast.LENGTH_SHORT).show();
            }

            if (!InputFieldCheck.isEmailValid(email)) {
                Toast.makeText(getActivity(), "Email non valida.", Toast.LENGTH_SHORT).show();
            }

            if(!InputFieldCheck.isPasswordValid(password)) {
                Toast.makeText(getActivity(), "Password troppo corta, minimo 6 caratteri.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This function is used to make EditText visible or not based on
     * the role of the actor who's registering
     * */
    private void manageTxtInputVisibility() {
        if (role.equals(KeysNamesUtils.RolesNames.COMMON_USER)) {
            layoutTxtInputClinicName.setVisibility(View.GONE);
            layoutTxtInputPhoneNumber.setVisibility(View.GONE);
            layoutTxtInputUsername.setVisibility(View.VISIBLE);
        } else if (role.equals(KeysNamesUtils.RolesNames.VETERINARIAN)){
            layoutTxtInputClinicName.setVisibility(View.VISIBLE);
            layoutTxtInputPhoneNumber.setVisibility(View.VISIBLE);
            layoutTxtInputAddress.setVisibility(View.VISIBLE);
            layoutTxtInputUsername.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddressConfirmed(String address) {
        this.address = address;
        txtInputAddress.setText(address);
    }
}