package com.example.project.Objects;

import java.sql.Timestamp;

public class MyEvent extends com.github.sundeepk.compactcalendarview.domain.Event {


    private String key;
    private String eventType;
    private String participants;
    private String location;
    private String description;
    private String startTime;
    private String date;
    private int colorChoosed = 0;
    private int colorArrNum;
    private boolean switchOn;
    private long dateInMillis = 0;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public MyEvent(){
        super(0,0);
    }
    public MyEvent(String eventType, String participants, String location, String date, long dateInMillis, String startTime, String description, int colorChoosed, int colorArrNum, boolean switchOn) {
        super(colorChoosed, dateInMillis);
        this.eventType = eventType;
        this.location = location;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.participants = participants;
        //this.colorChoosed = colorChoosed;
        this.colorArrNum = colorArrNum;
        this.switchOn = switchOn;
        //this.dateInMillis = dateInMillis;
        this.key = timestamp.getTime() + "";
    }


    @Override
    public String toString() {
        return eventType + ", " + date + ", " + startTime + " - " + participants;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEventType() {
        return eventType;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getParticipants() {
        return participants;
    }

    public String getDate() {
        return date;
    }

    public int getColorChoosed() {
        return colorChoosed;
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setColorChoosed(int colorChoosed) {
        this.colorChoosed = colorChoosed;
    }

    public void setColorArrNum(int colorArrNum) {
        this.colorArrNum = colorArrNum;
    }

    public void setSwitchOn(boolean switchOn) {
        this.switchOn = switchOn;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public int getColorArrNum() {
        return colorArrNum;
    }

    @Override
    public boolean equals(Object o) {
        MyEvent myEvent = (MyEvent) o;
        if (this.getKey().equals(myEvent.getKey())) return true;
        return false;
    }

}
