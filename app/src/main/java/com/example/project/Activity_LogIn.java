package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class Activity_LogIn extends AppCompatActivity {

    private TextInputLayout logIn_EDT_phoneNumber;
    private TextInputLayout logIn_EDT_password;
    private Button logIn_BTN_loginbutton;
    private Button logIn_BTN_signUpButton;
    private boolean correctinput = true;

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

    private View.OnClickListener signUpBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Activity_LogIn.this, Activity_SignUp.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener loginBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            makeError(view, logIn_EDT_phoneNumber, "Phone Number");
            makeError(view, logIn_EDT_password, "Password");
        }
    };

    private void makeError(View view, TextInputLayout inputLayout, String label) {
        Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                Snackbar.LENGTH_LONG);
        if (inputLayout.getEditText().length() == 0) {
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
            inputLayout.setError(label + " should not be empty");
            correctinput = false;
        } else {
            correctinput = true;
            snackbar.dismiss();
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }
    }
}