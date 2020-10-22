package com.example.project.Activities;
/*
Developer - Imry Ashur
*/

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.example.project.CallBacks.GetDataListener;
import com.example.project.Objects.User;
import com.example.project.Others.MySignalV2;
import com.example.project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Activity_LogIn extends AppCompatActivity {

    private TextInputEditText logIn_EDT_phoneNumber;
    private TextInputEditText logIn_EDT_password;
    private MaterialButton logIn_BTN_loginbutton;
    private MaterialButton logIn_BTN_signUpButton;
    private boolean phoneinput = false, passwordinput = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        logIn_BTN_loginbutton.setOnClickListener(loginBTN);
        logIn_BTN_signUpButton.setOnClickListener(signUpBTN);
    }

    private void findViews() {
        logIn_EDT_phoneNumber = findViewById(R.id.logIn_EDT_phoneNumber);
        logIn_EDT_password = findViewById(R.id.logIn_EDT_password);
        logIn_BTN_loginbutton = findViewById(R.id.logIn_BTN_loginbutton);
        logIn_BTN_signUpButton = findViewById(R.id.logIn_BTN_signUpButton);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private View.OnClickListener signUpBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Activity_LogIn.this, Activity_SignUp.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener loginBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isNetworkConnected()) {

                final String userInputPhoneNumber = logIn_EDT_phoneNumber.getText().toString().trim();
                final String userInputPassword = logIn_EDT_password.getText().toString().trim();

                phoneinput = makeError(view, logIn_EDT_phoneNumber, getString(R.string.phoneNumber));
                passwordinput = makeError(view, logIn_EDT_password, getString(R.string.password));

                if (phoneinput && passwordinput) {
                    showProgressDialog();
                    readData(FirebaseDatabase.getInstance().getReference(getString(R.string.users)), new GetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            //Check if phone number located in DB
                            if (dataSnapshot.hasChild(userInputPhoneNumber)) {
                                User user = dataSnapshot.child(userInputPhoneNumber).getValue(User.class);

                                //Check if password is correct
                                if (user.getPassword().equals(userInputPassword)) {
                                    startApp(user);
                                } else {
                                    dismissDialog();
                                    logIn_EDT_password.setError("Wrong Password, Try again");
                                }
                            } else {
                                dismissDialog();
                                logIn_EDT_phoneNumber.setError("Wrong Phone Number, Try again");
                            }
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailure() {

                        }
                    });


                }
            } else MySignalV2.getInstance().showToast(getString(R.string.network));
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void startApp(User user) {
        Gson gson = new Gson();
        String parseUser = gson.toJson(user);
        Intent intent = new Intent(Activity_LogIn.this, Activity_Main.class);
        intent.putExtra(Activity_Main.EXTRA_KEY_USER, parseUser);
        dismissDialog();
        startActivity(intent);
        finish();
    }


    public void readData(DatabaseReference ref, final GetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure();
            }

        });

    }

    private boolean makeError(View view, TextInputEditText inputLayout, String label) {
        Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                Snackbar.LENGTH_LONG);
        if (inputLayout.length() == 0) {
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
            inputLayout.setError(label + " should not be empty");
            return false;

        } else {
            snackbar.dismiss();
            inputLayout.setError(null);
            return true;
        }
    }
}