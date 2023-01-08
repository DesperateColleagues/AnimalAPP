package it.uniba.dib.sms22235.common_dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.AnimalResidence;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class DialogRequestDetailFragment extends Fragment {

    private Request request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        if (bundle != null) {
            request = (Request) bundle.getSerializable(KeysNamesUtils.CollectionsNames.REQUESTS);
        }

        return inflater.inflate(R.layout.fragment_request_details_and_confirmation,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button sendMail = view.findViewById(R.id.btnSendMail);

        TextView txtRequestDetailsTitle = view.findViewById(R.id.txtRequestDetailsTitle);
        TextView txtRequestDetailsRorO = view.findViewById(R.id.txtRequestDetailsRorO);
        TextView txtRequestDetailsEntityType = view.findViewById(R.id.txtRequestDetailsEntityType);
        TextView txtRequestDetailsBody = view.findViewById(R.id.txtRequestDetailsBody);

        txtRequestDetailsTitle.setText(request.getRequestTitle());
        txtRequestDetailsBody.setText(request.getRequestBody());

        txtRequestDetailsRorO.setText(new StringBuilder()
                .append(getContext().getResources().getString(R.string.oggetto))
                .append(": ")
                .append(request.getOperationType())
        );

        txtRequestDetailsEntityType.setText(
                new StringBuilder()
                        .append(getContext().getResources().getString(R.string.operation))
                        .append(": ")
                        .append(request.getRequestType())
        );

        sendMail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_EMAIL, request.getUserEmail());
            intent.putExtra(Intent.EXTRA_SUBJECT, request.getRequestTitle());
            intent.putExtra(Intent.EXTRA_TEXT, request.getRequestBody());
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.sendMail)));
        });

        /*
        *
        * Official approach
        *
        * public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
}
        * */

        /*confirmRequest.setOnClickListener(v -> {
            if (request.getRequestType().equals("Stallo")){
                //AnimalResidence temporaryResidence = new AnimalResidence();
                request.setCompleted(true);
            } else if (request.getRequestType().equals("Animale")){
                Toast.makeText(getContext(), "Funzione da implementare",Toast.LENGTH_SHORT).show();
            } else if (request.getRequestType().equals("Aiuto")){
                request.setCompleted(true);
                // query
            }
        });*/
    }
}
