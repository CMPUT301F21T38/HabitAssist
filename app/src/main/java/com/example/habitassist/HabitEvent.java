package com.example.habitassist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class HabitEvent implements Serializable {
    private String comment;
    /** This is the unique title of the Habit that this HabitEvent belongs to  */
    private String habitTitle;
    private String timeStamp;
    private String imageBitmapString;

    // Constructors
    HabitEvent(String habitTitle, String timeStamp, String comment, String imageBitmapString) {
        this.habitTitle = habitTitle;
        this.timeStamp = timeStamp;
        this.comment = comment;
        this.imageBitmapString = imageBitmapString;
    }

    // Getters and Setters

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

    public String getImageBitmapString() {
        return imageBitmapString;
    }

    public void setImageBitmapString(String imageBitmapString) {
        this.imageBitmapString = imageBitmapString;
    }

    // Utility methods

    public HashMap<String, String> getDocument() {
        HashMap<String, String> habitEventDocument = new HashMap<>();
        habitEventDocument.put("comment", comment);
        habitEventDocument.put("habitTitle", habitTitle);
        habitEventDocument.put("timeStamp", timeStamp);
        habitEventDocument.put("imageBitmapString", imageBitmapString);
        habitEventDocument.put("dateString", timeStamp.split(" ")[0]);
        return habitEventDocument;
    }

    String getUniqueId() {
        return habitTitle + "*" + timeStamp;
    }

    // Static utility methods

    // Source:  https://stackoverflow.com/a/18052269/8270982
    // String to Bitmap :
    static public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    // Source:  https://stackoverflow.com/a/18052269/8270982
    // Bitmap to String :
    static public String bitMapToString(Bitmap bitmap, boolean shouldCompress) {
        if (shouldCompress) {
            bitmap = resizeBitmapIfTooLarge(bitmap, 300);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    // Source: https://stackoverflow.com/a/15759464/8270982
    static public Bitmap resizeBitmapIfTooLarge(Bitmap image, int maxHeightOrWidth) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Return unaltered image if it is smaller than maxHeightOrWidth anyways
        if (width <= maxHeightOrWidth && height <= maxHeightOrWidth) {
            return image;
        }

        // Get new target width and height while preserving aspect ratio
        float aspectRatio = (float) width / (float) height;
        if (aspectRatio > 1) {
            width = maxHeightOrWidth;
            height = (int) (width / aspectRatio);
        } else {
            height = maxHeightOrWidth;
            width = (int) (height * aspectRatio);
        }

        // Return resized bitmap
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
