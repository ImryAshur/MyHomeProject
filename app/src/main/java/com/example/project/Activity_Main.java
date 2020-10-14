package com.example.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Activity_Main extends AppCompatActivity {
    public static final String TAG = "pttt";
    public static final String EXTRA_KEY_USER = "EXTRA_KEY_USER";
    public static final String EXTRA_KEY_EVENT = "EXTRA_KEY_EVENT";
    private Gson gson = new Gson();
    private User user;
    private String currentDate;
    private String calendarClickedDate;
    private Fragment_Calendar fragment_calendar;
    private Fragment_List fragment_list;
    private TextView main_LBL_header;
    private ImageView main_IMG_plus;
    private ArrayList<MyEvent> eventsArray = new ArrayList<>();
    private int mDateClicked ;
    private BroadcastReceiver myReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private boolean free = true;
    private Adapter_Event adapter_event;
    private MyEvent clickedEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                localBroadcastManager.unregisterReceiver(myReceiver);
                String mEvent = intent.getStringExtra(EXTRA_KEY_EVENT);
                MyEvent myEvent = new Gson().fromJson(mEvent, MyEvent.class);
                if (intent.getAction().equals(Activity_NewEvent.BROADCAST) && free) {
                    Log.d(TAG, "onReceive: zzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
                    free = false;
                    createNewEvent(myEvent);
                } else if (intent.getAction().equals(Activity_NewEvent.EDIT_EVENT_BROADCAST) && free) {
                    free = false;
                    Log.d(TAG, "hereeeeeeeeeeeeeeeeeeeeeeeeee");
                    removeEventFromCalendar();
                    createNewEvent(myEvent);
                } else if (intent.getAction().equals(Activity_NewEvent.REMOVE_EVENT_BROADCAST) && free) {
                    free = false;
                    removeEventFromCalendar();
                    updatedData(eventsArray);
                }
            }
        };
        initViews();
        getUser();
        initFragments();
        getCurrentDate();
        initEvents("families/" + user.getFamilyName() + "/familyMyEvents");
        initEvents("users/" + user.getPhone() + "/userMyEvents");
        Log.d(TAG, "onCreate: " + mDateClicked);
        main_IMG_plus.setOnClickListener(plusClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(myReceiver);

    }
    @Override
    protected void onResume() {
        super.onResume();
        setIntentFilter(Activity_NewEvent.BROADCAST);
        setIntentFilter(Activity_NewEvent.EDIT_EVENT_BROADCAST);
        setIntentFilter(Activity_NewEvent.REMOVE_EVENT_BROADCAST);
    }

    private void setIntentFilter(String broadcast) {
        IntentFilter intentFilter = new IntentFilter(broadcast);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(myReceiver, intentFilter);
    }

    private void getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        currentDate = dateFormat.format(date);
        calendarClickedDate = currentDate;
    }

    private void removeEventFromCalendar() {
        Log.d(TAG, "removeEventFromCalendar: " + eventsArray);
        fragment_calendar.getCalendar_SPC_calendar().removeEvent(clickedEvent);
        for (int i = 0 ; i < eventsArray.size(); i++){
            if (clickedEvent.getKey().equals(eventsArray.get(i).getKey())){
                eventsArray.remove(i);
            }
        }
        free = true;
    }

    private void initEvents(String path) {
        Log.d(TAG, "initEvents: ");
        readData(FirebaseDatabase.getInstance().getReference(path), new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d(TAG, "initEvents: " + ds.getValue().toString());
                        MyEvent myEvent = ds.getValue(MyEvent.class);
                        fragment_calendar.getCalendar_SPC_calendar().addEvent(myEvent);
                        if (currentDate.equals(myEvent.getDate())) {
                            eventsArray.add(myEvent);
                        }
                    }
                    if (eventsArray.size() != 0) {
                        updatedData(eventsArray);
                    }
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


    private void  createNewEvent(MyEvent myEvent) {
        fragment_calendar.getCalendar_SPC_calendar().addEvent(myEvent);
        if (calendarClickedDate.equals(myEvent.getDate())) {
            Log.d(TAG, "day clicked == today");
            eventsArray.add(myEvent);
            updatedData(eventsArray);
        }
        if (myEvent.isSwitchOn()){
            uploadEventToDB(myEvent, "families/" + user.getFamilyName() + "/familyMyEvents");
        }
        else uploadEventToDB(myEvent, "users/" + user.getPhone() + "/userMyEvents");
        free = true;
    }


    private void uploadEventToDB(final MyEvent myEvent, String path) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.child(myEvent.getKey()).setValue(myEvent);
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


    private void getUser() {
        Intent intent = getIntent();
        String tempUser = intent.getStringExtra(EXTRA_KEY_USER);
        if (tempUser.length() > 0) {
            user = gson.fromJson(tempUser, User.class);
        } else {
            Log.d(TAG, "getUser: FAILD!!!!!!!!!!!");
        }

    }

    private void initViews() {
        main_LBL_header = findViewById(R.id.main_LBL_header);
        main_IMG_plus = findViewById(R.id.main_IMG_plus);

    }



    private void updatedData(ArrayList itemsArrayList) {
        if (adapter_event == null) {
            adapter_event = new Adapter_Event(this, itemsArrayList);
            adapter_event.setClickListeners(eventItemClickListener);
            fragment_list.getList_LST_list().setLayoutManager(new LinearLayoutManager(this));
            fragment_list.getList_LST_list().setAdapter(adapter_event);
        } else {
            adapter_event.notifyDataSetChanged();
        }
    }

    private View.OnClickListener plusClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startNewEventActivity("");
        }
    };


    private void initFragments() {
        fragment_list = new Fragment_List();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_LAY_list, fragment_list);
        transaction.commit();


        fragment_calendar = new Fragment_Calendar();
        fragment_calendar.setActivityCallBack(callBack_calendar);
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.replace(R.id.main_SPC_calendar, fragment_calendar);
        transaction1.commit();
    }

