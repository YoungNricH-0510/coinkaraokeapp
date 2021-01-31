package com.holy.coinkaraoke;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.User;


public class SignUpFragment extends Fragment implements View.OnClickListener {

    // Edit texts for signing up
    private EditText mIdEdit;
    private EditText mPasswordEdit;
    private EditText mNameEdit;
    private EditText mPhoneEdit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Initialize edit texts
        mIdEdit = root.findViewById(R.id.edit_id);
        mPasswordEdit = root.findViewById(R.id.edit_password);
        mNameEdit = root.findViewById(R.id.edit_name);
        mPhoneEdit = root.findViewById(R.id.edit_phone);

        // Set click listeners to views
        TextView yesText = root.findViewById(R.id.txt_reserve);
        TextView noText = root.findViewById(R.id.txt_return);
        yesText.setOnClickListener(this);
        noText.setOnClickListener(this);

        return root;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        if (getFragmentManager() == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.txt_reserve:
                // Try signing up
                if (trySigningUp()) {
                    // If succeeded, return to intro fragment
                    getFragmentManager().popBackStack();
                }
                break;
            case R.id.txt_return:
                // Return to intro fragment
                getFragmentManager().popBackStack();
                break;
        }
    }

    private boolean trySigningUp() {

        // Get signing-up information
        String strId = mIdEdit.getText().toString().trim();
        String strPassword = mPasswordEdit.getText().toString().trim();
        String strName = mNameEdit.getText().toString().trim();
        String strPhone = mPhoneEdit.getText().toString().trim();

        // Validate inputs
        if (strId.length() < 4) {
            Toast.makeText(getContext(),
                    "ID must be 4 chars or longer", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strPassword.length() < 4) {
            Toast.makeText(getContext(),
                    "Password must be 4 chars or longer", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strName.length() < 2) {
            Toast.makeText(getContext(),
                    "Name must be 2 chars or longer", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (strPhone.length() != 11) {
            Toast.makeText(getContext(),
                    "Phone number must be 11 chars", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Process registration using SQLite DB

        // 1. Check if there is an existing user
        User existingUser = SQLiteHelper.getInstance(getContext()).getUser(strId);
        if (existingUser != null) {
            Toast.makeText(getContext(), "ID already exists", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. Insert user to DB
        User user = new User(strId, strPassword, strName, 0, strPhone, null);
        SQLiteHelper.getInstance(getContext()).addUser(user);

        // Show success message
        Toast.makeText(getContext(), "Registered successfully!", Toast.LENGTH_SHORT).show();

        return true;
    }

}


