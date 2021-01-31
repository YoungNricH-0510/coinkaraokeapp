package com.holy.coinkaraoke.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.coinkaraoke.R;
import com.holy.coinkaraoke.models.Post;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final List<Post> list;
    private OnItemClickListener onItemClickListener;

    public PostAdapter(List<Post> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView userText;
        TextView dateText;
        TextView likesText;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txt_post_title);
            userText = itemView.findViewById(R.id.txt_post_user);
            dateText = itemView.findViewById(R.id.txt_post_date);
            likesText = itemView.findViewById(R.id.txt_post_likes);
        }

        public void bind(Post model, OnItemClickListener listener) {

            titleText.setText(model.getTitle());
            userText.setText(model.getUserId());

            String strDate = String.format(Locale.getDefault(),
                    "%02d-%02d %02d:%02d",
                    model.getUploadTime().getMonthValue(),
                    model.getUploadTime().getDayOfMonth(),
                    model.getUploadTime().getHour(),
                    model.getUploadTime().getMinute());
            dateText.setText(strDate);

            String strLikes = String.format(Locale.getDefault(), "+%d", model.getLikes());
            likesText.setText(strLikes);

            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Post item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}