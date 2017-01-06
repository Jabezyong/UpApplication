package com.application.upapplication.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.R;
import com.application.upapplication.Views.ChatActivity;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

/**
 * Created by user on 12/30/2016.
 */

public class FriendListAdapter extends BaseAdapter implements View.OnClickListener{
    private Context mContext;
    List<FriendListItem> friends;

    FirebaseStorage firebase;
    public FriendListAdapter(Context mContext, List<FriendListItem> friends) {
        this.mContext = mContext;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friends.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        LayoutInflater mInfater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        viewHolder = new ViewHolder();
        if(convertView==null){
            convertView = mInfater.inflate(R.layout.friendlist_item,null);
            viewHolder.ivProfile_pic = (ImageView) convertView.findViewById(R.id.profile_pic);
            viewHolder.tvFriend_name = (TextView) convertView.findViewById(R.id.friend_name);
            viewHolder.btnChat = (Button) convertView.findViewById(R.id.btnChat);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final FriendListItem item =  friends.get(position);
        viewHolder.tvFriend_name.setText(item.getFriend_name());
        viewHolder.ivProfile_pic.setImageBitmap(item.getBitmap());
        viewHolder.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(ChatActivity.CHATROOMID,item.getRoomId());
                bundle.putString(ChatActivity.FRIENDID,item.getProfile_id());
                bundle.putString(ChatActivity.NAME,item.getFriend_name());
                intent.putExtra(ChatActivity.BUNDLE,bundle);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    private class ViewHolder{
        ImageView ivProfile_pic;
        TextView tvFriend_name;
        Button btnChat;
    }
}
