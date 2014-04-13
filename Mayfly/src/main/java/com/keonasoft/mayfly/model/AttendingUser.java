package com.keonasoft.mayfly.model;

/**
 * Created by kushal on 4/12/14.
 */
public class AttendingUser {

    private int userId;
    private int eventId;
    private String userName;

    public AttendingUser() {}

    public AttendingUser(int userId, int eventId, String userName){
        super();
        this.userId = userId;
        this.eventId = eventId;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "AttendingUser{" +
                "userId=" + userId +
                ", eventId=" + eventId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
