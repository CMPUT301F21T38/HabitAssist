package com.example.habitassist;

import java.util.Date;

public class Habit {
    private String title;
    private String reason;
    private Date startDate;

    Habit(String title, String reason, Date startDate){
        this.title = title;
        this.reason = reason;
        this.startDate = startDate;
    }

    String getHabitTitle(){return this.title;}
    String getReason(){return this.reason;}
    Date getStartDate(){return this.startDate;}

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
