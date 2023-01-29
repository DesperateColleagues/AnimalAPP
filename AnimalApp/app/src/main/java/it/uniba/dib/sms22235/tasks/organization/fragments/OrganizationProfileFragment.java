package it.uniba.dib.sms22235.tasks.organization.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;

public class OrganizationProfileFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_organization_profile, container, false);

        TextView organizationWelcome = rootView.findViewById(R.id.txtOrganizationWelcome);
        organizationWelcome.setText("Benvenuto, " + ((OrganizationNavigationActivity) requireActivity()).getUserId());

        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.VISIBLE);
        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);

        return rootView;
    }
}
