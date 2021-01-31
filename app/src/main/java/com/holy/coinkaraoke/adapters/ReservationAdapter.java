package com.holy.coinkaraoke.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.coinkaraoke.R;
import com.holy.coinkaraoke.models.Reservation;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private final List<Reservation> list;
    private OnItemClickListener onItemClickListener;

    public ReservationAdapter(List<Reservation> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView beginTimeText;
        TextView endTimeText;
        TextView userIdText;

        public ViewHolder(View itemView) {
            super(itemView);

            beginTimeText = itemView.findViewById(R.id.txt_begin_time);
            endTimeText = itemView.findViewById(R.id.txt_end_time);
            userIdText = itemView.findViewById(R.id.txt_user_id);
        }

        public void bind(Reservation model, OnItemClickListener listener) {

            int beginHour = model.getBeginTime().getHour();
            String strBeginTime = String.format(Locale.getDefault(),
                    "%s %02d:%02d",
                    beginHour <= 12 ? "AM" : "PM",
                    beginHour <= 12 ? beginHour : beginHour - 12,
                    model.getBeginTime().getMinute());
            beginTimeText.setText(strBeginTime);

            int endHour = model.getEndTime().getHour();
            String strEndTime = String.format(Locale.getDefault(),
                    "%s %02d:%02d",
                    endHour <= 12 ? "AM" : "PM",
                    endHour <= 12 ? endHour : endHour - 12,
                    model.getEndTime().getMinute());
            endTimeText.setText(strEndTime);

            String userId = model.getUserId();
            if (userId.charAt(0) != '#') {
                userIdText.setText(model.getUserId());
            } else {
                String hiddenPhone = userId
                        .substring(0, userId.length() - 4)
                        .concat("****");
                userIdText.setText(hiddenPhone);
            }

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
                .inflate(R.layout.item_reservation, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Reservation item = list.get(position);
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