package com.application.upapplication.Controller;

import com.application.upapplication.Model.ChatListItem;

import java.util.Comparator;

/**
 * Created by user on 6/1/2017.
 */

public class CustomComparator implements Comparator<ChatListItem> {
    @Override
    public int compare(ChatListItem o1, ChatListItem o2) {
        return o2.getTime().compareTo(o1.getTime());
    }
}
