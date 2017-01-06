package com.application.upapplication.Controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.application.upapplication.Model.ChatListItem;
import com.application.upapplication.R;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 12/23/2016.
 */

public class ChatListAdapter extends BaseAdapter {

    Context context;
    List<ChatListItem> chatListItems;

    public ChatListAdapter(Context context, List<ChatListItem> items){
        this.context = context;
        chatListItems = items;
    }
    @Override
    public int getCount() {
        return chatListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return chatListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return chatListItems.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder = null;
        LayoutInflater mInfater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if(convertView==null){
            convertView = mInfater.inflate(R.layout.chatlist_item,null);
            holder.ivProfile_pic = (ImageView) convertView.findViewById(R.id.profile_pic);
            holder.tvFriend_name = (TextView) convertView.findViewById(R.id.friend_name);
            holder.tvLastMessage = (TextView) convertView.findViewById(R.id.last_message);
            holder.tvTime = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        ChatListItem row_pos = chatListItems.get(position);

        holder.ivProfile_pic.setImageBitmap(row_pos.getProfile_pic());
        holder.tvFriend_name.setText(row_pos.getFriend_name());
        holder.tvLastMessage.setText(row_pos.getLastMsg());
        Date time = row_pos.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String timeString = sdf.format(time);
        holder.tvTime.setText(timeString);

        return convertView;
    }


    private class ViewHolder{
        ImageView ivProfile_pic;
        TextView tvFriend_name;
        TextView tvLastMessage;
        TextView tvTime;


    }
}
