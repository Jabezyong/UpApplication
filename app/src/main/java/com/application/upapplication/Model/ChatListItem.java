package com.application.upapplication.Model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by user on 12/23/2016.
 */

public class ChatListItem {
    private String friend_name;
    private Bitmap profile_pic;
    private String lastMsg;
    private String lastMsgKey;
    private Date time;
    private String chatroomId;
    private String profile_id;
    private String position ;
    public ChatListItem() {
    }



    public ChatListItem(String profile_id,String friend_name, Bitmap profile_pic, String lastMsg, String chatroomId) {
        this.profile_id = profile_id;
        this.friend_name = friend_name;
        this.profile_pic = profile_pic;
        this.lastMsg = lastMsg;
        this.chatroomId = chatroomId;
    }

    public String getLastMsgKey() {
        return lastMsgKey;
    }

    public void setLastMsgKey(String lastMsgKey) {
        this.lastMsgKey = lastMsgKey;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }
    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }

    public Bitmap getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Bitmap profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
