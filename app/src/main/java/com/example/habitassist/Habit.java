package com.example.habitassist;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Habit implements Serializable {
    private String title;
    private String reason;
    private String startDate;
    private String DaysToBeDone;

    Habit(String title, String reason, String startDate, String DaysToBeDone){
        this.title = title;
        this.reason = reason;
        this.startDate = startDate;
        this.DaysToBeDone = DaysToBeDone;
    }

    String getHabitTitle() {return this.title;}
    String getReason(){return this.reason;}
    String getStartDate(){return this.startDate;}

    public String getDaysToBeDone() {
        return DaysToBeDone;
    }

    public void setDaysToBeDone(String daysToBeDone) {
        DaysToBeDone = daysToBeDone;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //public void setStartDate(String startDate) {this.startDate = startDate;}

    public boolean isForToday() {
        Date date = Calendar.getInstance().getTime();
        String currentDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(date);
        return this.DaysToBeDone.contains(currentDayOfTheWeek);
    }

    public HashMap<String, String> getDocument() {
        HashMap<String, String> habitDocument = new HashMap<>();
        habitDocument.put("title", title);
        habitDocument.put("reason", reason);
        habitDocument.put("startDate", startDate);
        habitDocument.put("daysToBeDone", DaysToBeDone);
        return habitDocument;
    }
}
