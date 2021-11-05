package com.example.habitassist;

import android.widget.DatePicker;

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

    public boolean isForToday() {
        Date date = Calendar.getInstance().getTime();
        String currentDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(date);
        return this.DaysToBeDone.contains(currentDayOfTheWeek);
    }

    public static String dateStringFromDatePicker(DatePicker datePicker) {
        int take_day = datePicker.getDayOfMonth();
        String take_day1 = Integer.toString(take_day);
        if (take_day < 10){
            take_day1 = "0" + take_day1;
        }
        int take_month = datePicker.getMonth() + 1;
        int take_year = datePicker.getYear();

        String take_month1 = Integer.toString(take_month);
        if (take_month < 10){
            take_month1 = "0" + take_month1;
        }
        String take_year1 = Integer.toString(take_year);
        String date_Started = take_year1 + "-" + take_month1 + "-" + take_day1;
        return date_Started;
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
