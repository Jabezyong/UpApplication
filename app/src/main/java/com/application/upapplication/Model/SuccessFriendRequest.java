package com.application.upapplication.Model;

import java.util.UUID;

/**
 * Created by user on 5/1/2017.
 */

public class SuccessFriendRequest {
    private String friendId;
    private String roomId;

    public SuccessFriendRequest() {
    }

    public SuccessFriendRequest(String friendId, String roomId) {
        this.friendId = friendId;
        this.roomId = roomId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
