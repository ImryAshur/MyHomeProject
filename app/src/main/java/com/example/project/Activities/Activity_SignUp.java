package com.example.project.Activities;
/*
Developer - Imry Ashur
*/
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.project.CallBacks.GetDataListener;
import com.example.project.Objects.Family;
import com.example.project.Objects.User;
import com.example.project.Others.MySignalV2;
import com.example.project.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class Activity_SignUp extends AppCompatActivity {

    private TextInputLayout signUp_EDT_familyNickname;
    private TextInputLayout signUp_EDT_familyName;
    private TextInputLayout signUp_EDT_userName;
    private TextInputLayout signUp_EDT_phoneNumber;
    private TextInputLayout signUp_EDT_password;

    private TextInputLayout newHome_EDT_familyName;
    private TextInputLayout newHome_EDT_homeName;
    private TextInputLayout newHome_EDT_phoneNumber;

    private ProgressDialog progressDialog;
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
        signUp_EDT_familyNickname = findViewById(R.id.signUp_EDT_familyNickname);
        signUp_EDT_familyName = findViewById(R.id.signUp_EDT_familyName);
        signUp_EDT_userName = findViewById(R.id.signUp_EDT_userName);
        signUp_EDT_phoneNumber = findViewById(R.id.signUp_EDT_phoneNumber);
        signUp_EDT_password = findViewById(R.id.signUp_EDT_password);
        signUp_BTN_continue = findViewById(R.id.signUp_BTN_continue);
        signUp_BTN_logIn = findViewById(R.id.signUp_BTN_logIn);
        signUp_BTN_newHome = findViewById(R.id.signUp_BTN_newHome);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    private void dismissDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
            if (isNetworkConnected()) openDialog();
            else MySignalV2.getInstance().showToast(getString(R.string.network));
        }
    };

    private View.OnClickListener newHomeGoBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final String userInputNickname = newHome_EDT_homeName.getEditText().getText().toString().trim();
            final String managerPhone = newHome_EDT_phoneNumber.getEditText().getText().toString().trim();
            final String familyName = newHome_EDT_familyName.getEditText().getText().toString().trim();

            phoneinput = makeError(view,newHome_EDT_phoneNumber,getString(R.string.phoneNumber));
            nicknameinput = makeError(view,newHome_EDT_homeName,getString(R.string.nickname));
            familyinput = makeError(view,newHome_EDT_familyName,getString(R.string.familyName));
            if(phoneinput && nicknameinput && familyinput) {
                readData(FirebaseDatabase.getInstance().getReference(getString(R.string.families)), new GetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(userInputNickname)) {
                            newHome_EDT_homeName.setError("Nickname Already Exists");
                        } else {
                            addNewFamilyToDB(userInputNickname,familyName, managerPhone);
                            dialog.dismiss();
                            fillEditText(familyName, managerPhone, userInputNickname);
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

    private void fillEditText(String familyName, String managerPhone, String userInputNickname) {
        signUp_EDT_familyName.getEditText().setText(familyName);
        signUp_EDT_phoneNumber.getEditText().setText(managerPhone);
        signUp_EDT_familyNickname.getEditText().setText(userInputNickname);
    }


    private void addNewFamilyToDB(String key,String familyName,String managerPhone) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.families));
        Family family = new Family(key,familyName,managerPhone);
        myRef.child(family.getKey()).setValue(family);
    }

    private View.OnClickListener haveAccountBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private View.OnClickListener continueBTN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showProgressDialog();
            boolean familyNickname;
            final String userInputNickname = signUp_EDT_familyNickname.getEditText().getText().toString().trim();
            familyNickname = makeError(view,signUp_EDT_familyNickname,getString(R.string.nickname));
            familyinput = makeError(view,signUp_EDT_familyName,getString(R.string.familyName));
            userinput = makeError(view,signUp_EDT_userName,getString(R.string.userName));
            phoneinput = makeError(view,signUp_EDT_phoneNumber,getString(R.string.phoneNumber));
            passwordinput = makeError(view,signUp_EDT_password,getString(R.string.password));
            if(familyinput && userinput && phoneinput && passwordinput && familyNickname){
                if (isNetworkConnected()){
                    checkIfFamilyNameInDB(userInputNickname);
                }else MySignalV2.getInstance().showToast(getString(R.string.network));

            }else dismissDialog();
        }
    };

    private void checkIfFamilyNameInDB(final String userInputNickname) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.families));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userInputNickname)) {
                    User user = createNewUserAndStoreAtDB();
                    reference.child(user.getKey()).child("familyMembers").child(user.getPhone()).setValue(user);
                    dismissDialog();
                    finish();
                } else {
                    dismissDialog();
                    signUp_EDT_familyNickname.setError("There's no such family nickname");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private User createNewUserAndStoreAtDB() {

        String key = signUp_EDT_familyNickname.getEditText().getText().toString().trim();
        String familyName = signUp_EDT_familyName.getEditText().getText().toString().trim();
        String userName = signUp_EDT_userName.getEditText().getText().toString().trim();
        String phoneNumber = signUp_EDT_phoneNumber.getEditText().getText().toString().trim();
        String password = signUp_EDT_password.getEditText().getText().toString().trim();
        User user = new User(key,familyName,userName,phoneNumber,password);

        //Store at DB
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.users));
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
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
