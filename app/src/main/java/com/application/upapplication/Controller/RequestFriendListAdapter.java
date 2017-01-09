package com.application.upapplication.Controller;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.ChatRoom;
import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.Model.SendFriendRequest;
import com.application.upapplication.Model.SuccessFriendRequest;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.application.upapplication.Views.AccountFragment;
import com.application.upapplication.Views.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mob.commons.f;

import java.util.List;
import java.util.UUID;

/**
 * Created by user on 12/30/2016.
 */

public class RequestFriendListAdapter extends BaseAdapter{
    Context mContext;
    List<FriendListItem> friendListItems;
    public static String NEWFRIEND = "New Friends";
    public static String FRIENDLIST = "Friend List";
    public static String FRIENDREQUEST ="Friend Request";
    public static String CHATROOMLIST ="ChatRoomList";
    public static String MSSAGES = "Messages";
    String id;
    String friendId;
    UserDetails user;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        viewHolder.friendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               friendId = friendListItems.get(position).getProfile_id();
                    acceptFriendRequest(position);
            }
        });

        return convertView;
    }

    private void acceptFriendRequest(final int position) {
        SharedPreferences preferences = mContext.getSharedPreferences(MainActivity.UPPREFERENCE,Context.MODE_PRIVATE);
       id = preferences.getString(mContext.getString(R.string.ownerid),"");
        final String chatRoomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = new ChatRoom(chatRoomId);
        DatabaseReference newFriendReference = FirebaseDatabase.getInstance().getReference().child(NEWFRIEND).child(friendId);
        SendFriendRequest ownRequest = new SendFriendRequest(id);
        newFriendReference.child(id).setValue(ownRequest);
        DatabaseReference friendListReference = FirebaseDatabase.getInstance().getReference().child(FRIENDLIST).child(id);
        SuccessFriendRequest hisRequest = new SuccessFriendRequest(friendId,chatRoomId);
        friendListReference.child(friendId).setValue(hisRequest);
        friendListReference = FirebaseDatabase.getInstance().getReference().child(FRIENDLIST).child(friendId);
        hisRequest = new SuccessFriendRequest(id,chatRoomId);
        friendListReference.child(id).setValue(hisRequest);
        FirebaseDatabase.getInstance().getReference().child(CHATROOMLIST).child(chatRoomId).setValue(chatRoom);

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users").child(friendId);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    user = dataSnapshot.getValue(UserDetails.class);
                    executeQuery(user,chatRoomId);
                    saveImageToDatabase(user,position);
                    friendListItems.remove(position);
                    notifyDataSetChanged();
                    FirebaseDatabase.getInstance().getReference().child(FRIENDREQUEST).child(id).child(friendId).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(friendListItems.size() <=0){

        }
    }

    private void executeQuery(UserDetails user,String roomId) {
        ContentValues values = new ContentValues();
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(mContext);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        values.put(UpDatabaseHelper.ID_COLUMN,user.getId());
        values.put(UpDatabaseHelper.FIRST_NAME_COLUMN,user.getFirstName());
        values.put(UpDatabaseHelper.LAST_NAME_COLUMN,user.getLastName());
        values.put(UpDatabaseHelper.GENDER_COLUMN,user.getGender());
        values.put(UpDatabaseHelper.FACEBOOK_ID_COLUMN,user.getId());
        values.put(UpDatabaseHelper.PROFILE_PHOTO_COLUMN,user.getPhoto());
        values.put(UpDatabaseHelper.COURSE_COLUMN,user.getCourse());
        values.put(UpDatabaseHelper.ACADEMIC_YEAR_COLUMN,user.getAcademicYear());
        values.put(UpDatabaseHelper.ABOUT_ME_COLUMN,user.getAboutMe());
        values.put(UpDatabaseHelper.DOB_COLUMN,user.getBirthday());
        values.put(UpDatabaseHelper.PHONE_COLUMN,user.getPhoneNumber());
        values.put(UpDatabaseHelper.VERIFIED_COLUMN,user.getIsVerified());
        values.put(UpDatabaseHelper.AGE_COLUMN,user.getAge());
        values.put(UpDatabaseHelper.INTEREST_1_COLUMN,user.getInterest1());
        values.put(UpDatabaseHelper.INTEREST_2_COLUMN,user.getInterest2());
        values.put(UpDatabaseHelper.INTEREST_3_COLUMN,user.getInterest3());
        values.put(UpDatabaseHelper.TARGET_MALE_COLUMN,user.getTargetMale());
        values.put(UpDatabaseHelper.TARGET_FEMALE_COLUMN,user.getTargetFemale());
        values.put(UpDatabaseHelper.LAST_LOGIN_COLUMN,user.getLastLogin().toString());
        db.insert(UpDatabaseHelper.USER_TABLE,null,values);

        ContentValues newValues = new ContentValues();
        newValues.put(UpDatabaseHelper.FRIEND_ID_COLUMN,user.getId());
        newValues.put(UpDatabaseHelper.FIRST_NAME_COLUMN,user.getFirstName());
        newValues.put(UpDatabaseHelper.LAST_NAME_COLUMN,user.getLastName());
        newValues.put(UpDatabaseHelper.CHATROOM_ID_COLUMN,roomId);
        db.insert(UpDatabaseHelper.FRIENDSHIP_TABLE,null,newValues);
        db.close();
    }

    private void saveImageToDatabase(UserDetails user,int position){
        ContentValues values = new ContentValues();
        Bitmap bitmap = friendListItems.get(position).getBitmap();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN,user.getId());
        values.put(UpDatabaseHelper.IMAGE_COLUMN, AccountFragment.getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(mContext);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.insert(UpDatabaseHelper.IMAGES_TABLE,null,values);
        db.close();
    }

    public void updateList(List<FriendListItem> friendListItems){
        this.friendListItems = friendListItems;
    }
    private class ViewHolder{
        TextView friendName;
        ImageView friendImage;
        Button btnAccept;
        Button btnReject;

    }
}
