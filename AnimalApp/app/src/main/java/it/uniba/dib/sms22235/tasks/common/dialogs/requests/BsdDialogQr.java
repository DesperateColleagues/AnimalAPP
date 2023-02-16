package it.uniba.dib.sms22235.tasks.common.dialogs.requests;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import it.uniba.dib.sms22235.R;

/**
 * This dialog is used to display a QR code based on animal data. It is used in
 * various occasions such as animal ownership transfer, or to show the animal profile
 * of another user.
 * */
public class BsdDialogQr extends BottomSheetDialogFragment {

    private final String microchip;
    private final String animalName;
    private final String currentOwner;
    private final boolean isSell;

    public BsdDialogQr(String microchip, String animalName, String currentOwner, boolean isSell) {
        this.microchip = microchip;
        this.animalName = animalName;
        this.currentOwner = currentOwner;
        this.isSell = isSell;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_bsd_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imgQr = view.findViewById(R.id.imgQr);

        String content = microchip + " - " + animalName + " - " + currentOwner + " - " + isSell;

        // Generate Qr code

        QRCodeWriter writer = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            imgQr.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
