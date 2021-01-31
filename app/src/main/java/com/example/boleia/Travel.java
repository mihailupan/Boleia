package com.example.boleia;

public class Travel {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String from;
    private String to;
    private String date;
    private String time;
    private String meetingPointLat;
    private String meetingPointLng;
    private String vehicleBrand;
    private String vehicleModel;
    private String vehicleLicencePlate;
    private String vehiclePhotoName;

    public Travel(String userId, String name, String email, String phone,
                  String from, String to, String date, String time,
                  String meetingPointLat, String meetingPointLng,
                  String vehicleBrand, String vehicleModel,
                  String vehicleLicencePlate, String vehiclePhotoName) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.meetingPointLat = meetingPointLat;
        this.meetingPointLng = meetingPointLng;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.vehicleLicencePlate = vehicleLicencePlate;
        this.vehiclePhotoName = vehiclePhotoName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMeetingPointLat() {
        return meetingPointLat;
    }

    public void setMeetingPointLat(String meetingPointLat) {
        this.meetingPointLat = meetingPointLat;
    }

    public String getMeetingPointLng() {
        return meetingPointLng;
    }

    public void setMeetingPointLng(String meetingPointLng) {
        this.meetingPointLng = meetingPointLng;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleLicencePlate() {
        return vehicleLicencePlate;
    }

    public void setVehicleLicencePlate(String vehicleLicencePlate) {
        this.vehicleLicencePlate = vehicleLicencePlate;
    }

    public String getVehiclePhotoName() {
        return vehiclePhotoName;
    }

    public void setVehiclePhotoName(String vehiclePhotoName) {
        this.vehiclePhotoName = vehiclePhotoName;
    }
}
