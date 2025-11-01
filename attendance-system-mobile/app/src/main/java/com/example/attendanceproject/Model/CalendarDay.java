package com.example.attendanceproject.Model;

import java.util.Date;

public class CalendarDay {
    private int dayNumber;
    private String dayName;
    private boolean isToday;
    private Date date;

    public CalendarDay(int dayNumber, String dayName, boolean isToday, Date date) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.isToday = isToday;
        this.date = date;
    }

    public int getDayNumber() { return dayNumber; }
    public String getDayName() { return dayName; }
    public boolean isToday() { return isToday; }
    public Date getDate() { return date; }
}