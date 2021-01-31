package com.holy.coinkaraoke;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public class RoomFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_RESERVATION = 100;

    private static final int[] ROOM_VIEW_IDS = {
            R.id.txt_room_01, R.id.txt_room_02, R.id.txt_room_03, R.id.txt_room_04,
            R.id.txt_room_05, R.id.txt_room_06, R.id.txt_room_07, R.id.txt_room_08,
            R.id.txt_room_09, R.id.txt_room_10, R.id.txt_room_11, R.id.txt_room_12,
            R.id.txt_room_13, R.id.txt_room_14, R.id.txt_room_15, R.id.txt_room_16,
            R.id.txt_room_17, R.id.txt_room_18, R.id.txt_room_19,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_room, container, false);

        // Set listeners to views
        for (int roomViewId : ROOM_VIEW_IDS) {
            root.findViewById(roomViewId).setOnClickListener(this);
        }

        ImageButton refreshRoomButton = root.findViewById(R.id.ibtn_refresh_room);
        refreshRoomButton.setOnClickListener(this);

        // Update room status
        updateRoomStatus(root);

        return root;
    }

    // Update status of rooms (empty or occupied)

    private void updateRoomStatus(View root) {

        // Get current time
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < ROOM_VIEW_IDS.length; i++) {

            // Get reservation list of each room
            List<Reservation> reservationList = SQLiteHelper
                    .getInstance(getContext())
                    .getReservationsBy(1 + i);

            // Check if the room is currently occupied or not
            boolean isEmpty = true;

            for (Reservation reservation : reservationList) {
                if (reservation.conflictsWith(now)) {
                    isEmpty = false;
                    break;
                }
            }

            // Set background of each room view according to its status
            int color = getResources().getColor(
                    isEmpty ? R.color.colorEmpty : R.color.colorOccupied,
                    null);
            root.findViewById(ROOM_VIEW_IDS[i]).setBackgroundColor(color);
        }
    }

    @Override
    public void onClick(View v) {

        // Check if any room view has been clicked
        for (int i = 0; i < ROOM_VIEW_IDS.length; i++) {
            if (v.getId() == ROOM_VIEW_IDS[i]) {
                // Then, try reservation of the room
                tryReservation(1 + i);
            }
        }

        if (v.getId() == R.id.ibtn_refresh_room) {
            // Update room status
            updateRoomStatus(getView());
        }

    }

    public void tryReservation(int roomNumber) {

        // Launch reservation activity
        Intent intent = new Intent(getContext(), ReservationActivity.class);
        intent.putExtra(ReservationActivity.EXTRA_ROOM_NUMBER, roomNumber);
        startActivityForResult(intent, REQUEST_RESERVATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RESERVATION) {
            // Update room status
            updateRoomStatus(getView());
        }
    }
}