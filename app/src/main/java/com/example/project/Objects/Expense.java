package com.example.project.Objects;

import java.sql.Timestamp;

public class Expense {

    private String key = "";
    private String title = "";
    private int amount = 0;
    private String date = "";
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private boolean repeat = false;

    public Expense() { }

    public Expense(String title, int amount, String date, boolean repeat) {
        this.key = new Timestamp(System.currentTimeMillis()).getTime() + "";
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.repeat = repeat;
        getDate(date);
    }

    private void getDate(String date) {
        String[] tempDate = date.split("/");
        this.day = Integer.parseInt(tempDate[0]);
        this.month = Integer.parseInt(tempDate[1]);
        this.year = Integer.parseInt(tempDate[2]);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }


    public int compareTo(Expense another) {
        if (this.getDay() < another.getDay()) return -1;
        else return 1;
    }

    @Override
    public String toString() {
        return  date + ", " +
                title + ", " +
                amount + "$" ;
    }
}
