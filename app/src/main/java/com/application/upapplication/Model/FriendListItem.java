package com.application.upapplication.Model;

import android.graphics.Bitmap;

/**
 * Created by user on 12/30/2016.
 */

public class FriendListItem {
    String friend_name;
    String profile_id;
    String roomId;
    Bitmap bitmap;

    public FriendListItem() {

    }

    public FriendListItem(String friend_name, String profile_id) {
        this.friend_name = friend_name;
        this.profile_id = profile_id;
    }

    public FriendListItem(String friend_name, String profile_id, Bitmap bitmap) {
        this.friend_name = friend_name;
        this.profile_id = profile_id;
        this.bitmap = bitmap;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
