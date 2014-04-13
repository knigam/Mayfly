package com.keonasoft.mayfly.model;

/**
 * Created by kushal on 4/12/14.
 */
public class Friend {

    private int friendshipId;
    private int userId;
    private int groupId;
    private String friendName;
    private String userName;
    private String groupName;

    public Friend(){};

    public Friend(int friendshipId, int userId, int groupId, String friendName, String userName, String groupName){
        super();
        this.friendshipId = friendshipId;
        this. userId = userId;
        this.groupId = groupId;
        this.friendName = friendName;
        this.userName = userName;
        this.groupName = groupName;
    }

    public int getFriendshipId() {
        return friendshipId;
    }

    public void setFriendshipId(int friendshipId) {
        this.friendshipId = friendshipId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "friendshipId=" + friendshipId +
                ", userId=" + userId +
                ", groupId=" + groupId +
                ", friendName='" + friendName + '\'' +
                ", userName='" + userName + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