//    ------------------ callBack Calendar ----------

    CallBack_Calendar callBack_calendar = new CallBack_Calendar() {
        @Override
        public void setHeaderText(String text) {
            main_LBL_header.setText(text);
        }

        @Override
        public void getEventFromCalendar(CompactCalendarView calendar, Date date, int dateClicked,String parseDate ) {
            mDateClicked = dateClicked;
            calendarClickedDate = parseDate;
            eventsArray.clear();
            ArrayList<Event> array = (ArrayList<Event>) calendar.getEvents(date);
            for (int i = 0 ; i < array.size(); i++){
                eventsArray.add((MyEvent)array.get(i));
            }
            updatedData(eventsArray);
        }
    };

    //    ------------------ callBack List ----------
    Adapter_Event.EventItemClickListener eventItemClickListener = new Adapter_Event.EventItemClickListener() {
        @Override
        public void itemClicked(MyEvent event, int position) {
            clickedEvent = event;
            String myEvent = new Gson().toJson(event);
            startNewEventActivity(myEvent);
        }
    };

    private void startNewEventActivity(String myEvent) {
        Intent intent = new Intent(Activity_Main.this, Activity_NewEvent.class);
        intent.putExtra(Activity_NewEvent.EXTRA_KEY_USER_FAMILY_NAME, user.getFamilyName());
        intent.putExtra(Activity_NewEvent.EXTRA_KEY_USER_PHONE_NUMBER, user.getPhone());
        intent.putExtra(Activity_NewEvent.EXTRA_KEY_DATE, calendarClickedDate);
        if (myEvent.length() > 0) {
            intent.putExtra(Activity_NewEvent.EXTRA_KEY_USER_EVENT, myEvent);
        }
        startActivity(intent);
    }
}

