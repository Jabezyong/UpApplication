package com.application.upapplication.Model;

/**
 * Created by user on 11/25/2016.
 */

public class Message {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SEND = 1;
    public static final String TEXT = "TEXT";
    public static final String AUDIO = "AUDIO";
    public static final String IMAGE = "IMAGE";
    private String sender;
    private String receiver;
    private String content;
    private String file;
    private String contentType;
    private int type;
    private int date;
    private int seen;

    public Message(String content, int type){
        this.content = content;
        this.type = type;
    }
    public Message(String content){
        this.content = content;
        this.type = TYPE_SEND;
    }

    public Message(String sender, String receiver, String content,String contentType, int type, int date, int seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.contentType =contentType;
        this.type = type;
        this.date = date;
        this.seen = seen;
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

}
