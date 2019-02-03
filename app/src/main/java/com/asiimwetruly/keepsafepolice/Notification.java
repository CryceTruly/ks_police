package com.asiimwetruly.keepsafepolice;

/**
 * Created by Elia on 17/04/2018.
 */

public class Notification {
    private String description,district;
long reported;
    public Notification() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Notification(String description, String district, long reported) {
        this.description = description;
        this.district = district;
        this.reported = reported;
    }

    public long getReported() {
        return reported;
    }

    public void setReported(long reported) {
        this.reported = reported;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
