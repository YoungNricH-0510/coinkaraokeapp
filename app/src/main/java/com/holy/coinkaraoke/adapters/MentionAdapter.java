package com.holy.coinkaraoke.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.coinkaraoke.R;
import com.holy.coinkaraoke.models.Mention;

import java.util.List;

@SuppressWarnings("unused")
public class MentionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_MENTION = 0;
    public static final int VIEW_TYPE_BUTTON = 1;

    private final List<Mention> list;
    private OnQuestionClickListener onQuestionClickListener;

    public MentionAdapter(List<Mention> list) {
        this.list = list;
    }

    public void setOnQuestionClickListener(OnQuestionClickListener listener) {
        this.onQuestionClickListener = listener;
    }

    public static class MentionViewHolder extends RecyclerView.ViewHolder {

        TextView writerText;
        TextView messageText;

        public MentionViewHolder(View itemView) {
            super(itemView);

            writerText = itemView.findViewById(R.id.txt_mention_writer);
            messageText = itemView.findViewById(R.id.txt_mention_message);
        }

        public void bind(Mention model) {

            writerText.setText(model.getWriter());
            messageText.setText(model.getMessage());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            if (model.getWriter().equals("Me")) {
                params.setMarginStart(70);
                params.setMarginEnd(0);
            } else {
                params.setMarginStart(0);
                params.setMarginEnd(70);
            }
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        Button button;

        public ButtonViewHolder(@NonNull View itemView, OnQuestionClickListener listener) {
            super(itemView);

            button = itemView.findViewById(R.id.btn_question);

            if (listener != null) {
                button.setOnClickListener(v -> {
                    listener.onQuestionClick();
                });
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        switch (viewType) {
            default:
            case VIEW_TYPE_MENTION:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_mention, parent, false);
                return new MentionViewHolder(itemView);
            case VIEW_TYPE_BUTTON:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_button_view, parent, false);
                return new ButtonViewHolder(itemView, onQuestionClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VIEW_TYPE_MENTION) {
            Mention item = list.get(position);
            ((MentionViewHolder)holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public interface OnQuestionClickListener {
        void onQuestionClick();
    }

    @Override
    public int getItemViewType(int position) {

        if (position < list.size()) {
            return VIEW_TYPE_MENTION;
        } else {
            return VIEW_TYPE_BUTTON;
        }
    }
}