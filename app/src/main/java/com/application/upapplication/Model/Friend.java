package com.application.upapplication.Model;

/**
 * Created by user on 12/30/2016.
 */

public class Friend {
    private String ownerId;
    private String friendId;
    private int isProved;
    private String roomId;
    public Friend(){}
    public Friend(String ownerId, String friendId, int isProved, String roomId) {
        this.ownerId = ownerId;
        this.friendId = friendId;
        this.isProved = isProved;
        this.roomId = roomId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public int getIsProved() {
        return isProved;
    }

    public void setIsProved(int isProved) {
        this.isProved = isProved;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
