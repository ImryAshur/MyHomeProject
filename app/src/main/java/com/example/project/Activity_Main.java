package com.example.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class Activity_Main extends AppCompatActivity {
    public static final String TAG = "pttt";
    private DatePickerDialog picker;
    private Calendar calendar = Calendar.getInstance();
    private Dialog dialog;
    private ArrayAdapter arrayAdapter;
    private Fragment_Calendar fragment_calendar;
    private Fragment_List fragment_list;
    private TextView main_LBL_header;
    private ImageView main_IMG_plus;
    private ArrayList<com.github.sundeepk.compactcalendarview.domain.Event> eventsArray = new ArrayList<>();
    private int mDateClicked;

    private String colors_array[] = {"Black","Green","Yellow","Blue"};
    private int[] colors = {Color.BLACK,Color.GREEN,Color.YELLOW,Color.BLUE};
    private TextView event_EDT_title;
    private TextView event_LBL_header;
    private TextView event_EDT_description;
    private TextView event_EDT_location;
    private TextView event_EDT_date;
    private TextView event_EDT_timeStart;
    private TextView event_EDT_timeEnd;
    private Switch event_SPC_shareSwitch;
    private Button event_BTN_createEvent;
    private Button event_BTN_color;

    private String eventTitle = "";
    private String eventLocation = "";
    private String eventDescription = "";
    private String eventStartTime = "";
    private String eventEndTime = "";
    private String eventDate = "";
    private int colorChoosed = 0;
    private long dateInMilliseconds;
    private boolean switchOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initFragments();
        main_IMG_plus.setOnClickListener(plusClick);


    }

    private void initViews() {
        main_LBL_header = findViewById(R.id.main_LBL_header);
        main_IMG_plus = findViewById(R.id.main_IMG_plus);

    }

    private View.OnClickListener plusClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openDialog();
            event_BTN_createEvent.setTag("n");
        }
    };

    private View.OnClickListener datePicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(Activity_Main.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            event_EDT_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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
            if (((String) view.getTag()).equals("S")) timeDialog(event_EDT_timeStart,minute,hour);
            else timeDialog(event_EDT_timeEnd,minute,hour);
        }
    };


    private View.OnClickListener colorPicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Main.this);
            builder.setTitle(R.string.pick_color)
                    .setItems(colors_array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            colorChoosed = which;
                        }

                    });
            AlertDialog d = builder.create();
            d.show();
        }

    };

    private void timeDialog(final TextView time, final int minute, final int hour) {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_Main.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour,int selectedMinute) {
                            if (selectedMinute <= 9) {
                                time.setText(selectedHour + ":" + "0" + selectedMinute);
                            }else
                                time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private View.OnClickListener addEventClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            eventTitle = event_EDT_title.getText().toString();
            eventLocation = event_EDT_location.getText().toString();
            eventDate = event_EDT_date.getText().toString();
            eventStartTime = event_EDT_timeStart.getText().toString();
            eventEndTime = event_EDT_timeEnd.getText().toString();
            eventDescription = event_EDT_description.getText().toString();
            switchOn = event_SPC_shareSwitch.isChecked();
            parseDateToMilis();
           // if (((String) view.getTag()).equals("n"))
            newEvent();
            Log.d(TAG, eventTitle + "," + eventLocation + "," + eventDate +  ","  + dateInMilliseconds +  "," + eventStartTime + "," + eventEndTime + "," + eventDescription +
                    "," + switchOn +"," + colorChoosed);
            //setArray();
            dialog.dismiss();
        }
    };

    private void newEvent() {
        Event event = new Event(eventTitle,eventLocation,eventDate,dateInMilliseconds,eventStartTime,eventEndTime,eventDescription,colors[colorChoosed],colorChoosed,switchOn);
        String[] day = eventDate.split("/");
        int mDay = Integer.parseInt(day[0]);
        if(mDay == mDateClicked){
            eventsArray.add(event);
            fragment_calendar.getCalendar_SPC_calendar().removeEvents(dateInMilliseconds);
            fragment_calendar.getCalendar_SPC_calendar().addEvents(eventsArray);
            Log.d(TAG, "newEvent: " + eventsArray);
            setArray();
        }else{
            fragment_calendar.getCalendar_SPC_calendar().addEvent(event);
        }
        if (switchOn) sharedData();
        else mData();
    }

    private void mData() {

    }

    private void sharedData() {
    }


    private void parseDateToMilis() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            Date mDate = sdf.parse(eventDate);
            dateInMilliseconds = mDate.getTime();
            Log.d(TAG, "mili " + dateInMilliseconds);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    public void openDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_event);
        initDialog();
        //Set Listeners
        event_EDT_timeEnd.setOnClickListener(timePicker);
        event_EDT_timeStart.setOnClickListener(timePicker);
        event_EDT_date.setOnClickListener(datePicker);
        event_BTN_color.setOnClickListener(colorPicker);
        event_BTN_createEvent.setOnClickListener(addEventClick);
        dialog.show();
    }

    private void setArray() {
        arrayAdapter = new ArrayAdapter(Activity_Main.this,android.R.layout.simple_list_item_1, eventsArray);
        fragment_list.getList_LST_list().setAdapter(arrayAdapter);
    }

    private void initDialog() {
        event_LBL_header = dialog.findViewById(R.id.event_LBL_header);
        event_SPC_shareSwitch = dialog.findViewById(R.id.event_SPC_shareSwitch);
        event_EDT_date = dialog.findViewById(R.id.event_EDT_date);
        event_EDT_timeStart = dialog.findViewById(R.id.event_EDT_timeStart);
        event_EDT_timeEnd = dialog.findViewById(R.id.event_EDT_timeEnd);
        event_EDT_location = dialog.findViewById(R.id.event_EDT_location);
        event_EDT_description = dialog.findViewById(R.id.event_EDT_description);
        event_EDT_title = dialog.findViewById(R.id.event_EDT_title);
        event_BTN_createEvent = dialog.findViewById(R.id.event_BTN_createEvent);
        event_BTN_color = dialog.findViewById(R.id.event_BTN_color);
    }

    private void initFragments() {
        fragment_list = new Fragment_List();
        fragment_list.setActivityCallBack(callBack_list);
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
        public void getEventFromCalendar(CompactCalendarView calendar,Date date, int dateClicked) {
            //Log.d(TAG, "getEventFromCalendar: " + mDateClicked);
            mDateClicked = dateClicked;
            eventsArray = new ArrayList<>();
            //Log.d(TAG, "getEvents before: " + eventsArray);
            eventsArray = (ArrayList<com.github.sundeepk.compactcalendarview.domain.Event>) calendar.getEvents(date);
            Log.d(TAG, "getEventFromCalendar: " + eventsArray);
            setArray();

        }
    };





