package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
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

import java.util.HashMap;

public class Activity_SignUp extends AppCompatActivity {

    private TextInputLayout signUp_EDT_familyName;
    private TextInputLayout signUp_EDT_userName;
    private TextInputLayout signUp_EDT_phoneNumber;
    private TextInputLayout signUp_EDT_password;

    private TextInputLayout newHome_EDT_familyName;
    private TextInputLayout newHome_EDT_homeName;
    private TextInputLayout newHome_EDT_phoneNumber;

    private HashMap<String,Object> userHashMapDB = new HashMap<>();
    private Button signUp_BTN_continue;
    private Button signUp_BTN_logIn;
    private Button signUp_BTN_newHome;
    private Button newHome_BTN_go;
    private boolean familyinput = false,userinput = false,phoneinput = false,passwordinput = false,nicknameinput = false;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findViews();
        signUp_BTN_continue.setOnClickListener(continueBTN);
        signUp_BTN_logIn.setOnClickListener(haveAccountBTN);
        signUp_BTN_newHome.setOnClickListener(createNewHomeBTN);
    }

    private void findViews() {
        signUp_EDT_familyName = findViewById(R.id.signUp_EDT_familyName);
        signUp_EDT_userName = findViewById(R.id.signUp_EDT_userName);
        signUp_EDT_phoneNumber = findViewById(R.id.signUp_EDT_phoneNumber);
        signUp_EDT_password = findViewById(R.id.signUp_EDT_password);
        signUp_BTN_continue = findViewById(R.id.signUp_BTN_continue);
        signUp_BTN_logIn = findViewById(R.id.signUp_BTN_logIn);
        signUp_BTN_newHome = findViewById(R.id.signUp_BTN_newHome);
    }


    public void openDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_newhome);
        initDialog();
        newHome_BTN_go.setOnClickListener(newHomeGoBTN);
        dialog.show();
    }

    private void initDialog() {
        newHome_EDT_familyName = dialog.findViewById(R.id.newHome_EDT_familyName);
        newHome_EDT_homeName = dialog.findViewById(R.id.newHome_EDT_homeName);
        newHome_BTN_go = dialog.findViewById(R.id.newHome_BTN_go);
        newHome_EDT_phoneNumber = dialog.findViewById(R.id.newHome_EDT_phoneNumber);
    }

    private View.OnClickListener createNewHomeBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openDialog();
        }
    };

    private View.OnClickListener newHomeGoBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String userInputNickname = newHome_EDT_homeName.getEditText().getText().toString().trim();
            final String managerPhone = newHome_EDT_phoneNumber.getEditText().getText().toString().trim();
            phoneinput = makeError(view,newHome_EDT_phoneNumber,"Phone number");
            nicknameinput = makeError(view,newHome_EDT_homeName,"Home nickname");
            familyinput = makeError(view,newHome_EDT_familyName,"Family name");
            if(phoneinput && nicknameinput && familyinput) {
                readData(FirebaseDatabase.getInstance().getReference("families"), new GetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userInputNickname)) {
                            newHome_EDT_homeName.setError("Nickname Already Exists");
                        } else {
                            addNewFamilyToDB(userInputNickname, managerPhone);
                            dialog.dismiss();
                            signUp_EDT_familyName.getEditText().setText(userInputNickname);
                            signUp_EDT_phoneNumber.getEditText().setText(managerPhone);
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


//    private void checkUniqeFamilyNickname(final String input) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("families");
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.hasChild(input)){
//                    Log.d("pttt", "here");
//                    Log.d("pttt", "" + snapshot.hasChild(input));
//                    //familyCheck = false;
//                    newHome_EDT_homeName.setError("Nickname Already Exists");
//                }else{
//                    //familyCheck = true;
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void addNewFamilyToDB(String familyNickname,String managerPhone) {
        Log.d("pttt", "addNewFamilyToDB: " + managerPhone);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("families/");
        Family family = new Family(familyNickname,managerPhone);
        myRef.child(family.getNickname()).setValue(family);
    }

    private View.OnClickListener haveAccountBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Activity_SignUp.this,Activity_LogIn.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener continueBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String userInputNickname = signUp_EDT_familyName.getEditText().getText().toString().trim();
            familyinput = makeError(view,signUp_EDT_familyName,"Family name");
            userinput = makeError(view,signUp_EDT_userName,"User name");
            phoneinput = makeError(view,signUp_EDT_phoneNumber,"Phone number");
            passwordinput = makeError(view,signUp_EDT_password,"Password");
            if(familyinput && userinput && phoneinput && passwordinput){
                readData(FirebaseDatabase.getInstance().getReference("families"), new GetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userInputNickname)) {
                            Family family = dataSnapshot.child(userInputNickname).getValue(Family.class);
                            Log.d("pttt",   "" + family);
                            User user = createNewUserAndStoreAtDB();
                            family.getFamilyMembers().add(user);
                            Log.d("pttt",   "" + family.toString());
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("families/");
                            myRef.child(family.getNickname()).setValue(family);
                            finish();
                        } else {
                            signUp_EDT_familyName.setError("There's no such family nickname");
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

    private User createNewUserAndStoreAtDB() {
        String familyName = signUp_EDT_familyName.getEditText().getText().toString().trim();
        String userName = signUp_EDT_userName.getEditText().getText().toString().trim();
        String phoneNumber = signUp_EDT_phoneNumber.getEditText().getText().toString().trim();
        String password = signUp_EDT_password.getEditText().getText().toString().trim();
        User user = new User(familyName,userName,phoneNumber,password);

        //Store at DB
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
        userHashMapDB.put(phoneNumber,user);
        myRef.updateChildren(userHashMapDB);
        return user;
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

//        Query checkUserInput = reference.orderByChild("nickname").equalTo(input);
//        Log.d("pttt", " " +checkUserInput.getRef());
//        Log.d("pttt", " " +checkUserInput);
//        if (checkUserInput.getRef().child("nickname").getKey().equals(input)){
//            Log.d("pttt", "here ");
//            newHome_EDT_homeName.setError("Family Nickname Already Exists");
//            correctinput = false;
//        }else correctinput = true;

//        checkUserInput.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    Log.d("pttt", "here ");
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });