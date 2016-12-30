package com.application.upapplication.Controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.R;

import java.util.List;

/**
 * Created by user on 12/30/2016.
 */

public class RequestFriendListAdapter extends BaseAdapter{
    Context mContext;
    List<FriendListItem> friendListItems;

    public RequestFriendListAdapter(Context context, List<FriendListItem> friendListItems) {
        this.mContext = context;
        this.friendListItems = friendListItems;
    }

    @Override
    public int getCount() {
        return friendListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return friendListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friendListItems.indexOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        LayoutInflater mInfater = (LayoutInflater)
                mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = mInfater.inflate(R.layout.requestfriendlist_item,null);
            viewHolder.btnAccept = (Button) convertView.findViewById(R.id.btnAccept);
            viewHolder.btnReject = (Button) convertView.findViewById(R.id.btnReject);
            viewHolder.friendName = (TextView) convertView.findViewById(R.id.friend_name);
            viewHolder.friendImage = (ImageView) convertView.findViewById(R.id.profile_pic);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FriendListItem item = friendListItems.get(position);
        viewHolder.friendName.setText(item.getFriend_name());
        viewHolder.friendImage.setImageBitmap(item.getBitmap());
        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }



    private class ViewHolder{
        TextView friendName;
        ImageView friendImage;
        Button btnAccept;
        Button btnReject;

    }
}
