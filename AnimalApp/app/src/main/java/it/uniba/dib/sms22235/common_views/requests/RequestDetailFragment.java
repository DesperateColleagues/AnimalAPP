package it.uniba.dib.sms22235.common_views.requests;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.activities.ActivityInterface;
import it.uniba.dib.sms22235.common_views.backbench.BackBenchFragment;
import it.uniba.dib.sms22235.entities.operations.Request;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class RequestDetailFragment extends Fragment {

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
        TextView txtRequestDetailsEntityType = view.findViewById(R.id.txtRequestDetailsEntityType);
        TextView txtRequestDetailsBody = view.findViewById(R.id.txtRequestDetailsBody);

        txtRequestDetailsTitle.setText(request.getRequestTitle());
        txtRequestDetailsBody.setText(request.getRequestBody());

        // Setup request details messages
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

        // Manage backbenches requests
        if (request.getRequestType().equals("Offerta stallo")) {

            ViewPager viewpagerRequests = view.findViewById(R.id.viewpagerRequests);
            viewpagerRequests.setVisibility(View.VISIBLE);

            view.findViewById(R.id.infoDivider).setVisibility(View.VISIBLE);
            view.findViewById(R.id.txtRequestDetailsTitle2).setVisibility(View.VISIBLE);

            Adapter adapter = new Adapter(getChildFragmentManager());
            adapter.addFragment(new BackBenchFragment(request.getUserEmail()));

            viewpagerRequests.setAdapter(adapter);
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private Fragment fragment;


        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        public void addFragment(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Informazioni stallo";
        }
    }
}
