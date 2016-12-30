package com.application.upapplication.Model;

/**
 * Created by user on 12/23/2016.
 */

public class ChatListItem {
    private String friend_name;
    private int profile_pic_id;
    private String lastMsg;
    private String time;

    public ChatListItem(String friend_name, int profile_pic_id, String lastMsg, String time) {
        this.friend_name = friend_name;
        this.profile_pic_id = profile_pic_id;
        this.lastMsg = lastMsg;
        this.time = time;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public int getProfile_pic_id() {
        return profile_pic_id;
    }

    public void setProfile_pic_id(int profile_pic_id) {
        this.profile_pic_id = profile_pic_id;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
