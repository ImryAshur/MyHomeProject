package com.example.project;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Fragment_Calendar extends Fragment {
    protected View view;
    private CompactCalendarView calendar_SPC_calendar;
    private CallBack_Calendar callBack_calendar;

    public CompactCalendarView getCalendar_SPC_calendar() {
        return calendar_SPC_calendar;
    }

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    public void setActivityCallBack(CallBack_Calendar callBack_calendar) {
        this.callBack_calendar = callBack_calendar;
    }

    public static Fragment_Calendar newInstance() {
        Fragment_Calendar fragment = new Fragment_Calendar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view = inflater.inflate(R.layout.fragment_calendar, container, false);
        }
        findViews(view);
        initCalendar();
        calendar_SPC_calendar.setListener(setListener);
        return view;
    }

    private void initCalendar() {
        calendar_SPC_calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar_SPC_calendar.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        calendar_SPC_calendar.setUseThreeLetterAbbreviation(true);
        calendar_SPC_calendar.setShouldDrawDaysHeader(true);
        callBack_calendar.setHeaderText(dateFormatForMonth.format(calendar_SPC_calendar.getFirstDayOfCurrentMonth()));
    }

    private void findViews(View view) {
        calendar_SPC_calendar= view.findViewById(R.id.calendar_SPC_calendar);
    }

    private CompactCalendarView.CompactCalendarViewListener setListener = new CompactCalendarView.CompactCalendarViewListener(){

        @Override
        public void onDayClick(Date dateClicked) {
           // Log.d("pttt", "onDayClick: " + dateClicked.getDate());
            callBack_calendar.getEventFromCalendar(calendar_SPC_calendar,dateClicked,dateClicked.getDate());
        }

        @Override
        public void onMonthScroll(Date firstDayOfNewMonth) {
            callBack_calendar.setHeaderText(dateFormatForMonth.format(firstDayOfNewMonth));
        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
