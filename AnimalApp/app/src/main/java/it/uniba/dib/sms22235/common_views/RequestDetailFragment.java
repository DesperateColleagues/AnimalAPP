package it.uniba.dib.sms22235.common_views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Request;

public class RequestDetailFragment extends Fragment {

    private Request request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            request = (Request) bundle.getSerializable("Richiesta");
        }

        return inflater.inflate(R.layout.fragment_request_details_and_confirmation,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button inviaMail = view.findViewById(R.id.invioMail);
        Button conferma = view.findViewById(R.id.confermaRichiesta);

        inviaMail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, request.getUserEmail());
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        conferma.setOnClickListener(v -> {

        });
    }
}
