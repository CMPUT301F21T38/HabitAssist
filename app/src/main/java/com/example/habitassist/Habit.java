package com.example.habitassist;

import java.util.List;

public class Habit {
    private String title;
    private String reason;
    private long startDate;
    private List<String> DaysToBeDone;

    Habit(String title, String reason, long startDate, List<String> DaysToBeDone){
        this.title = title;
        this.reason = reason;
        this.startDate = startDate;
        this.DaysToBeDone = DaysToBeDone;
    }

    String getHabitTitle() {return this.title;}
    String getReason(){return this.reason;}
    long getStartDate(){return this.startDate;}

    public List<String> getDaysToBeDone() {
        return DaysToBeDone;
    }

    public void setDaysToBeDone(List<String> daysToBeDone) {
        DaysToBeDone = daysToBeDone;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
