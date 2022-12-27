package it.uniba.dib.sms22235.activities.veterinarian.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.veterinarian.VeterinarianNavigationActivity;

public class VeterinarianProfileFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_veterinarian_profile, container, false);

        String title = "Benvenuto, " + ((VeterinarianNavigationActivity) requireActivity())
                .getVeterinarianFullName();

        ((TextView) rootView.findViewById(R.id.txtVeterinarianWelcome)).setText(title);

        return rootView;
    }
}