/*
 * This file implements the class Habit
 * -------------------------------------------------------------------------------------------------
 *
 * Copyright [2021] [CMPUT301F21T38]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.example.habitassist;

import android.widget.DatePicker;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to model all information related to a specific Habit
 */
public class Habit implements Serializable {

    /** a string containing the title of the habit */
    private String title;
    /** a string containing the reason for the habit */
    private String reason;
    /** a date string in the format yyyy-MM-dd; This represents the date the habit was started on */
    private String startDate;
    /** a string of comma-separated days of the week; These are the days to do the habit */
    private String daysToBeDone;
    private String ownerUsername;
    private boolean isPublic;

    /** This is a unique timeStamp that is different for every single habit */
    private String timeStamp;

    private ArrayList<HabitEvent> habitEvents;






    /**
     * Simple constructor that expects all the attributes as arguments
     * @param title a string containing the title of the habit
     * @param reason a string containing the reason for the habit
     * @param startDate a date string in the format yyyy-MM-dd
     * @param DaysToBeDone a string of comma-separated days of the week
     * @param ownerUsername a string containing the username of the owner of this Habit
     * @param isPublic a boolean value that says if the Habit is visible to other users who follow the User
     */
    Habit(String title, String reason, String startDate, String DaysToBeDone, String ownerUsername, boolean isPublic){
        this.title = title;
        this.reason = reason;
        this.startDate = startDate;
        this.daysToBeDone = DaysToBeDone;
        this.ownerUsername = ownerUsername;
        this.isPublic = isPublic;
        this.habitEvents = new ArrayList<>();
        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    Habit(String title, String reason, String startDate, String DaysToBeDone, String ownerUsername, boolean isPublic, String timeStamp){
        this.title = title;
        this.reason = reason;
        this.startDate = startDate;
        this.daysToBeDone = DaysToBeDone;
        this.ownerUsername = ownerUsername;
        this.isPublic = isPublic;
        this.habitEvents = new ArrayList<>();
        this.timeStamp = timeStamp;
    }

    // Getters and Setters
    /**
     * Getter for the `isPublic` attribute
     * @return the isPublic attribute
     */
    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    /**
     * Getter for the `title` attribute
     * @return the title attribute
     */
    String getHabitTitle() {
        return this.title;
    }

    /**
     * Getter for the `reason` attribute
     * @return the reason attribute
     */
    String getReason() {
        return this.reason;
    }

    /**
     * Getter for the `startDate` attribute
     * @return the startDate attribute
     */
    String getStartDate() {
        return this.startDate;
    }

    /**
     * Getter for the `dayToBeDone` attribute
     * @return the daysToBeDone attribute
     */
    public String getDaysToBeDone() {
        return daysToBeDone;
    }

    /**
     * Getter for the `ownerUsername` attribute
     * @return the ownerUsername attribute
     */
    public String getOwnerUsername() {
        return ownerUsername;
    }

    /**
     * Getter for the UniqueId
     * @return the ownerUsername + * + timeStamp resulting in a unique ID for the Habit that can be used
     * to identify the Habit in the database
     */
    String getUniqueId() {
        return ownerUsername + "*" + timeStamp;
    }

    /**
     * Setter for the `dayToBeDone` attribute
     * @param daysToBeDone
     */
    public void setDaysToBeDone(String daysToBeDone) {
        this.daysToBeDone = daysToBeDone;
    }

    /**
     * Setter for the `reason` attribute
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Setter for the `title` attribute
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the `timeStamp' attribute
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Getter for the `habitEvents' attribute
     */
    public ArrayList<HabitEvent> getHabitEvents() {
        return habitEvents;
    }

    /**
     * Clears the Habit events
     */
    public void clearHabitEvents() {
        this.habitEvents.clear();
    }

    /**
     * Adds a new Habit Event to the Habit events attributes
     */
    public void addToHabitEvents(HabitEvent habitEvent) {
        this.habitEvents.add(habitEvent);
    }

    // Other utility methods

    /**
     * This method tells if the daysToBeDone attribute contains the day of the week of today's date
     * @return a boolean answering whether the habit needs to be done today
     */
    public boolean isForToday() {
        Date currentDate = Calendar.getInstance().getTime();

        try {
            // Return false if the date is in the future
            Date startDate = (new SimpleDateFormat("yyyy-MM-dd")).parse(this.startDate);
            if (startDate.after(currentDate)) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Dynamically calculate the string of the day of the week of today's date
        String currentDayOfTheWeek = (new SimpleDateFormat("EEEE")).format(currentDate);

        // Check if it is in daysToBeDone
        return this.daysToBeDone.contains(currentDayOfTheWeek);
    }

    /**
     * This static method reads the date from a given DatePicker widget and transforms it to the
     * format that we expect in the startDate attribute.
     * @param datePicker a DatePicker android widget to read from
     * @return a date string in the format yyyy-MM-dd
     */
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

    /**
     * implements a parser that collects information and returns a habit from the given data.
     * used as a way to use one function in many different lines.
     * if the data has a null value it returns null.
     */
    static public Habit parseFromDoc(Map<String, Object> data) {
        String title = (String) data.get("title");
        String reason = (String) data.get("reason");
        String startDate = (String) data.get("startDate");
        String daysToBeDone = (String) data.get("daysToBeDone");
        String ownerUsername = (String) data.get("ownerUsername");
        String timeStamp = (String) data.get("timeStamp");
        Boolean isPublic = (Boolean) data.get("isPublic");
        // Add a guard in case a wrongly structured Habit data is put into firestore
        if (title != null && reason != null && startDate != null && daysToBeDone != null && ownerUsername != null && isPublic != null) {
            Habit habit = new Habit(title, reason, startDate, daysToBeDone, ownerUsername, isPublic, timeStamp);
            return habit;
        }
        return null;
    }

    /**
     * Returns a Map representing the habit object. This is useful when adding a habit to
     * the Firestore Database
     * @return a hashmap representation of the Habit class
     */
    public HashMap<String, Object> getDocument() {
        HashMap<String, Object> habitDocument = new HashMap<>();
        habitDocument.put("title", title);
        habitDocument.put("reason", reason);
        habitDocument.put("startDate", startDate);
        habitDocument.put("daysToBeDone", daysToBeDone);
        habitDocument.put("isPublic", isPublic);
        habitDocument.put("ownerUsername", ownerUsername);
        habitDocument.put("timeStamp", timeStamp);
        return habitDocument;
    }
}
