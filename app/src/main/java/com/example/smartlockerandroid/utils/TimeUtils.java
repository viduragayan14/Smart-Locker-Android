package com.example.smartlockerandroid.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    public static long calculateElapsedTime(String startTimestampStr, String endTimestampStr) {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

        try {
            // Parse timestamp strings into Date objects
            Date startTime = dateFormat.parse(startTimestampStr);
            Date endTime = dateFormat.parse(endTimestampStr);

            // Calculate the time difference in milliseconds
            return endTime.getTime() - startTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Indicates an error in parsing timestamps
        }
    }
}
