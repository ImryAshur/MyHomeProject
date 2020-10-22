package com.example.project.Activities;
/*
Developer - Imry Ashur
*/
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.project.Adapters.Adapter_Event;
import com.example.project.CallBacks.CallBack_Calendar;
import com.example.project.Fragments.Fragment_Calendar;
import com.example.project.Fragments.Fragment_List;
import com.example.project.CallBacks.GetDataListener;
import com.example.project.Others.MySharedPreferencesV4;
import com.example.project.Objects.MyEvent;
import com.example.project.Objects.User;
import com.example.project.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Activity_Main extends AppCompatActivity {
    public static final String TAG = "pttt";
    public static final String EXTRA_KEY_USER = "EXTRA_KEY_USER";
    public static final String EXTRA_KEY_EVENT = "EXTRA_KEY_EVENT";

    public static String KEY_FAMILY_MEMBERS = "";
    public static String KEY_FAMILY_EVENTS = "";
    public static String KEY_USER_EVENTS = "";

    public static String DB_FAMILY_EVENTS = "";
    public static String DB_USER_EVENTS = "";
    public static String DB_FAMILY_MEMBERS = "";

    private DrawerLayout main_SPC_drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar main_SPC_toolBar;
    private NavigationView main_SPC_nav;
    private Gson gson = new Gson();
    private User user;
    private String currentDate;
    private String calendarClickedDate;
    private Fragment_Calendar fragment_calendar;
    private Fragment_List fragment_list;
    private TextView navHeader_LBL_userName;
    private TextView main_LBL_header;
    private ImageView main_IMG_plus;
    private ImageView main_IMG_background;
    private ArrayList<MyEvent> eventsArray = new ArrayList<>();
    private int mDateClicked ;
    private BroadcastReceiver myReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private boolean free = true;
    private Adapter_Event adapter_event;
    private MyEvent clickedEvent;
    private ProgressDialog progressDialog;
    private HashMap<String, String> hashMapFamilyMembersNames = new HashMap<>();
    private HashMap<String, MyEvent> hashMapFamilyEvents = new HashMap<>();
    private HashMap<String, MyEvent> hashMapUserEvents = new HashMap<>();


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
                    free = false;
                    createNewEvent(myEvent);
                } else if (intent.getAction().equals(Activity_NewEvent.EDIT_EVENT_BROADCAST) && free) {
                    free = false;
                    removeEventFromCalendar();
                    createNewEvent(myEvent);
                } else if (intent.getAction().equals(Activity_NewEvent.REMOVE_EVENT_BROADCAST) && free) {
                    free = false;
                    removeEventFromCalendar();
                    updatedData(eventsArray);
                }
            }
        };
        showProgressDialog();
        initViews();
        initFragments();
        getUser();
        setKeys();
        getHashMapMembersFromSP(KEY_FAMILY_MEMBERS);
        hashMapFamilyEvents = getHashMapDataFromSP(KEY_FAMILY_EVENTS);
        hashMapUserEvents = getHashMapDataFromSP(KEY_USER_EVENTS);
        getCurrentDate();
        setSideMenu();
        initBackground();
        initEvents(DB_FAMILY_EVENTS);
        initEvents(DB_USER_EVENTS);
        main_IMG_plus.setOnClickListener(plusClick);
        main_SPC_nav.setNavigationItemSelectedListener(menuListener);
        getFamilyMembers();




    }

    private void putEventsFromHashMaps(HashMap<String, MyEvent> hashMap) {
        if (hashMap.size() > 0){
        Log.d(TAG, "putEventsFromHashMaps: " + hashMap.size());
            for (MyEvent myEvent : hashMap.values()) {
                fragment_calendar.getCalendar_SPC_calendar().addEvent(myEvent);
                if (currentDate.equals(myEvent.getDate())) {
                    eventsArray.add(myEvent);
                }
            }
            if (eventsArray.size() > 0 )
                updatedData(eventsArray);
        }

    }

    private HashMap<String,MyEvent> getHashMapDataFromSP(String ref) {
        HashMap<String,MyEvent> hashMap = new HashMap<>();
        String hashMapString = MySharedPreferencesV4.getInstance().getString(ref, "");
        if (hashMapString.length() > 0) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, MyEvent>>() {}.getType();
            hashMap = gson.fromJson(hashMapString, type);
        }
        Log.d(TAG, "getDataFromSP: " + hashMap.size());
        return hashMap;
    }

    private void getHashMapMembersFromSP(String ref) {
        String hashMapString = MySharedPreferencesV4.getInstance().getString(ref, "");
        if (hashMapString.length() > 0) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            hashMapFamilyMembersNames = gson.fromJson(hashMapString, type);
        }else hashMapFamilyMembersNames = new HashMap<>();

        Log.d(TAG, "getDataFromSP: " + hashMapFamilyMembersNames.size());

    }

    private void getFamilyMembers() {
        Log.d(TAG, "onDataChange: ");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(DB_FAMILY_MEMBERS);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange1: " + user.getKey());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange2: ");
                    if (!(hashMapFamilyMembersNames.containsKey(ds.getKey()))) {
                        User user = ds.getValue(User.class);
                        Log.d(TAG, "onDataChange3: " + user.getUserName());
                        hashMapFamilyMembersNames.put(user.getPhone(), user.getUserName());
                    }

                }
                Log.d(TAG, "FINISH GET FAMILY MEMBERS : " + hashMapFamilyMembersNames.size());

                //upload Family members to SP
                String hashMapString = gson.toJson(hashMapFamilyMembersNames);
                MySharedPreferencesV4.getInstance().putString(KEY_FAMILY_MEMBERS, hashMapString);
                dismissDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    private void setSideMenu() {
        setSupportActionBar(main_SPC_toolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, main_SPC_drawerLayout, main_SPC_toolBar, R.string.open, R.string.close);
        main_SPC_drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    private void initBackground() {
        Glide
                .with(this)
                .load(R.drawable.background)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(main_IMG_background);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(myReceiver);

    }

    @Override
    protected void onStop() {
        //Upload Events To SP
        parseHashMapToStringAndUploadToSP(hashMapFamilyEvents, KEY_FAMILY_EVENTS);
        parseHashMapToStringAndUploadToSP(hashMapUserEvents, KEY_USER_EVENTS);
        super.onStop();
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
        if (clickedEvent.isSwitchOn()) hashMapFamilyEvents.remove(clickedEvent.getKey());
        else hashMapUserEvents.remove(clickedEvent.getKey());
        for (int i = 0; i < eventsArray.size(); i++) {
            if (clickedEvent.getKey().equals(eventsArray.get(i).getKey())) {
                eventsArray.remove(i);
                break;
            }
        }
        free = true;
    }

    private void initEvents(String path) {

        readData(FirebaseDatabase.getInstance().getReference(path), new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                //init Event From SP
                if (dataSnapshot.getKey().charAt(0) == 'f') putEventsFromHashMaps(hashMapFamilyEvents);
                else putEventsFromHashMaps(hashMapUserEvents);

                Log.d(TAG, "onSuccess: " + dataSnapshot.getKey().charAt(0));
                if (dataSnapshot != null) {
                    // init Events from DB
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onSuccess: " + ds.getKey());
                        if (dataSnapshot.getKey().charAt(0) == 'f' && (!hashMapFamilyEvents.containsKey(ds.getKey()))) {
                            Log.d(TAG, "FAMILY initEvents: " + ds.getValue().toString());
                            addEventToCalendarArrayHashMap(ds, hashMapFamilyEvents);

                        } else if (dataSnapshot.getKey().charAt(0) == 'u' && !(hashMapUserEvents.containsKey(ds.getKey()))) {
                            Log.d(TAG, "USER initEvents: " + ds.getValue().toString());
                            addEventToCalendarArrayHashMap(ds, hashMapUserEvents);
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

    private void addEventToCalendarArrayHashMap(DataSnapshot ds, HashMap<String, MyEvent> hashMap) {
        MyEvent myEvent = ds.getValue(MyEvent.class);
        hashMap.put(myEvent.getKey(), myEvent);
        fragment_calendar.getCalendar_SPC_calendar().addEvent(myEvent);
        if (currentDate.equals(myEvent.getDate())) {
            eventsArray.add(myEvent);
        }
    }


    private void createNewEvent(MyEvent myEvent) {
        fragment_calendar.getCalendar_SPC_calendar().addEvent(myEvent);
        if (calendarClickedDate.equals(myEvent.getDate())) {
            Log.d(TAG, "day clicked == today");
            eventsArray.add(myEvent);
        }
        updatedData(eventsArray);
        if (myEvent.isSwitchOn()) {
            uploadEventToDB(myEvent, DB_FAMILY_EVENTS);
        } else uploadEventToDB(myEvent, DB_USER_EVENTS);
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
            navHeader_LBL_userName.setText(user.getUserName());

        } else {
            Log.d(TAG, "getUser: FAILD!!!!!!!!!!!");
        }

    }

    private void setKeys() {
        KEY_FAMILY_MEMBERS = user.getKey() + "_FAMILY_MEMBERS";
        KEY_FAMILY_EVENTS = user.getKey() + "_FAMILY_EVENTS";
        KEY_USER_EVENTS = user.getUserName() + "_USER_EVENTS";

        DB_FAMILY_EVENTS = "families/" + user.getKey() + "/familyMyEvents";
        DB_USER_EVENTS ="users/" + user.getPhone() + "/userMyEvents";
        DB_FAMILY_MEMBERS = "families/" + user.getKey() + "/familyMembers";

    }

    private void initViews() {
        main_LBL_header = findViewById(R.id.main_LBL_header);
        main_IMG_plus = findViewById(R.id.main_IMG_plus);
        main_IMG_background = findViewById(R.id.main_IMG_background);
        main_SPC_drawerLayout = findViewById(R.id.main_SPC_drawerLayout);
        main_SPC_toolBar = findViewById(R.id.main_SPC_toolBar);
        main_SPC_nav = findViewById(R.id.main_SPC_nav);
        View headerView = main_SPC_nav.getHeaderView(0);
        navHeader_LBL_userName = headerView.findViewById(R.id.navHeader_LBL_userName);
    }


    private void updatedData(ArrayList itemsArrayList) {
        if (adapter_event == null) {
            adapter_event = new Adapter_Event(this, itemsArrayList);
            adapter_event.setClickListeners(eventItemClickListener);
            fragment_list.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
            fragment_list.getRecyclerView().setAdapter(adapter_event);
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

    private NavigationView.OnNavigationItemSelectedListener menuListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.nav_LBL_notes:
                    startNewActivity(Activity_Notes.class, true);
                    break;
                case R.id.nav_LBL_settings:
                    startNewActivity(Activity_Settings.class, true);
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

    private void parseHashMapToStringAndUploadToSP(HashMap<String, MyEvent> hashMap, String path) {
        String hashMapString = new Gson().toJson(hashMap);
        MySharedPreferencesV4.getInstance().putString(path, hashMapString);
    }

    private void startNewActivity(Class newActivity, boolean parseUser) {
        Intent intent = new Intent(Activity_Main.this, newActivity);
        if (parseUser) {
            String stringUser = gson.toJson(user);
            intent.putExtra(EXTRA_KEY_USER, stringUser);
        }
        startActivity(intent);
        finish();
    }


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
        public void getEventFromCalendar(CompactCalendarView calendar, Date date, int dateClicked, String parseDate) {
            mDateClicked = dateClicked;
            calendarClickedDate = parseDate;
            eventsArray.clear();
            ArrayList<Event> array = (ArrayList<Event>) calendar.getEvents(date);
            for (int i = 0; i < array.size(); i++) {
                eventsArray.add((MyEvent) array.get(i));
            }
            updatedData(eventsArray);
        }
    };

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Events...");
        progressDialog.show();
    }

    private void dismissDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

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

