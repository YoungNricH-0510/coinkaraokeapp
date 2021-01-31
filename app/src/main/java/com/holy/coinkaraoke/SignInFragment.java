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


public class SignInFragment extends Fragment implements View.OnClickListener {

    // Edit texts for signing in
    private EditText mIdEdit;
    private EditText mPasswordEdit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Initialize edit texts
        mIdEdit = root.findViewById(R.id.edit_id);
        mPasswordEdit = root.findViewById(R.id.edit_password);

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

        switch (v.getId()) {
            case R.id.txt_reserve:
                // Try signing in
                if (trySigningIn()) {
                    // Return to intro fragment
                    getFragmentManager().popBackStack();
                }
                break;
            case R.id.txt_return:
                // Return to intro fragment
                getFragmentManager().popBackStack();
                break;
        }
    }

    private boolean trySigningIn() {

        // Get signing-in information
        String strId = mIdEdit.getText().toString().trim();
        String strPassword = mPasswordEdit.getText().toString().trim();

        // No empty field allowed
        if (strId.isEmpty() || strPassword.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please enter all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Process signing in using SQLite Database

        // 1. Check if there is an corresponding id
        User user = SQLiteHelper.getInstance(getContext()).getUser(strId);
        if (user == null) {
            Toast.makeText(getContext(), "ID does not exist", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. Check if the password entered matches with the real one
        if (!strPassword.equals(user.getPassword())) {
            Toast.makeText(getContext(), "Password does not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. Set current id of app
        ((App)getActivity().getApplication()).setCurrentId(strId);

        // Show message
        // Toast.makeText(getContext(), "Signed in successfully", Toast.LENGTH_SHORT).show();

        return true;
    }


}