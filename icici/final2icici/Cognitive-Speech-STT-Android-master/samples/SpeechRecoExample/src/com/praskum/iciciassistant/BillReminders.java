package com.praskum.iciciassistant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by praskum on 4/16/2017.
 */

public class BillReminders {
    public static List<String> remindersList = new ArrayList<String>();
    public static String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static List<String> getRemindersList() {
        return remindersList;
    }

    public static void addReminder(String rem) {
        remindersList.add(rem);
    }
}
