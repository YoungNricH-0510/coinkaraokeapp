package com.holy.coinkaraoke.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.coinkaraoke.R;
import com.holy.coinkaraoke.models.Song;

import java.util.List;

@SuppressWarnings("unused")
public class SongRemoveAdapter extends RecyclerView.Adapter<SongRemoveAdapter.ViewHolder> {

    private final List<Song> list;
    private OnRemoveFromFavoriteClickListener onRemoveFromFavoriteClickListener;

    public SongRemoveAdapter(List<Song> list) {
        this.list = list;
    }

    public void setOnRemoveFromFavoriteClickListener(OnRemoveFromFavoriteClickListener listener) {
        this.onRemoveFromFavoriteClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView artistText;
        TextView albumText;
        ImageButton removeButton;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txt_song_title);
            artistText = itemView.findViewById(R.id.txt_song_artist);
            albumText = itemView.findViewById(R.id.txt_song_album);
            removeButton = itemView.findViewById(R.id.ibtn_remove_from_favorite);
        }

        public void bind(Song model, OnRemoveFromFavoriteClickListener listener) {

            titleText.setText(model.getTitle());
            artistText.setText(model.getArtist());
            albumText.setText(model.getAlbum());

            if (listener != null) {
                removeButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRemoveFromFavoriteClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_remove, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Song item = list.get(position);
        holder.bind(item, onRemoveFromFavoriteClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnRemoveFromFavoriteClickListener {
        void onRemoveFromFavoriteClick(int position);
    }

}
