package it.uniba.dib.sms22235.tasks.organization.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Locale;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.users.Organization;
import it.uniba.dib.sms22235.tasks.organization.OrganizationNavigationActivity;

public class OrganizationProfileFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_organization_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        TextView organizationWelcome = rootView.findViewById(R.id.txtOrganizationWelcome);
        organizationWelcome.setText("Benvenuto, " + mAuth.getCurrentUser().getEmail());

        Context ctx = requireContext();
        /*Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        MapView map = rootView.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();

        GeoPoint startPoint = new GeoPoint(40.9202986785771, 17.02601805879099);
        mapController.animateTo(startPoint);
        mapController.setCenter(startPoint);
        mapController.setZoom(18);
        map.setMaxZoomLevel(19.3d);


        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(40.9202986785771, 17.02601805879099)));
        // Lat/Lon decimal degrees

        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items, null, requireContext());


        ((OrganizationNavigationActivity) requireActivity()).getFab().setOnClickListener(v -> {
            Toast.makeText(getContext(),"Still nothing, but organizations",Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.testButton).setOnClickListener(v -> {
            double latitude = 40.9202986785771;
            double longitude = 17.02601805879099;
            String label = "Location segnalazione";
            String uriBegin = "geo:" + latitude + "," + longitude;
            String query = latitude + "," + longitude + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });*/

        return rootView;
    }
}
