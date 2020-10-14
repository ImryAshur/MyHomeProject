package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Activity_LogIn extends AppCompatActivity {

    private TextInputLayout logIn_EDT_phoneNumber;
    private TextInputLayout logIn_EDT_password;
    private Button logIn_BTN_loginbutton;
    private Button logIn_BTN_signUpButton;
    private boolean phoneinput = false,passwordinput = false;

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
        }
    };

    private View.OnClickListener loginBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String userInputPhoneNumber = logIn_EDT_phoneNumber.getEditText().getText().toString().trim();
            final String userInputPassword = logIn_EDT_password.getEditText().getText().toString().trim();

            phoneinput = makeError(view, logIn_EDT_phoneNumber, "Phone Number");
            passwordinput = makeError(view, logIn_EDT_password, "Password");

            if(phoneinput && passwordinput){
                readData(FirebaseDatabase.getInstance().getReference("users"), new GetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userInputPhoneNumber)) {
                            User user = dataSnapshot.child(userInputPhoneNumber).getValue(User.class);
                            if (user.getPassword().equals(userInputPassword)){
                                Gson gson = new Gson();
                                String parseUser = gson.toJson(user);
                                Intent intent = new Intent(Activity_LogIn.this,Activity_Main.class);
                                intent.putExtra(Activity_Main.EXTRA_KEY_USER,parseUser);
                                startActivity(intent);
                                finish();
                            }else{
                                logIn_EDT_password.setError("Wrong Password, Try again");
                            }
                        } else {
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
        }
    };


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

    private boolean makeError(View view,TextInputLayout inputLayout,String label) {
        Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                Snackbar.LENGTH_LONG);
        if (inputLayout.getEditText().length() == 0) {
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
            snackbar.show();
            inputLayout.setError(label + " should not be empty");
            return false;

        }else{
            snackbar.dismiss();
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
            return true;
        }
    }
}