package it.uniba.dib.sms22235.tasks.common.dialogs.reports;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import it.uniba.dib.sms22235.BuildConfig;
import it.uniba.dib.sms22235.R;

public class DialogMap extends DialogFragment {

    private GeoPoint location;
    private MapView mapView;
    private IMapController mapController;
    private DialogMapListener listener;

    public interface DialogMapListener {
        void onPositionConfirmed(double latitude, double longitude);
    }

    public DialogMap(GeoPoint location) {
        this.location = location;
    }

    public void setListener(DialogMapListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater(); //get the layout inflater
        @SuppressLint("InflateParams") View root = inflater.inflate(R.layout.fragment_dialog_report_map, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AnimalCardRoundedDialog);
        builder.setView(root);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        mapView = root.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();

        mapController.setZoom(18);
        mapView.setMaxZoomLevel(20d);
        mapView.setMinZoomLevel(13d);

        GeoPoint startPoint = new GeoPoint(location);
        mapController.animateTo(startPoint);
        mapController.setCenter(startPoint);

        Overlay touchOverlay = new Overlay(){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;

            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }

            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {

                @SuppressLint("UseCompatLoadingForDrawables") final Drawable marker =
                        requireContext().getResources().getDrawable(org.osmdroid.library.R.drawable.marker_default_focused_base);

                Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());

                ArrayList<OverlayItem> overlayArray = new ArrayList<>();

                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double)loc.getLatitudeE6())/1000000), (((double)loc.getLongitudeE6())/1000000)));
                mapItem.setMarker(marker);
                overlayArray.add(mapItem);

                if(anotherItemizedIconOverlay==null){
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<>(requireContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();
                }else{
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<>(requireContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                }

                @SuppressLint("InflateParams") View titleView = getLayoutInflater().inflate(R.layout.fragment_dialogs_title, null);
                TextView titleText = titleView.findViewById(R.id.dialog_title);
                titleText.setText("Conferma posizione");

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AnimalCardRoundedDialog);
                builder.setMessage("Sicuro di voler confermare la posizione inserita?")
                        .setCustomTitle(titleView)
                        .setPositiveButton("Conferma", ((dialog, which) -> {
                            listener.onPositionConfirmed(loc.getLatitude(), loc.getLongitude());
                            dialog.dismiss();
                            dismiss();
                        }))
                        .setNegativeButton("Annulla", (dialog, which) -> {
                            dialog.dismiss();
                        });

                builder.show();

                return true;
            }
        };

        mapView.getOverlays().add(touchOverlay);

        return builder.create();
    }

}
