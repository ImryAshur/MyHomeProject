package com.example.project.Activities;
/*
Developer - Imry Ashur
*/

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project.Others.MySharedPreferencesV4;
import com.example.project.Others.MySignalV2;
import com.example.project.Objects.User;
import com.example.project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_Settings extends AppCompatActivity {

    private String TAG = "pttt";
    private DrawerLayout settings_SPC_drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView settings_SPC_nav;
    private Toolbar settings_SPC_toolBar;
    private ArrayList<String> familyMembers;
    private User user;
    private HashMap<String, String> hashMapFamilyMembersNames;
    private TextView navHeader_LBL_userName;
    private MaterialTextView settings_LBL_details;
    private MaterialTextView settings_LBL_familyMembers;
    private MaterialTextView settings_LBL_addMember;
    private MaterialTextView settings_LBL_password;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        getUser();
        setSideMenu();
        getFamilyMembersFromSP();


        settings_LBL_details.setOnClickListener(detailsListener);
        settings_LBL_password.setOnClickListener(changePasswordsListener);
        settings_LBL_familyMembers.setOnClickListener(familyMembersListener);
        settings_LBL_addMember.setOnClickListener(addDeleteMemberListener);
        settings_SPC_nav.setNavigationItemSelectedListener(menuListener);
    }

    // upload family member to SP before closing the activity
    @Override
    protected void onStop() {
        super.onStop();
        String hashMapString = new Gson().toJson(hashMapFamilyMembersNames);
        MySharedPreferencesV4.getInstance().putString(Activity_Main.KEY_FAMILY_MEMBERS, hashMapString);
    }

    // getting family members from SP
    private ArrayList<String> getArrayListFromSP() {
        ArrayList<String> familyMembers = new ArrayList<>();
        String hashMapString = MySharedPreferencesV4.getInstance().getString(Activity_Main.KEY_FAMILY_MEMBERS, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        hashMapFamilyMembersNames = new Gson().fromJson(hashMapString, type);
        for (String key : hashMapFamilyMembersNames.values()) {
            familyMembers.add(key);
        }

        return familyMembers;
    }


    private void getFamilyMembersFromSP() {
        familyMembers = getArrayListFromSP();
        Log.d(TAG, "getFamilyMembersFromSP: " + familyMembers.size());
        if (familyMembers == null) familyMembers = new ArrayList<String>();
    }


    // getting user from previous activity
    private void getUser() {
        Intent intent = getIntent();
        String tempUser = intent.getStringExtra(Activity_Main.EXTRA_KEY_USER);
        if (tempUser.length() > 0) {
            user = new Gson().fromJson(tempUser, User.class);
            navHeader_LBL_userName.setText(user.getUserName());
        } else {
            MySignalV2.getInstance().showToast("Something went wrong please restart the App...");
        }

    }


    private void setSideMenu() {
        setSupportActionBar(settings_SPC_toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, settings_SPC_drawerLayout, settings_SPC_toolBar, R.string.open, R.string.close);
        settings_SPC_drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }


    //set side menu options
    private NavigationView.OnNavigationItemSelectedListener menuListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            switch (id) {
                case R.id.nav_LBL_calendar:
                    startNewActivity(Activity_Main.class, true);
                    break;
                case R.id.nav_LBL_notes:
                    startNewActivity(Activity_Notes.class, true);
                    break;
                case R.id.nav_LBL_logout:
                    startNewActivity(Activity_LogIn.class, false);
                    break;

                default:
                    return true;
            }
            return false;
        }

    };


    // start new activity and send the user inside the intent
    private void startNewActivity(Class newActivity, boolean parseUser) {
        Intent intent = new Intent(Activity_Settings.this, newActivity);
        if (parseUser) {
            String stringUser = new Gson().toJson(user);
            intent.putExtra(Activity_Main.EXTRA_KEY_USER, stringUser);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        settings_SPC_drawerLayout = findViewById(R.id.settings_SPC_drawerLayout);
        settings_SPC_toolBar = findViewById(R.id.settings_SPC_toolBar);
        settings_SPC_nav = findViewById(R.id.settings_SPC_nav);
        View headerView = settings_SPC_nav.getHeaderView(0);
        navHeader_LBL_userName = headerView.findViewById(R.id.navHeader_LBL_userName);

        settings_LBL_details = findViewById(R.id.settings_LBL_details);
        settings_LBL_familyMembers = findViewById(R.id.settings_LBL_familyMembers);
        settings_LBL_addMember = findViewById(R.id.settings_LBL_addMember);
        settings_LBL_password = findViewById(R.id.settings_LBL_password);
    }


    // set listeners
    private View.OnClickListener detailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openDialog(R.layout.dialog_user_details);
            initDialog();
        }
    };

    private View.OnClickListener familyMembersListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            displayFamilyMembers();

        }
    };

    private View.OnClickListener addDeleteMemberListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openDialog(R.layout.dialog_add_delete_member);
            MaterialButton addMember_BTN_addMember = dialog.findViewById(R.id.addMember_BTN_addMember);
            MaterialButton addMember_BTN_removeMember = dialog.findViewById(R.id.addMember_BTN_removeMember);
            addMember_BTN_addMember.setOnClickListener(addRemoveMember);
            addMember_BTN_removeMember.setOnClickListener(addRemoveMember);
        }
    };

    // get values from edit text and check if all the values are correct
    // if it's ok remove / add member
    private View.OnClickListener addRemoveMember = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean userName, phoneNum;
            TextInputEditText addMember_EDT_userName = dialog.findViewById(R.id.addMember_EDT_userName);
            TextInputEditText addMember_EDT_phoneNumber = dialog.findViewById(R.id.addMember_EDT_phoneNumber);
            String inputUserName = addMember_EDT_userName.getText().toString();
            String inputphoneNum = addMember_EDT_phoneNumber.getText().toString();
            userName = makeError(addMember_EDT_userName, getString(R.string.userName));
            phoneNum = makeError(addMember_EDT_phoneNumber, getString(R.string.phoneNumber));

            if (inputUserName.equals(user.getUserName())) {
                userName = false;
                addMember_EDT_userName.setError("YOU CAN'T REMOVE YOUR ACCOUNT!");
            }

            if (((String) view.getTag()).equals("a")) {
                addMemberToDB(userName, phoneNum, inputUserName, inputphoneNum);
            } else if (phoneNum && userName) {

                deleteMember(inputUserName, inputphoneNum, addMember_EDT_userName, addMember_EDT_phoneNumber);
            }
        }
    };

    // adding new member to DB
    private void addMemberToDB(boolean userName, boolean phoneNum, String inputUserName, String inputphoneNum) {
        boolean password;
        TextInputEditText addMember_EDT_password = dialog.findViewById(R.id.addMember_EDT_password);
        String inputpassword = addMember_EDT_password.getText().toString();
        password = makeError(addMember_EDT_password, getString(R.string.password));
        if (userName && phoneNum && password) {
            User myUser = new User(user.getKey(), user.getFamilyName(), inputUserName, inputphoneNum, inputpassword);
            addUserToDB(Activity_Main.DB_FAMILY_MEMBERS, myUser);
            addUserToDB("users", myUser);
            familyMembers.add(myUser.getUserName());
            hashMapFamilyMembersNames.put(myUser.getPhone(), myUser.getUserName());
            MySignalV2.getInstance().showToast("User Added!");
            dialog.dismiss();
        }
    }

    // delete member from DB
    private void deleteMember(String inputUserName, String inputphoneNum, TextInputEditText addMember_EDT_userName
            , TextInputEditText addMember_EDT_phoneNumber) {
        int index = checkIfInFamilyMembers(inputUserName);
        if (index != -1) {
            addMember_EDT_userName.setError(null);
            checkPhoneNumberInDB(inputphoneNum, addMember_EDT_phoneNumber, index, inputUserName);
        } else addMember_EDT_userName.setError("There Isn't Family Member In This Name...");
    }


    // checking if phone number located in DB
    private void checkPhoneNumberInDB(final String inputphoneNum, final TextInputEditText addMember_EDT_phoneNumber, final int index, final String inputUserName) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Activity_Main.DB_FAMILY_MEMBERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(inputphoneNum)) {
                    addMember_EDT_phoneNumber.setError(null);
                    User tempUser = snapshot.child(inputphoneNum).getValue(User.class);
                    Log.d(TAG, "onDataChange: " + tempUser.getUserName() + tempUser.getPhone());
                    removeFamilyMember(ref, inputphoneNum, index, inputUserName, tempUser, addMember_EDT_phoneNumber);
                } else {
                    addMember_EDT_phoneNumber.setError("There Isn't Phone Number In This Family...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // remove family member from arraylist,hashmap,DB
    private void removeFamilyMember(DatabaseReference ref, String inputphoneNum, int index, String inputUserName, User user, TextInputEditText addMember_EDT_phoneNumber) {
        if (user.getUserName().equals(inputUserName)) {
            addMember_EDT_phoneNumber.setError(null);
            ref.child(inputphoneNum).removeValue();
            familyMembers.remove(index);
            hashMapFamilyMembersNames.remove(inputphoneNum);
            deleteFromDB("users/", inputphoneNum);
            MySignalV2.getInstance().showToast("Family Member Removed!");
            dialog.dismiss();
        } else addMember_EDT_phoneNumber.setError("User Name And Phone Number Didn't Match!");
    }

    // checking if there is user name in this family
    private int checkIfInFamilyMembers(String inputUserName) {
        for (int i = 0; i < familyMembers.size(); i++) {
            Log.d(TAG, "onClick: " + familyMembers.get(i));
            if (familyMembers.get(i).equals(inputUserName)) {
                return i;
            }
        }
        return -1;
    }


    private void addUserToDB(String path, User value) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child(value.getPhone()).setValue(value);
    }

    private void deleteFromDB(String path, String phone) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child(phone).removeValue();
    }


    // set listeners
    private View.OnClickListener changePasswordsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openDialog(R.layout.dialog_change_password);
            MaterialButton changePassword_BTN_addMember = dialog.findViewById(R.id.changePassword_BTN_addMember);
            changePassword_BTN_addMember.setOnClickListener(changePasswordsBTNListener);
        }
    };

    private View.OnClickListener changePasswordsBTNListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean userPassword = false, samePassword = false;
            TextInputEditText changePassword_EDT_currentPassword = dialog.findViewById(R.id.changePassword_EDT_currentPassword);
            TextInputEditText changePassword_EDT_newPassword = dialog.findViewById(R.id.changePassword_EDT_newPassword);
            TextInputEditText changePassword_EDT_verifyPassword = dialog.findViewById(R.id.changePassword_EDT_verifyPassword);
            String currentPassword = changePassword_EDT_currentPassword.getText().toString();
            String newPassword = changePassword_EDT_newPassword.getText().toString();
            String verfiyPassword = changePassword_EDT_verifyPassword.getText().toString();

            //check same password
            userPassword = currentPassMatchInput(userPassword, changePassword_EDT_currentPassword, currentPassword);

            //check match between password and verfiy password
            samePassword = isSamePassword(samePassword, changePassword_EDT_currentPassword, changePassword_EDT_verifyPassword, newPassword, verfiyPassword);

            if (userPassword && samePassword) {
                uptadePasswordInDB(newPassword);
            }
        }
    };

    // make sure its the same password
    private boolean isSamePassword(boolean samePassword, TextInputEditText changePassword_EDT_currentPassword, TextInputEditText changePassword_EDT_verifyPassword, String newPassword, String verfiyPassword) {
        if (newPassword.equals(verfiyPassword) && newPassword.length() > 0) {
            samePassword = correctInput(changePassword_EDT_currentPassword);
        } else {
            putError(changePassword_EDT_verifyPassword, "Check Password");
        }
        return samePassword;
    }

    // checking if the input password equals to user password
    private boolean currentPassMatchInput(boolean userPassword, TextInputEditText changePassword_EDT_currentPassword, String currentPassword) {
        if (currentPassword.equals(user.getPassword())) {
            userPassword = correctInput(changePassword_EDT_currentPassword);
        } else {
            putError(changePassword_EDT_currentPassword, "Wrong Password");
        }
        return userPassword;
    }

    // set error on edit text
    private void putError(TextInputEditText changePassword_EDT_currentPassword, String text) {
        changePassword_EDT_currentPassword.setError(text);
    }


    private boolean correctInput(TextInputEditText changePassword_EDT_currentPassword) {
        boolean userPassword;
        userPassword = true;
        changePassword_EDT_currentPassword.setError(null);
        return userPassword;
    }


    // update password in DB when changing password
    private void uptadePasswordInDB(String newPassword) {
        updateDB("users/" + user.getPhone(), newPassword);
        updateDB(Activity_Main.DB_FAMILY_MEMBERS + "/" + user.getPhone(), newPassword);
        user.setPassword(newPassword);
        dialog.dismiss();
        MySignalV2.getInstance().showToast("Password Changed!");
    }

    private void updateDB(String path, String value) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child("password").setValue(value);
    }


    private void openDialog(int layout) {
        dialog = new Dialog(this);
        dialog.setContentView(layout);
        dialog.show();
    }


    // build dialog with family members list
    private void displayFamilyMembers() {
        Log.d(TAG, "displayFamilyMembers: ");
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Activity_Settings.this);
        builderSingle.setIcon(R.drawable.ic_logo);
        builderSingle.setTitle("Family Members");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, familyMembers);


        builderSingle.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        builderSingle.show();

    }


    private void initDialog() {
        MaterialTextView details_LBL_familyNickname = dialog.findViewById(R.id.details_LBL_familyNickname);
        MaterialTextView details_LBL_familyName = dialog.findViewById(R.id.details_LBL_familyName);
        MaterialTextView details_LBL_userName = dialog.findViewById(R.id.details_LBL_userName);
        MaterialTextView details_LBL_phoneNumber = dialog.findViewById(R.id.details_LBL_phoneNumber);
        MaterialTextView details_LBL_password = dialog.findViewById(R.id.details_LBL_password);
        MaterialButton details_BTN_ok = dialog.findViewById(R.id.details_BTN_ok);
        details_BTN_ok.setOnClickListener(finishDialog);

        setTextOnEditText(details_LBL_familyNickname, details_LBL_familyName,
                details_LBL_userName, details_LBL_phoneNumber, details_LBL_password);
    }

    // setting values on user details
    private void setTextOnEditText(MaterialTextView details_LBL_familyNickname, MaterialTextView details_LBL_familyName,
                                   MaterialTextView details_LBL_userName, MaterialTextView details_LBL_phoneNumber, MaterialTextView details_LBL_password) {
        details_LBL_familyNickname.setText(user.getKey());
        details_LBL_familyName.setText(user.getFamilyName());
        details_LBL_userName.setText(user.getUserName());
        details_LBL_phoneNumber.setText(user.getPhone());
        details_LBL_password.setText(user.getPassword());
    }

    // make sure the edit text not empty
    private boolean makeError(EditText inputLayout, String label) {
        if (inputLayout.length() == 0) {
            inputLayout.setError(label + " should not be empty");
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }

    private View.OnClickListener finishDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
        }
    };
}