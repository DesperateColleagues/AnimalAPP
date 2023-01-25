package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.Report;
import it.uniba.dib.sms22235.utils.KeysNamesUtils;

public class ReportDetailFragment extends Fragment {

    private Report report = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            report = (Report) arguments.getSerializable(KeysNamesUtils.BundleKeys.REPORT_SHOW);
        }

        return inflater.inflate(R.layout.fragment_reports_details, container, false);
    }

    @SuppressLint({"SetTextI18n", "QueryPermissionsNeeded"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find layout objects

        TextView txtReportDescriptionDetail = view.findViewById(R.id.txtReportDescriptionDetail);
        TextView txtPositionSumUpDetail = view.findViewById(R.id.txtPositionSumUpDetail);
        TextView txtReportAnimalDetail = view.findViewById(R.id.txtReportAnimalDetail);
        TextView txtReportDetailTitle = view.findViewById(R.id.txtReportDetailTitle);
        TextView txtReportReporterDetail = view.findViewById(R.id.txtReportReporterDetail);

        MapView reportMapDetail = view.findViewById(R.id.reportMapDetail);

        ImageView imgReportDetail = view.findViewById(R.id.imgReportDetail);

        Button btnContactReporter = view.findViewById(R.id.btnContactReporter);
        Button btnReportMapIntent = view.findViewById(R.id.btnReportMapIntent);
        Button btnReportAnimal = view.findViewById(R.id.btnReportAnimal);

        // Set the text for the text views
        txtReportDetailTitle.setText(report.getReportTitle());
        txtReportDescriptionDetail.setText(report.getReportDescription());
        txtPositionSumUpDetail.setText(report.getReportAddress());
        txtReportReporterDetail.setText("Dati segnalatore:\n- " + report.getReporter());

        if (!report.getReportAnimal().equals("")) {
            txtReportAnimalDetail.setText(report.getReportAnimal());
        } else {
            // if the animal is not set update some items visibility
            view.findViewById(R.id.txtReportDetailTitleAnimalSection).setVisibility(View.GONE);
            view.findViewById(R.id.animalDivider).setVisibility(View.GONE);
            txtReportDetailTitle.setVisibility(View.GONE);
            btnReportAnimal.setVisibility(View.GONE);
        }

        // Set up the map view
        reportMapDetail.setTileSource(TileSourceFactory.MAPNIK);
        reportMapDetail.setBuiltInZoomControls(false);
        reportMapDetail.setMultiTouchControls(false);
        IMapController mapController = reportMapDetail.getController();

        mapController.setZoom(18);
        reportMapDetail.setMaxZoomLevel(20d);
        reportMapDetail.setMinZoomLevel(13d);

        // Animate to the point of the report
        GeoPoint startPoint = new GeoPoint(report.getLat(), report.getLon());

        ArrayList<OverlayItem> overlayArray = new ArrayList<>();

        @SuppressLint("UseCompatLoadingForDrawables") final Drawable marker =
                requireContext().getResources().getDrawable(org.osmdroid.library.R.drawable.marker_default);

        OverlayItem item = new OverlayItem("Posizione segnalazione", "", startPoint);
        item.setMarker(marker);
        overlayArray.add(item);

        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<>(requireContext(),
                overlayArray,null);

        reportMapDetail.getOverlays().add(itemizedIconOverlay);
        mapController.animateTo(startPoint);
        mapController.setCenter(startPoint);

        // Setup the report image with Glide
        if (!report.getReportHelpPictureUri().equals("")) {
            Glide.with(requireContext()).load(report.getReportHelpPictureUri()).into(imgReportDetail);
        } else {
            // If the image is not set manage visibility
            view.findViewById(R.id.txtImgReportTitle).setVisibility(View.GONE);
            view.findViewById(R.id.imgDivider).setVisibility(View.GONE);
            imgReportDetail.setVisibility(View.GONE);
        }

        btnReportMapIntent.setOnClickListener(v -> {
            double myLatitude = report.getLat();
            double myLongitude = report.getLon();
            String labelLocation = "Segnalazione " + report.getReporter();

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:<" + myLatitude  + ">,<" + myLongitude + ">?q=<" +
                            myLatitude  + ">,<" + myLongitude + ">(" + labelLocation + ")"));

            intent.setPackage("com.google.android.apps.maps");

            startActivity(intent);
        });

        btnReportAnimal.setOnClickListener(v -> {
            // todo: animal profile
        });

        btnContactReporter.setOnClickListener(v -> {
            // todo: contact intent
            composeEmail(new String [] {report.getReporter()}, report.getReportTitle());
        });

    }

    @SuppressLint("QueryPermissionsNeeded")
    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        startActivity(intent);
    }
}
