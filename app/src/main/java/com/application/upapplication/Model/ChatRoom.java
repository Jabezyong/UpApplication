package com.application.upapplication.Model;

import java.util.UUID;

/**
 * Created by user on 5/1/2017.
 */

public class ChatRoom {
    String chatRoomId;
    String lastMessageChild;
    public  ChatRoom(){

    }
    public ChatRoom(String chatRoomId){
        this.chatRoomId = chatRoomId;
        lastMessageChild ="";
    }
    public ChatRoom(String chatRoomId, String lastMessageChild) {
        this.chatRoomId = chatRoomId;
        this.lastMessageChild = lastMessageChild;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getLastMessageChild() {
        return lastMessageChild;
    }

    public void setLastMessageChild(String lastMessageChild) {
        this.lastMessageChild = lastMessageChild;
    }
}
