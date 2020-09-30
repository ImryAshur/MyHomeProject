package com.example.project;

public class Event extends com.github.sundeepk.compactcalendarview.domain.Event {
    private String title;
    private String location;
    private String description;
    private String startTime;
    private String endTime;
    private String date;
    private int colorChoosed;
    private int colorArrNum;
    private boolean switchOn;
    private long dateInMillis;


    public Event(String title, String location, String date, long dateInMillis, String startTime, String endTime, String description, int colorChoosed, int colorArrNum, boolean switchOn) {
        super(colorChoosed, dateInMillis);
        this.title = title;
        this.location = location;
        this.date = date;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.colorChoosed = colorChoosed;
        this.colorArrNum = colorArrNum;
        this.switchOn = switchOn;
        this.dateInMillis = dateInMillis;
    }

    @Override
    public String toString() {
        return title + ", " + date + ", " + startTime + " - " + endTime;
    }

    public String getTitle() {
        return title;
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

    public String getEndTime() {
        return endTime;
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

    public void setTitle(String title) {
        this.title = title;
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

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

}
