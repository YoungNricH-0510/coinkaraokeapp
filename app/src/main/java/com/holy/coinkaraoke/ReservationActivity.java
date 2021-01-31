package com.holy.coinkaraoke;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.holy.coinkaraoke.adapters.ReservationAdapter;
import com.holy.coinkaraoke.helpers.PaymentHelper;
import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.Reservation;
import com.holy.coinkaraoke.models.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ROOM_NUMBER = "com.holy.coinkaraoke.room_number";

    private int mRoomNumber;

    private ReservationAdapter mReservationAdapter;
    private List<Reservation> mReservationList;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        mUserId = ((App)getApplication()).getCurrentId();

        // Get room number
        mRoomNumber = getIntent().getIntExtra(EXTRA_ROOM_NUMBER, -1);

        // Display room number
        TextView roomNumberText = findViewById(R.id.txt_room_number);
        String strRoomNumber = String.format(Locale.getDefault(), "Room %02d", mRoomNumber);
        roomNumberText.setText(strRoomNumber);

        // Build reservation recycler view
        buildReservationRecycler();

        // Set listeners to view
        TextView reserveText = findViewById(R.id.txt_reserve);
        TextView returnText = findViewById(R.id.txt_return);
        reserveText.setOnClickListener(this);
        returnText.setOnClickListener(this);
    }

    // Build reservation recycler view

    private void buildReservationRecycler() {

        RecyclerView reservationRecycler = findViewById(R.id.recycler_reservation);
        reservationRecycler.setHasFixedSize(true);
        reservationRecycler.setLayoutManager(new LinearLayoutManager(this));

        mReservationList = SQLiteHelper.getInstance(this).getReservationsBy(mRoomNumber);
        mReservationAdapter = new ReservationAdapter(mReservationList);
        reservationRecycler.setAdapter(mReservationAdapter);
    }

    // Update reservation recycler view

    private void updateReservationRecycler() {

        mReservationList.clear();
        mReservationList.addAll(SQLiteHelper.getInstance(this).getReservationsBy(mRoomNumber));
        mReservationAdapter.notifyDataSetChanged();
    }

    // Process click events

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_reserve:
                // Show dialog that prompts user reservation info
                showReservationDialog();
                break;
            case R.id.txt_return:
                finish();
                break;
        }
    }

    // Show dialog that prompts user reservation info

    private void showReservationDialog() {

        // Inflate view that prompts user reservation info
        View addReservationView = View.inflate(this, R.layout.add_reservation_view, null);

        // Inner views
        EditText beginTimeEdit = addReservationView.findViewById(R.id.edit_begin_time);
        EditText endTimeEdit = addReservationView.findViewById(R.id.edit_end_time);
        ImageButton enterBeginTimeButton = addReservationView.findViewById(R.id.ibtn_enter_begin_time);
        ImageButton enterEndTimeButton = addReservationView.findViewById(R.id.ibtn_enter_end_time);
        TimePicker timePicker = addReservationView.findViewById(R.id.time_picker);
        Button priceButton = addReservationView.findViewById(R.id.btn_price);

        // Reservation times
        final LocalDateTime[] beginTime = new LocalDateTime[1];
        final LocalDateTime[] endTime = new LocalDateTime[1];

        // Set button listeners
        enterBeginTimeButton.setOnClickListener(v -> {

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            beginTime[0] = LocalDateTime.now().withHour(hour).withMinute(minute);
            if (endTime[0] != null) {
                Duration duration = Duration.between(beginTime[0], endTime[0]);
                int price = PaymentHelper.getPrice(duration);
                priceButton.setText(String.valueOf(price));
            }

            String strBeginTime = String.format(Locale.getDefault(),
                    "%s %02d:%02d",
                    hour <= 12 ? "AM" : "PM",
                    hour <= 12 ? hour : hour - 12,
                    minute);
            beginTimeEdit.setText(strBeginTime);
        });
        enterEndTimeButton.setOnClickListener(v -> {

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            endTime[0] = LocalDateTime.now().withHour(hour).withMinute(minute);
            if (beginTime[0] != null) {
                Duration duration = Duration.between(beginTime[0], endTime[0]);
                int price = PaymentHelper.getPrice(duration);
                priceButton.setText(String.valueOf(price));
            }

            String strEndTime = String.format(Locale.getDefault(),
                    "%s %02d:%02d",
                    hour <= 12 ? "AM" : "PM",
                    hour <= 12 ? hour : hour - 12,
                    minute);
            endTimeEdit.setText(strEndTime);
        });

        // Build and show dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.reservation)
                .setView(addReservationView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    // Failure: Empty time values
                    if (beginTime[0] == null || endTime[0] == null) {
                        Toast.makeText(this, "Enter all values", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Failure: Reversed time
                    if (beginTime[0].isAfter(endTime[0])) {
                        Toast.makeText(this, "Enter valid times", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get duration
                    Duration duration = Duration.between(beginTime[0], endTime[0]);
                    
                    // Failure: Too short duration
                    if (duration.getSeconds() < 30 * 60) {
                        Toast.makeText(this, "Must be longer than 30 minutes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Failure: Too long duration
                    if (duration.getSeconds() > 180 * 60) {
                        Toast.makeText(this, "Must not be longer than 3 hours", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Check if the reservation conflicts with any of existing reservation
                    for (Reservation reservation : mReservationList) {

                        // Failure: Conflicts occurred
                        if (reservation.conflictsWith(beginTime[0]) || reservation.conflictsWith(endTime[0])) {
                            Toast.makeText(this, "There is conflicts!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (mUserId != null) {

                        // Payment For member: by coin

                        // Check if user has enough coins
                        int price = PaymentHelper.getPrice(duration);
                        User user = SQLiteHelper.getInstance(this).getUser(mUserId);

                        // Failure: Not enough coins
                        if (user.getBalance() < price) {
                            Toast.makeText(this,
                                    "You don't have enough coins", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Make reservation using coins
                        Reservation reservation = new Reservation(mRoomNumber, beginTime[0], endTime[0], mUserId);
                        SQLiteHelper.getInstance(this).addReservation(reservation);
                        SQLiteHelper.getInstance(this).updateUserBalance(mUserId, user.getBalance() - price);

                        updateReservationRecycler();

                        Toast.makeText(this,
                                "Reservation succeeded!", Toast.LENGTH_SHORT).show();

                    } else {

                        // Payment For non-member: Show payment dialog
                        showPaymentDialog(beginTime[0], endTime[0]);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showPaymentDialog(LocalDateTime beginTime, LocalDateTime endTime) {

        View payView = View.inflate(this, R.layout.pay_cash_view, null);
        EditText phoneEdit = payView.findViewById(R.id.edit_phone);
        ImageView visaImage = payView.findViewById(R.id.img_visa);
        ImageView paypalImage = payView.findViewById(R.id.img_paypal);
        ImageView amazonImage = payView.findViewById(R.id.img_amazon);
        int[] payWith = new int[]{-1};

        visaImage.setOnClickListener(v -> {
            visaImage.setAlpha(0.7f);
            paypalImage.setAlpha(1f);
            amazonImage.setAlpha(1f);
            payWith[0] = 0;
        });
        paypalImage.setOnClickListener(v -> {
            paypalImage.setAlpha(0.7f);
            visaImage.setAlpha(1f);
            amazonImage.setAlpha(1f);
            payWith[0] = 1;
        });
        amazonImage.setOnClickListener(v -> {
            amazonImage.setAlpha(0.7f);
            paypalImage.setAlpha(1f);
            visaImage.setAlpha(1f);
            payWith[0] = 2;
        });

        new AlertDialog.Builder(this)
                .setTitle("Pay now")
                .setView(payView)
                .setPositiveButton("Pay now", (dialog, which) -> {

                    // Get phone number
                    String strPhone = phoneEdit.getText().toString().trim();

                    // Failure: Phone number not specified
                    if (strPhone.isEmpty()) {
                        Toast.makeText(this,
                                "Please specify phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Failure: payment method not specified
                    if (payWith[0] == -1) {
                        Toast.makeText(this,
                                "Select payment method", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Consist non-member id with '#' and phone number
                    String nonMemberId = String.format(Locale.getDefault(), "#%s", strPhone);

                    // Make reservation using given information
                    Reservation reservation = new Reservation(
                            mRoomNumber, beginTime, endTime, nonMemberId
                    );
                    SQLiteHelper.getInstance(this).addReservation(reservation);

                    updateReservationRecycler();

                    Toast.makeText(this,
                            "Reservation succeeded!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

}