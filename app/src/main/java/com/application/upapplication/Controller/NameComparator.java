package com.application.upapplication.Controller;

import com.application.upapplication.Model.FriendListItem;

import java.util.Comparator;

/**
 * Created by user on 9/1/2017.
 */

public class NameComparator implements Comparator<FriendListItem> {
    @Override
    public int compare(FriendListItem o1, FriendListItem o2) {
        return o1.getFriend_name().compareTo(o2.getFriend_name());
    }
}
