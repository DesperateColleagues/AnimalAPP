package it.uniba.dib.sms22235.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.entities.operations.PhotoDiaryPost;

public class PostGridAdapter extends RecyclerView.Adapter<PostGridAdapter.ViewHolder> {
    private final List<PhotoDiaryPost> posts;
    private final Context context;
    private ViewHolder.OnItemClickListener onItemClickListener;

    public PostGridAdapter(Context context, List<PhotoDiaryPost>posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_fragment_photo_diary_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhotoDiaryPost post = posts.get(position);

        // Use glide to display the post using its URI
        Glide.with(context).load(post.getPostUri()).into(holder.photoEntry);

        holder.photoEntry.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(post.getPostUri());
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView photoEntry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoEntry = itemView.findViewById(R.id.photoEntry);
        }

        public interface OnItemClickListener {
            void onItemClick(String uri);
        }
    }

    public void setOnItemClickListener(ViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
