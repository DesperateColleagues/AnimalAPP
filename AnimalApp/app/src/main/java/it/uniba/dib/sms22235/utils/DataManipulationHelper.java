package it.uniba.dib.sms22235.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DataManipulationHelper {

    public static String saveToInternalStorage(Bitmap bitmapImage, String filename, Context context) {

        //ContextWrapper allows to get reference to the desired image directory.
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("AnimalAPP_Images", Context.MODE_PRIVATE);
        File file = new File(directory,filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // Use the compress method on the Bitmap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 85, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // fos.close() is used to ensure the Bitmap is saved in the app image folder
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path, String filename) {
        Bitmap image = null;
        try {
            // Reading the image from the path
            File f = new File(path, filename);
            // Converting it into a Bitmap
            image = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }
}
