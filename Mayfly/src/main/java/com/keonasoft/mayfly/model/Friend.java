package com.keonasoft.mayfly.model;

/**
 * Created by kushal on 4/12/14.
 */
public class Friend {

    private int friendId;
    private int groupId;
    private String friendName;
    private String userName;
    private String groupName;

    public Friend(){};

    public Friend(int friendId, int groupId, String friendName, String userName, String groupName){
        super();
        this. friendId = friendId;
        this.groupId = groupId;
        this.friendName = friendName;
        this.userName = userName;
        this.groupName = groupName;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
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
                ", friendId=" + friendId +
                ", groupId=" + groupId +
                ", friendName='" + friendName + '\'' +
                ", userName='" + userName + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }
}
