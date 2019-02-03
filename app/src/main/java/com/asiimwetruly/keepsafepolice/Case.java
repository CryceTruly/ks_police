package com.asiimwetruly.keepsafepolice;

public class Case {
    private String phone,id,happened,reportedFrom,description,district,province,month,reported_by,user;
long reported;
    public Case() {
    }

    public void setReported(long reported) {
        this.reported = reported;
    }

    public Case(String phone, String id, String happened, String reportedFrom,
                String description, String district, String province, String month, String reported_by, String user, long reported) {
        this.phone = phone;
        this.id = id;
        this.happened = happened;
        this.reportedFrom = reportedFrom;
        this.description = description;
        this.district = district;
        this.province = province;
        this.month = month;
        this.reported_by = reported_by;
        this.user = user;
        this.reported = reported;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHappened() {
        return happened;
    }

    public void setHappened(String happened) {
        this.happened = happened;
    }

    public String getReportedFrom() {
        return reportedFrom;
    }

    public void setReportedFrom(String reportedFrom) {
        this.reportedFrom = reportedFrom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistrict() {
        return district;
    }

    public long getReported() {
        return reported;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getReported_by() {
        return reported_by;
    }

    public void setReported_by(String reported_by) {
        this.reported_by = reported_by;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


}
