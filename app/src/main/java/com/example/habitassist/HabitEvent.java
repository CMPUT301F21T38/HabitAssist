package com.example.habitassist;

import java.io.Serializable;
import java.util.HashMap;

public class HabitEvent implements Serializable {
    private String comment;
    /** This is the unique title of the Habit that this HabitEvent belongs to  */
    private String habitTitle;
    private String timeStamp;

    HabitEvent(String habitTitle, String timeStamp, String comment) {
        this.habitTitle = habitTitle;
        this.timeStamp = timeStamp;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getHabitTitle() {
        return habitTitle;
    }

    public void setHabitTitle(String habitTitle) {
        this.habitTitle = habitTitle;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public HashMap<String, String> getDocument() {
        HashMap<String, String> habitEventDocument = new HashMap<>();
        habitEventDocument.put("comment", comment);
        habitEventDocument.put("habitTitle", habitTitle);
        habitEventDocument.put("timeStamp", timeStamp);
        return habitEventDocument;
    }

    String getUniqueId() {
        return habitTitle + "*" + timeStamp;
    }
}
