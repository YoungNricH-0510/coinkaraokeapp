package com.holy.coinkaraoke;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class IntroFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_HOME = 100;

    private String mUserId;         // user id only for member
    private String mNonMemberId;    // non member id (based on phone number)

    private TextView mReservationText;
    private TextView mOccupiedText;
    private TextView mEmptyText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() == null) {
            return null;
        }

        // Inflate the layout for this fragment
        mUserId = ((App)getActivity().getApplication()).getCurrentId();
        View root = inflater.inflate(
                (mUserId == null) ? R.layout.fragment_intro : R.layout.fragment_intro_member,
                container, false);

        mReservationText = root.findViewById(R.id.txt_reservation);
        mOccupiedText = root.findViewById(R.id.txt_occupied);
        mEmptyText = root.findViewById(R.id.txt_empty);

        if (mUserId != null) {
            // Display current id
            TextView reservationTitleText = root.findViewById(R.id.txt_reservation_title);
            reservationTitleText.setText(
                    String.format(Locale.getDefault(), "%s's\nReservation", mUserId)
            );

            // Set click listeners to views
            ImageButton signOutButton = root.findViewById(R.id.ibtn_sign_out);
            signOutButton.setOnClickListener(this);
        }

        // Set click listeners to views
        if (mUserId == null) {
            TextView signUpText = root.findViewById(R.id.txt_sign_up);
            TextView signInText = root.findViewById(R.id.txt_sign_in);
            ImageButton checkButton = root.findViewById(R.id.ibtn_check_reservation);
            signUpText.setOnClickListener(this);
            signInText.setOnClickListener(this);
            checkButton.setOnClickListener(this);
        }

        TextView yesText = root.findViewById(R.id.txt_yes);
        TextView noText = root.findViewById(R.id.txt_no);
        yesText.setOnClickListener(this);
        noText.setOnClickListener(this);

        // Display user's reservation
        displayReservationRoomNumber();

        // Display room status
        displayRoomStatus();

        return root;
    }

    // Get user's reservation room number

    private int getReservationRoomNumber() {

        String id = (mUserId != null ? mUserId : mNonMemberId);

        if (id != null) {
            List<Reservation> reservationList = SQLiteHelper
                    .getInstance(getContext())
                    .getReservationsBy(id);

            if (!reservationList.isEmpty()) {
                Reservation reservation = reservationList.get(0);
                return reservation.getRoomNumber();
            } else {
                mReservationText.setText(R.string.no_reservation);
            }
        } else {
            mReservationText.setText(R.string.no_reservation);
        }
        return -1;
    }

    // Display reservation room number

    private void displayReservationRoomNumber() {

        int roomNumber = getReservationRoomNumber();

        if (roomNumber == -1) {
            mReservationText.setText(R.string.no_reservation);
            return;
        }

        String strReservation = String.format(Locale.getDefault(), "No. %02d", roomNumber);
        mReservationText.setText(strReservation);
    }

    // Display current room status (number of occupied or empty rooms)

    private void displayRoomStatus() {

        int occupiedNum = 0;

        // Get current time
        LocalDateTime now = LocalDateTime.now();

        for (int i = 1; i <= 19; i++) {

            // Get reservation list of each room
            List<Reservation> reservationList = SQLiteHelper
                    .getInstance(getContext())
                    .getReservationsBy(i);

            // Check if the room is currently occupied or not
            for (Reservation reservation : reservationList) {
                if (reservation.conflictsWith(now)) {
                    occupiedNum++;
                    break;
                }
            }
        }

        mOccupiedText.setText(String.valueOf(occupiedNum));
        mEmptyText.setText(String.valueOf(19 - occupiedNum));
    }

    // Process view click

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_sign_up:
                // Show sign up fragment
                showSignUpFragment();
                break;
            case R.id.txt_sign_in:
                // Show sign in fragment
                showSignInFragment();
                break;
            case R.id.ibtn_check_reservation:
                showCheckReservationDialog();
                break;
            case R.id.ibtn_sign_out:
                askWhetherToSignOut();
                break;
            case R.id.txt_yes:
                // Start home activity
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivityForResult(intent, REQUEST_HOME);
                break;
            case R.id.txt_no:
                // Show dialog that asks whether to finish the app
                askWhetherToFinish();
                break;
        }
    }

    // Show sign up fragment

    private void showSignUpFragment() {

        // Show Sign Up fragment
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, new SignUpFragment())
                    .addToBackStack("SignUp")
                    .commit();
        }
    }

    // Show sign in fragment

    private void showSignInFragment() {

        if (getFragmentManager() != null) {
            // Show Sign In fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, new SignInFragment())
                    .addToBackStack("SignIn")
                    .commit();
        }
    }

    // Show dialog for checking reservation

    private void showCheckReservationDialog() {

        // Inflate custom view for input
        View checkReservationView = View.inflate(
                getContext(), R.layout.check_reservation_view, null);

        EditText phoneEdit = checkReservationView.findViewById(R.id.edit_phone);

        // Build and show dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Check Reservation")
                .setView(checkReservationView)
                .setPositiveButton(R.string.check_now, (dialog, which) -> {

                    // Get phone number input
                    String strPhone = phoneEdit.getText().toString().trim();

                    // Failure: Phone number not specified
                    if (strPhone.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Please enter phone number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Consist non-member id with the phone number
                    mNonMemberId = String.format(Locale.getDefault(), "#%s", strPhone);

                    // Display reservation
                    displayReservationRoomNumber();

                    // If no reservation found, show toast
                    if (getReservationRoomNumber() == -1) {
                        Toast.makeText(getContext(),
                                "No reservation found", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // Show dialog that asks whether to finish the app

    private void askWhetherToFinish() {

        new AlertDialog.Builder(getContext())
                .setTitle("Finish")
                .setMessage("Would you finish the app?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Update views for possible changes
        if (requestCode == REQUEST_HOME) {

            displayReservationRoomNumber();
            displayRoomStatus();
        }
    }

    // Show dialog that asks whether to sign out

    private void askWhetherToSignOut() {

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.sign_out)
                .setMessage("Do you really want to sign out?")
                .setPositiveButton(R.string.sign_out, (dialog, which) -> {
                    // Sign out
                    ((App)getActivity().getApplication()).setCurrentId(null);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, new IntroFragment())
                            .commit();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}