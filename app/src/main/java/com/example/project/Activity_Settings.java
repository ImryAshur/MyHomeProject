package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class Activity_Settings extends AppCompatActivity {

    private MaterialTextView settings_LBL_details;
    private MaterialTextView settings_LBL_familyMembers;
    private MaterialTextView settings_LBL_addMember;
    private MaterialTextView settings_LBL_password;
    private ImageView settings_IMG_menu;
    private Dialog dialog;
    //------- details dialog
    private MaterialTextView details_LBL_familyNickname;
    private MaterialTextView details_LBL_familyName;
    private MaterialTextView details_LBL_userName;
    private MaterialTextView details_LBL_phoneNumber;
    private MaterialTextView details_LBL_password;
    private MaterialButton details_BTN_ok;
    //------- change password dialog
    private TextInputEditText changePassword_EDT_currentPassword;
    private TextInputEditText changePassword_EDT_newPassword;
    private TextInputEditText changePassword_EDT_verifyPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        settings_LBL_details.setOnClickListener(buttonsListener);

    }

    private void initViews() {
        settings_LBL_details = findViewById(R.id.settings_LBL_details);
        settings_LBL_familyMembers = findViewById(R.id.settings_LBL_familyMembers);
        settings_LBL_addMember = findViewById(R.id.settings_LBL_addMember);
        settings_LBL_password = findViewById(R.id.settings_LBL_password);
        settings_IMG_menu = findViewById(R.id.settings_IMG_menu);

    }

    private View.OnClickListener buttonsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //if (((String) view.getTag()).equals("0"))
            openDialog(R.layout.dialog_user_details,1);
        }
    };

    private void openDialog(int layout , int num) {
        dialog = new Dialog(this);
        dialog.setContentView(layout);
        initDialog(num);
        setUserDetails();
//        newNote_BTN_go.setOnClickListener(newNoteGoBTN);
//        if (noteClicked == null){
//            newNote_BTN_cancel.setOnClickListener(cancelBTN);
//        }
//        else {
//            setProperties(noteClicked);
//        }
        dialog.show();
    }

    private void setUserDetails() {
    }

    private void initDialog(int num) {
        details_LBL_familyNickname = dialog.findViewById(R.id.details_LBL_familyNickname);
        details_LBL_familyName = dialog.findViewById(R.id.details_LBL_familyName);
        details_LBL_userName = dialog.findViewById(R.id.details_LBL_userName);
        details_LBL_phoneNumber = dialog.findViewById(R.id.details_LBL_phoneNumber);
        details_LBL_password = dialog.findViewById(R.id.details_LBL_password);
        details_BTN_ok = dialog.findViewById(R.id.details_BTN_ok);
        details_BTN_ok.setOnClickListener(finishDialog);
    }

    private View.OnClickListener finishDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
        }
    };
}