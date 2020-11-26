package com.codearlystudio.homedelivery.models;

public class PojoTimeSlot {
    String dateSlot, timeSlot;

    public PojoTimeSlot(String dateSlot, String timeSlot) {
        this.dateSlot = dateSlot;
        this.timeSlot = timeSlot;
    }

    public String getDateSlot() {
        return dateSlot;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

}
