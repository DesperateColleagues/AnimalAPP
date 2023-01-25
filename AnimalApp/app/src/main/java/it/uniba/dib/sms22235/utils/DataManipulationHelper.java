package it.uniba.dib.sms22235.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class DataManipulationHelper {

    /**
     * This method is used to save a Bitmap object to the internal storage
     *
     * @param bitmapImage the data to be saved
     * @param dir the directory where the data will be saved
     * @param filename the name of the file
     * @param context the context of the app
     * */
    @NonNull
    public static String saveBitmapToInternalStorage(Bitmap bitmapImage, String dir,
                                                     String filename, Context context) {

        //ContextWrapper allows to get reference to the desired image directory.
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir(dir, Context.MODE_PRIVATE);
        File file = new File(directory, filename);
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
                if (fos != null) {
                    fos.close();
                }
                //Toast.makeText(context, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }


    /**
     * This method is used to load a Bitmap object from the internal storage
     *
     * @param dirPath the path of the directory where the data is saved
     * @param filename the name of the file
     * */
    public static Bitmap loadBitmapFromStorage(String dirPath, String filename) {
        Bitmap image = null;
        try {
            // Reading the image from the path
            File f = new File(dirPath, filename);
            // Converting it into a Bitmap
            image = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * This method is used to save an object to a binary file
     *
     * @param context the context of the app
     * @param obj the object to be saved
     * @param path the path where the object will be saved
     * */
    public static boolean saveDataInternally(Context context, Object obj, String path){
        boolean isCorrect = true;
        try{
            FileOutputStream out = context.openFileOutput(path, Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream(out);
            o.writeObject(obj);

            o.close();
        }catch (Exception e){
            isCorrect = false;
            e.printStackTrace();
        }
        return isCorrect;
    }

    /**
     * This method is used to read an object from a binary file
     *
     * @param context the context of the app
     * @param path the path where the object is saved
     * */
    public static Object readDataInternally(Context context, String path){
        Object obj = null;
        try{
            FileInputStream fin = context.getApplicationContext().openFileInput(path);//open file input stream
            ObjectInputStream in = new ObjectInputStream(fin);//open object input stream

            obj = in.readObject();//reading object
            in.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }

    public static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }
}

