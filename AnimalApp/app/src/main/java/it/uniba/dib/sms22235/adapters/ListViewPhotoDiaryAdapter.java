package it.uniba.dib.sms22235.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public ListViewPhotoDiaryAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        postList = new ArrayList<>();
    }

    public void addPost(PhotoDiaryPost post) {
        postList.add(post);
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

        ImageView photoDiaryImage = listView.findViewById(R.id.photoDiaryImage);

        TextView txtPostTitle = listView.findViewById(R.id.txtPostTitle);
        TextView txtImageDescription = listView.findViewById(R.id.txtImageDescription);

        PhotoDiaryPost post = getItem(position);

        if (post != null) {
            // Set the texts to the Text views
            txtImageDescription.setText(post.getDescription());
            txtPostTitle.setText(post.getTitle());

            // Obtain the the input data used to load the image
            String dirName = post.getDirName();
            String fileName = post.getFileName();

            // Load the image from the file
            Bitmap bitmap = DataManipulationHelper
                    .loadBitmapFromStorage(dirName, fileName);

            if (bitmap != null) {
                photoDiaryImage.setImageBitmap(bitmap);
            }
        }

        return listView;
    }

}
