package com.application.upapplication.Model;

/**
 * Created by user on 12/30/2016.
 */

public class SendFriendRequest {
    private String friendId;
    public SendFriendRequest(){};
    public SendFriendRequest(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
}
