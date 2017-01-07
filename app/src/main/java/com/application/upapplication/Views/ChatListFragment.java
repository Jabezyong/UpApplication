package com.application.upapplication.Views;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.application.upapplication.Controller.ChatListAdapter;
import com.application.upapplication.Controller.CustomComparator;
import com.application.upapplication.Controller.RequestFriendListAdapter;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.ChatListItem;
import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.Model.Message;
import com.application.upapplication.Model.SuccessFriendRequest;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.widget.AdapterView.OnItemClickListener;

/**
 * Created by user on 12/23/2016.
 */

public class ChatListFragment extends Fragment implements OnItemClickListener {
    ListView chatList;
    View view;
    String[] friend_names;
    TypedArray profile_pics;
    String[] lastMsg;
    String[] time;
    String ownerId;
    static List<ChatListItem> chatListItems;
    static  ChatListAdapter adapter;
    ProgressDialog progressDialog;
    UpDatabaseHelper databaseHelper;
    public static ChatListFragment newInstance() {
        ChatListFragment chatListFragment = new ChatListFragment();
        Bundle extraArguments = new Bundle();
        chatListFragment.setArguments(extraArguments);
        return chatListFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chatlist,null);
        init();
        return view;
    }

    private void init() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Message");
        progressDialog.setCancelable(false);
        progressDialog.show();
        chatList= (ListView) view.findViewById(R.id.chatlist);

        chatListItems = new ArrayList<ChatListItem>();

        friend_names = getResources().getStringArray(R.array.friend_names);
        lastMsg = getResources().getStringArray(R.array.lastmessage);
        time = getResources().getStringArray(R.array.contact);
        profile_pics = getResources().obtainTypedArray(R.array.profile_pic);

        adapter = new ChatListAdapter(getContext(), chatListItems);
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.UPPREFERENCE, Context.MODE_PRIVATE);
        ownerId = preferences.getString(getString(R.string.ownerid),"");
        chatList.setAdapter(adapter);
        profile_pics.recycle();;

        initFirebase();
    }
    private void initFirebase(){
        DatabaseReference child = FirebaseDatabase.getInstance().getReference().child("Friend List").child(ownerId);
        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chatList.setOnItemClickListener(ChatListFragment.this);
                    final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        final DataSnapshot next = iterator.next();
                        SuccessFriendRequest value = next.getValue(SuccessFriendRequest.class);
                        String roomId = value.getRoomId();
                            DatabaseReference messages = FirebaseDatabase.getInstance().getReference().child("MESSAGES").child(roomId);
                        messages.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Iterator<DataSnapshot> iterator1 = dataSnapshot.getChildren().iterator();
                                    while(iterator1.hasNext()){
                                        DataSnapshot next1 = iterator1.next();
                                        Message value1 = next1.getValue(Message.class);
                                        String content = value1.getContent();
                                        readDataFromDatabase(value1);
                                    }

                                }else{
                                    if(progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    getActivity().setTitle("Empty Message");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(),ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.CHATROOMID,chatListItems.get(position).getChatroomId());
        bundle.putString(ChatActivity.FRIENDID,chatListItems.get(position).getProfile_id());
        bundle.putString(ChatActivity.NAME,chatListItems.get(position).getFriend_name());
        intent.putExtra(ChatActivity.BUNDLE,bundle);
        startActivity(intent);
//        Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();
    }
    public static void updateUI(String friendId,Message msg){
        for(int i=0;i<chatListItems.size();i++){
            if(chatListItems.get(i).getProfile_id().equals(friendId)){
                chatListItems.get(i).setLastMsg(msg.getContent());
                chatListItems.get(i).setTime(msg.getDate());
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void readDataFromDatabase(final Message msg) {
        databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        String content = msg.getContent();
        String friendId ="";
        String roomId  = msg.getRoomId();
        Date date = msg.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String time1 = sdf.format(date);
        if(msg.getReceiver().equals(ownerId)){
            friendId = msg.getSender();
        }else {
            friendId = msg.getReceiver();
        }
        final UserDetails friend = databaseHelper.readUser(friendId);
        if(friend != null) {
            byte[] data = databaseHelper.getProfilePic(friendId);
            if(data != null) {
                Bitmap bitmap = getImage(data);

                if (friend != null && bitmap != null) {
                    String fullName = friend.getFirstName() + " " + friend.getLastName();
                    ChatListItem item = new ChatListItem(friendId, fullName, bitmap, content, roomId);
                    item.setTime(date);
                    chatListItems.add(0, item);
                    Collections.sort(chatListItems, new CustomComparator());
                    adapter.notifyDataSetChanged();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }else{
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("UserPhotos").child(friend.getId() + ".png");
                int ONE_MEGABYTE = 1024 * 1024;
                filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = AccountFragment.getImage(bytes);
                        saveImageToDatabase(friend, bitmap,msg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
        }else{
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
    private void saveImageToDatabase(UserDetails user, Bitmap bitmap,Message msg) {
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN, user.getId());
        values.put(UpDatabaseHelper.IMAGE_COLUMN, AccountFragment.getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.insert(UpDatabaseHelper.IMAGES_TABLE, null, values);
        db.close();
        readDataFromDatabase(msg);
//        String fullName = user.getFirstName() + " " + user.getLastName();
//        String friendId  = "";
//        if(msg.getReceiver().equals(ownerId)){
//            friendId = msg.getSender();
//        }else {
//            friendId = msg.getReceiver();
//        }
//        String content = msg.getContent();
//        String roomId = msg.getRoomId();
//        Date date = msg.getDate();
//        ChatListItem item = new ChatListItem(friendId, fullName, bitmap, content, roomId);
//        item.setTime(date);
//        chatListItems.add(0, item);
//        Collections.sort(chatListItems, new CustomComparator());
//        adapter.notifyDataSetChanged();
//        if (progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }


    }
    public Bitmap getImage(byte[] data){
        if(data == null){
            return null;
        }
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }
}