//    ------------------ callBack List ----------
    CallBack_List callBack_list = new CallBack_List() {
        @Override
        public void getEventFromList(int index) {
            Log.d(TAG, "getEventFromList: arraylist " + eventsArray );
            Event event = (Event) eventsArray.get(index);
            eventsArray.remove(index);
            fragment_calendar.getCalendar_SPC_calendar().removeEvent(event);
            Log.d(TAG, "callBack List: "+ eventsArray + "event: " + event +" ,index -  " +  index);
            openDialog();
            setEventProperties(event);
            //updateEvent(event);
            //setArray();
        }
    };

    private void updateEvent(Event event) {
        event.setTitle(eventTitle);
        event.setDescription(eventDescription);
        event.setDate(eventDate);
        event.setStartTime(eventStartTime);
        event.setEndTime(eventEndTime);
        event.setLocation(eventLocation);
        event.setColorArrNum(colorChoosed);
        event.setColorChoosed(colors[colorChoosed]);
        event.setDateInMillis(dateInMilliseconds);
        event.setSwitchOn(switchOn);
    }

    private void setEventProperties(Event event) {
        event_EDT_title.setText(event.getTitle());
        event_EDT_location.setText(event.getLocation());
        event_EDT_date.setText(event.getDate());
        event_EDT_timeStart.setText(event.getStartTime());
        event_EDT_timeEnd.setText(event.getEndTime());
        event_EDT_description.setText(event.getDescription());
        colorChoosed = event.getColorArrNum();
        event_SPC_shareSwitch.setChecked(event.isSwitchOn());
        event_BTN_createEvent.setText("Edit");
        event_BTN_createEvent.setTag("e");
        event_LBL_header.setText("Edit Event");
    }

//    private View.OnClickListener editEventClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            updateEvent();
//            dialog.dismiss();
//
//        }
//    };
}

