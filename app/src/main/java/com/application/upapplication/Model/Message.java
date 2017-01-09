package com.application.upapplication.Model;

import java.util.Date;

/**
 * Created by user on 11/25/2016.
 */

public class Message {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;
    public static final int TEXT = 0;
    public static final int AUDIO = 1;
    public static final int IMAGE =  2;
    private String messageId;
    private String roomId;
    private String sender;
    private String receiver;
    private String content;
    private String file;
    private int contentType;
    private int deliverType;
    private Date date;
    private int seen;

    public Message() {
    }

    public Message(String messageId, String sender , String receiver, String content, int contentType){
        this.messageId = messageId;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.contentType = contentType;
        this.date = new Date();
    }

    public Message(String sender ,String receiver,String content, int contentType){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.contentType = contentType;
        this.date = new Date();
    }
    public Message(String messageId,String content,int deliverType){
        this.messageId = messageId;
        this.content = content;
        this.deliverType = deliverType;
    }
    public Message(String content,int deliverType){
        this.content = content;
        this.deliverType = deliverType;
    }

    public Message(String sender, String receiver, String content,int contentType, int deliverType, int seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.contentType =contentType;
        this.deliverType = deliverType;
        this.seen = seen;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public static int getTypeReceived() {
        return TYPE_RECEIVED;
    }

    public static int getTypeSend() {
        return TYPE_SEND;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getDeliverType() {
        return deliverType;
    }

    public void setDeliverType(int deliverType) {
        this.deliverType = deliverType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

}
