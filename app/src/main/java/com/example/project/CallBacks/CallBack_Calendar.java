package com.example.project.CallBacks;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import java.util.Date;

public interface CallBack_Calendar {
    void setHeaderText(String text);
    void getEventFromCalendar(CompactCalendarView calendar_SPC_calendar,Date date, int dateClicked,String parseDate);
}
