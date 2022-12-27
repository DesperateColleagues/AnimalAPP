package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;
import it.uniba.dib.sms22235.utils.DataManipulationHelper;

public class ListViewPhotoDiaryAdapter extends ArrayAdapter<PhotoDiaryPost> {
    private List<PhotoDiaryPost> postList;
    private List<Bitmap> picList;

    public ListViewPhotoDiaryAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        postList = new ArrayList<>();
        picList = new ArrayList<>();
    }

    public void addPost(PhotoDiaryPost post) {
        /*postList.add(post);
        // Load the image from the file
        picList.add(DataManipulationHelper
                .loadBitmapFromStorage(post.getDirName(), post.getFileName()));*/
    }

    @Override
    public int getCount() {
        return postList.size();
    }

    @Nullable
    @Override
    public PhotoDiaryPost getItem(int position) {
        return postList.get(position);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_fragment_photo_diary_list, parent, false);
        }

        GridView gridView = listView.findViewById(R.id.photoGrid);

        return listView;
    }

}
