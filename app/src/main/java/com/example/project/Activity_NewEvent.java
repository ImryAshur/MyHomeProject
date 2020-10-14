package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Activity_NewEvent extends AppCompatActivity {
    public static final String EXTRA_KEY_USER_FAMILY_NAME = "EXTRA_KEY_USER_FAMILY_NAME";
    public static final String EXTRA_KEY_USER_PHONE_NUMBER = "EXTRA_KEY_USER_PHONE_NUMBER";
    public static final String EXTRA_KEY_DATE = "EXTRA_KEY_DATE";
    public static final String EXTRA_KEY_USER_EVENT = "EXTRA_KEY_USER_EVENT";
    public static final String BROADCAST = "com.example.project.NEW_LOCATION_DETECTED";
    public static final String REMOVE_EVENT_BROADCAST = "com.example.project.REMOVE_EVENT_BROADCAST";
    public static final String EDIT_EVENT_BROADCAST = "com.example.project.EDIT_EVENT_BROADCAST";
    private TextView newEvent_LBL_header;
    private AutoCompleteTextView newEvent_LST_eventType;
    private TextInputEditText newEvent_LST_members;
    private AutoCompleteTextView newEvent_BTN_color;
    private TextInputEditText newEvent_EDT_date;
    private TextInputEditText newEvent_EDT_location;
    private TextInputEditText newEvent_EDT_timeStart;
    private TextInputEditText newEvent_EDT_description;
    private Switch newEvent_SPC_shareSwitch;
    private MaterialButton newEvent_BTN_createEvent;
    private MaterialButton newEvent_BTN_cancel;
    private String userFamilyName;
    private String userPhone;
    private Family family;
    private MyEvent event;
    private boolean[] checkedItems;
    private ArrayList<String> familyMembers = new ArrayList<>();
    private ArrayList<Integer> mUserItems = new ArrayList<>();

    private DatePickerDialog picker;
    private Calendar calendar = Calendar.getInstance();

    private String[] eventList = {"Family Event", "Activity", "Pick Up", "Other"};

    private String[] colorList = {"Black", "Green", "Yellow", "Blue"};
    private int[] colors = {Color.BLACK, Color.GREEN, Color.YELLOW, Color.BLUE};
    private int colorSelected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newevent);
        initviews();
        getMyIntent();

        setDropDownMenu(newEvent_LST_eventType, eventList);
        setDropDownMenu(newEvent_BTN_color, colorList);

        //listeners

        newEvent_BTN_color.setOnItemClickListener(colorPicker);
        newEvent_EDT_date.setOnClickListener(datePicker);
        newEvent_EDT_timeStart.setOnClickListener(timePicker);
    }

    private void initviews() {
        newEvent_LBL_header = findViewById(R.id.newEvent_LBL_header);
        newEvent_LST_eventType = findViewById(R.id.newEvent_LST_eventType);
        newEvent_LST_members = findViewById(R.id.newEvent_LST_members);
        newEvent_EDT_location = findViewById(R.id.newEvent_EDT_location);
        newEvent_EDT_date = findViewById(R.id.newEvent_EDT_date);
        newEvent_EDT_timeStart = findViewById(R.id.newEvent_EDT_timeStart);
        newEvent_EDT_description = findViewById(R.id.newEvent_EDT_description);
        newEvent_BTN_color = findViewById(R.id.newEvent_BTN_color);
        newEvent_SPC_shareSwitch = findViewById(R.id.newEvent_SPC_shareSwitch);
        newEvent_BTN_createEvent = findViewById(R.id.newEvent_BTN_createEvent);
        newEvent_BTN_cancel = findViewById(R.id.newEvent_BTN_cancel);

    }

    private void getMyIntent() {
        newEvent_BTN_createEvent.setOnClickListener(newEvent);
        newEvent_BTN_cancel.setOnClickListener(closeActivity);
        Intent intent = getIntent();
        userFamilyName = intent.getStringExtra(EXTRA_KEY_USER_FAMILY_NAME);
        userPhone = intent.getStringExtra(EXTRA_KEY_USER_PHONE_NUMBER);
        newEvent_EDT_date.setText(intent.getStringExtra(EXTRA_KEY_DATE));
        String myEvent = intent.getStringExtra(EXTRA_KEY_USER_EVENT);
        if (myEvent != null) {
            event = new Gson().fromJson(myEvent, MyEvent.class);
            setProperties(event);
            newEvent_BTN_cancel.setOnClickListener(removeEvent);
        }
        //Log.d("pttt", "getFamilyName: " + userFamilyName);
        if (userFamilyName.length() > 0) {
            readData(FirebaseDatabase.getInstance().getReference("families/" + userFamilyName), new GetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    family = dataSnapshot.getValue(Family.class);
                    setMemberListCheckBox();
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

    private void setProperties(MyEvent event) {
        newEvent_BTN_createEvent.setText("edit");
        newEvent_BTN_cancel.setText("remove");
        newEvent_LBL_header.setText("Edit Event");
        newEvent_LST_eventType.setText(event.getEventType());
        newEvent_LST_members.setText(event.getParticipants());
        newEvent_BTN_color.setText(colorList[event.getColorArrNum()]);
        newEvent_EDT_date.setText(event.getDate());
        newEvent_EDT_location.setText(event.getLocation());
        newEvent_EDT_timeStart.setText(event.getStartTime());
        newEvent_EDT_description.setText(event.getDescription());
        newEvent_SPC_shareSwitch.setChecked(event.isSwitchOn());

    }

    private void setMemberListCheckBox() {
        ArrayList<User> temp = family.getFamilyMembers();
        final String[] listMembers = new String[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            familyMembers.add(temp.get(i).getUserName());
            listMembers[i] = temp.get(i).getUserName();
        }
        checkedItems = new boolean[familyMembers.size()];
        newEvent_LST_members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Activity_NewEvent.this);
                mBuilder.setTitle("Choose Members");
                mBuilder.setMultiChoiceItems(listMembers, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            mUserItems.add(i);
                        } else {
                            mUserItems.remove((Integer.valueOf(i)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listMembers[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        newEvent_LST_members.setText(item);
                    }
                });

                mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
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

    private void setDropDownMenu(AutoCompleteTextView textView, String[] List) {
        ArrayAdapter<String> adapter = new ArrayAdapter(Activity_NewEvent.this, R.layout.dropdown_menu_popup_item, List);
        textView.setAdapter(adapter);
    }

    private boolean makeError( EditText inputLayout, String label) {
        if (inputLayout.length() == 0) {
            inputLayout.setError(label + " should not be empty");
            return false;
        } else {
            inputLayout.setError(null);
            return true;
        }
    }

    private long parseDateToMilis(String eventDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long dateInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(eventDate);
            dateInMilliseconds = mDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateInMilliseconds;
    }


    // Listeners methods
    private AdapterView.OnItemClickListener colorPicker = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            colorSelected = i;
        }
    };

    private View.OnClickListener newEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean eventCheck, membersCheck, dateCheck, timeCheck , edit = false;
            String eventType = newEvent_LST_eventType.getText().toString().trim();
            String members = newEvent_LST_members.getText().toString().trim();
            String location = newEvent_EDT_location.getText().toString().trim();
            String date = newEvent_EDT_date.getText().toString().trim();
            String time = newEvent_EDT_timeStart.getText().toString().trim();
            String description = newEvent_EDT_description.getText().toString().trim();
            boolean switchOn = newEvent_SPC_shareSwitch.isChecked();
            eventCheck = makeError(newEvent_LST_eventType, "Event Type");
            membersCheck = makeError(newEvent_LST_members, "Participants");
            dateCheck = makeError(newEvent_EDT_date, "Date");
            timeCheck = makeError(newEvent_EDT_timeStart, "Time");

            if (eventCheck && membersCheck && dateCheck && timeCheck) {
                if (newEvent_BTN_createEvent.getText().charAt(0) == 'e') {
                    edit = true;
                    if (event.isSwitchOn()) {
                        removeFromDB("families/" + userFamilyName + "/familyMyEvents");
                    } else removeFromDB("users/"+ userPhone+ "/userMyEvents");
                }
                long dateInMilliseconds = parseDateToMilis(date);
                MyEvent myEvent = new MyEvent(eventType, members, location, date, dateInMilliseconds, time, description, colors[colorSelected], colorSelected, switchOn);
                Gson gson = new Gson();
                String mEvent = gson.toJson(myEvent);
                if (edit){
                    sendMyBroadcast(EDIT_EVENT_BROADCAST,mEvent);
                }else sendMyBroadcast(BROADCAST,mEvent);
            }
        }
    };

    private void sendMyBroadcast(String broadcast , String mEvent) {
        Intent intent = new Intent(broadcast);
        intent.putExtra(Activity_Main.EXTRA_KEY_EVENT, mEvent);
        LocalBroadcastManager.getInstance(Activity_NewEvent.this).sendBroadcast(intent);
        finish();
    }

    private void removeFromDB(String path) {
        readData(FirebaseDatabase.getInstance().getReference(path), new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                   dataSnapshot.child(event.getKey()+"").getRef().removeValue();
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


    private View.OnClickListener datePicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(Activity_NewEvent.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            newEvent_EDT_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }, year, month, day);
            picker.show();
        }
    };


    private View.OnClickListener timePicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int minute = calendar.get(Calendar.MINUTE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            timeDialog(newEvent_EDT_timeStart, minute, hour);

        }
    };

    private View.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };



    private View.OnClickListener removeEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (event.isSwitchOn()) removeFromDB("families/" + userFamilyName + "/familyMyEvents");
            else removeFromDB("users/"+ userPhone+ "/userMyEvents");
            String myEvent = new Gson().toJson(event);
            sendMyBroadcast(REMOVE_EVENT_BROADCAST,myEvent);
            finish();
        }
    };

    private void timeDialog(final TextView time, final int minute, final int hour) {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_NewEvent.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        if (selectedMinute <= 9) {
                            time.setText(selectedHour + ":" + "0" + selectedMinute);
                        } else
                            time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }
}