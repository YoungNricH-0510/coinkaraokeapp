package com.holy.coinkaraoke;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.holy.coinkaraoke.helpers.PaymentHelper;
import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class CoinFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CONTACT = 100;
    public static final int REQUEST_PERMISSION_CONTACT = 100;

    private String mUserId;
    private EditText mSendAmountEdit;

    // Intent Integrator for QR code scanning
    private IntentIntegrator mIntentIntegrator;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_coin, container, false);

        mSendAmountEdit = root.findViewById(R.id.edit_send_amount);

        // Set button listeners
        Button chargeButton = root.findViewById(R.id.btn_charge_coin);
        Button sendButton = root.findViewById(R.id.btn_send_coin);
        Button scanButton = root.findViewById(R.id.btn_scan_qr);
        chargeButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);

        // Get user id
        mUserId = ((App) getActivity().getApplication()).getCurrentId();

        // Display current coin
        displayCoinAndHours(root);

        // Create IntentIntegrator for QR code scanning.
        mIntentIntegrator = IntentIntegrator.forSupportFragment(this);
        mIntentIntegrator.setBeepEnabled(false);

        return root;
    }

    public void updateViews() {

        if (getView() != null) {
            displayCoinAndHours(getView());
        }
    }

    private void displayCoinAndHours(View root) {

        TextView coinText = root.findViewById(R.id.txt_coin);
        TextView hoursText = root.findViewById(R.id.txt_coin_hours);

        // Get current coin
        User user = SQLiteHelper.getInstance(getContext()).getUser(mUserId);
        int coin = user.getBalance();
        coinText.setText(String.valueOf(coin));

        // Calculate hours available
        StringBuilder strHours = new StringBuilder();
        int hours = PaymentHelper.getAvailableHours(coin);
        int minutes = PaymentHelper.getAvailableMinutes(coin);

        if (hours == 1) {
            strHours.append(hours);
            strHours.append(" hour ");
        } else {
            strHours.append(hours);
            strHours.append(" hours ");
        }
        if (minutes == 30) {
            strHours.append(minutes);
            strHours.append(" minutes ");
        }
        strHours.append("available");

        hoursText.setText(strHours);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_charge_coin:
                showChargeDialog();
                break;
            case R.id.btn_send_coin:
                trySendingCoin();
                break;
            case R.id.btn_scan_qr:
                scanQRCode();
                break;
        }
    }

    // Show dialog that prompts user payment data

    private void showChargeDialog() {

        View chargeView = View.inflate(getContext(), R.layout.charge_view, null);
        EditText amountEdit = chargeView.findViewById(R.id.edit_amount);
        ImageView visaImage = chargeView.findViewById(R.id.img_visa);
        ImageView paypalImage = chargeView.findViewById(R.id.img_paypal);
        ImageView amazonImage = chargeView.findViewById(R.id.img_amazon);
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

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.charge_now)
                .setView(chargeView)
                .setPositiveButton(R.string.charge_now, (dialog, which) -> {

                    String strAmount = amountEdit.getText().toString().trim();

                    // Failure: Amount not specified
                    if (strAmount.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Enter amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Failure: payment not specified
                    if (payWith[0] == -1) {
                        Toast.makeText(getContext(),
                                "Select payment method", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get amount
                    int amount = Integer.parseInt(strAmount);

                    // Failure: Amount is too much or little
                    if (amount < 1000) {
                        Toast.makeText(getContext(),
                                "Amount must be at least 1000", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (amount > 100000) {
                        Toast.makeText(getContext(),
                                "Amount must be at most 100000", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Process payment: update user balance
                    User user = SQLiteHelper.getInstance(getContext()).getUser(mUserId);
                    SQLiteHelper.getInstance(getContext())
                            .updateUserBalance(mUserId, user.getBalance() + amount);

                    // Display current coin
                    if (getView() != null) {
                        displayCoinAndHours(getView());
                    }

                    Toast.makeText(getContext(),
                            "Payment succeeded", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // Process sending coin

    private void trySendingCoin() {

        // Get amount of coin to send
        String strAmount = mSendAmountEdit.getText().toString().trim();

        // Failure: Amount not specified
        if (strAmount.isEmpty()) {
            Toast.makeText(getContext(),
                    "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(strAmount);

        // Failure: Amount is under 1000
        if (amount < 1000) {
            Toast.makeText(getContext(),
                    "Amount must be at least 1000", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current coin balance
        User user = SQLiteHelper.getInstance(getContext()).getUser(mUserId);
        int coin = user.getBalance();

        // Failure: Not enough coin
        if (coin < amount) {
            Toast.makeText(getContext(),
                    "You don't have enough coin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start contact app to prompt user phone number of recipient
        if (getContext() != null
                && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request contact permission
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSION_CONTACT);
        } else {
            // Start contact activity
            startContactActivity();
        }
    }

    // Start contact activity

    private void startContactActivity() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Contact activity finished
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK
                && data != null && getContext() != null) {

            // Get selected contact id from given URI
            String dataUri = data.getDataString();
            String contactId;

            try (Cursor cursor = getContext().getContentResolver().query(
                    Uri.parse(dataUri),
                    null,
                    null,
                    null,
                    null)) {
                cursor.moveToFirst();
                contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            }

            // Get phone number of selected contact through the id
            String phoneNumber = null;

            try (Cursor phoneCursor = getContext().getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID
                            + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                    new String[]{contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE},
                    null)) {
                if (phoneCursor.moveToFirst()) {
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.DATA));
                }
            }

            // Failure: Couldn't get phone number
            if (phoneNumber == null) {
                Toast.makeText(getContext(),
                        "Failed to get phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the phone number is registered
            User recipient = SQLiteHelper.getInstance(getContext()).getUserBy(phoneNumber);

            // Failure: Not registered
            if (recipient == null) {
                Toast.makeText(getContext(),
                        "Please select registered user", Toast.LENGTH_SHORT).show();
                return;
            }

            // Failure: Cannot send to oneself
            if (recipient.getId().equals(mUserId)) {
                Toast.makeText(getContext(),
                        "Cannot send to yourself.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get amount of coin to send
            String strAmount = mSendAmountEdit.getText().toString().trim();
            int amount = Integer.parseInt(strAmount);

            // Send coin from user's account to recipient's
            SQLiteHelper.getInstance(getContext())
                    .transferUserBalance(mUserId, recipient.getId(), amount);

            // Display current coin
            if (getView() != null) {
                displayCoinAndHours(getView());
            }

            Toast.makeText(getContext(),
                    "Coin has been sent successfully", Toast.LENGTH_SHORT).show();
        } else {

            // Parse QR code result.
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {
                if (result.getContents() == null) {
                    // NO QR code data.
                    Toast.makeText(getContext(), "Scanning cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Convert QR code data into JSON
                        JSONObject obj = new JSONObject(result.getContents());

                        // Get package name in QR data
                        String packageName = obj.getString("packageName");
                        if (packageName == null || !packageName.equals("com.holy.coinKaraoke")) {
                            Toast.makeText(getContext(), "No coin scanned", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get amount of coins in QR data
                        int coins = obj.getInt("coins");

                        // Increase user balance
                        User user = SQLiteHelper.getInstance(getContext()).getUser(mUserId);
                        SQLiteHelper.getInstance(getContext()).updateUserBalance(mUserId,
                                user.getBalance() + coins);

                        // Display current coin
                        if (getView() != null) {
                            displayCoinAndHours(getView());
                        }

                        String strMessage = String.format(Locale.getDefault(),
                                "%d coins charged!", coins);
                        Toast.makeText(getContext(), strMessage, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "No coin scanned", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CONTACT && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContactActivity();
            } else {
                Toast.makeText(getContext(), "Cannot open contact", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Scan QR code to charge coin

    private void scanQRCode() {

        // Start scanning.
        mIntentIntegrator.setPrompt("Scanning...");
        mIntentIntegrator.initiateScan();
    }

}