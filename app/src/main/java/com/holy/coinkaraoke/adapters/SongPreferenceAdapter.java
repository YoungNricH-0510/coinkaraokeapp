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
public class SongPreferenceAdapter extends RecyclerView.Adapter<SongPreferenceAdapter.ViewHolder> {

    private final List<Song> list;
    private OnAddToFavoriteClickListener onAddToFavoriteClickListener;

    public SongPreferenceAdapter(List<Song> list) {
        this.list = list;
    }

    public void setOnAddToFavoriteClickListener(OnAddToFavoriteClickListener listener) {
        this.onAddToFavoriteClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView artistText;
        TextView albumText;
        TextView preferenceText;
        ImageButton addButton;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txt_song_title);
            artistText = itemView.findViewById(R.id.txt_song_artist);
            albumText = itemView.findViewById(R.id.txt_song_album);
            preferenceText = itemView.findViewById(R.id.txt_preference);
            addButton = itemView.findViewById(R.id.ibtn_add_to_favorite);
        }

        public void bind(Song model, OnAddToFavoriteClickListener listener) {

            titleText.setText(model.getTitle());
            artistText.setText(model.getArtist());
            albumText.setText(model.getAlbum());
            preferenceText.setText(String.valueOf(model.getPreference()));

            if (listener != null) {
                addButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAddToFavoriteClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_preference, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Song item = list.get(position);
        holder.bind(item, onAddToFavoriteClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnAddToFavoriteClickListener {
        void onAddToFavoriteClick(int position);
    }

}