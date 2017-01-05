package com.application.upapplication.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;


import com.application.upapplication.Controller.RequestFriendListAdapter;
import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.Model.SendFriendRequest;
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

import java.util.ArrayList;
import java.util.List;

public class RequestFriendActivity extends AppCompatActivity {
    private int totalCount = 0;
    private int currentCount = 0;
    ProgressDialog dialog;
    String friendTree ="Friend Request";
    ListView requestFriendList;
    List<FriendListItem> friendListItems;
    String ownerId;
    RequestFriendListAdapter adapter;
    static int ONE_MEGABYTE = 1024*1024;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friend);
        setTitle("New Friend Request");
        initView();
    }

    private void initView(){
        dialog = new ProgressDialog(RequestFriendActivity.this);
        dialog.setMessage("Loading");
        dialog.setCancelable(true);
        dialog.show();
        requestFriendList = (ListView) findViewById(R.id.requestfriendlistview);
        friendListItems = new ArrayList<>();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(MainActivity.UPPREFERENCE, Context.MODE_PRIVATE);
        ownerId = preferences.getString(getString(R.string.ownerid),"");
        getFriendRequest();
    }
    private void getFriendRequest(){

        DatabaseReference friendReferece = FirebaseDatabase.getInstance().getReference().child(friendTree).child(ownerId);
        friendReferece.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for(DataSnapshot data:children){
                        ++totalCount;
                        addIntoList(data);
                    }

                }else{
                    showMessageDialog();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addIntoList(final DataSnapshot data) {
        SendFriendRequest hello = data.getValue(SendFriendRequest.class);
        String id = hello.getFriendId();
        final FriendListItem item = new FriendListItem();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot !=null){
                    UserDetails user = dataSnapshot.getValue(UserDetails.class);
                    item.setFriend_name(user.getFirstName() + " " +user.getLastName());
                    item.setProfile_id(user.getId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filepath = mStorage.child("UserPhotos").child(id+".png");
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                item.setBitmap(getImage(bytes));
                friendListItems.add(item);
//                if(adapter !=null){
//                    adapter.notifyDataSetChanged();
                    ++currentCount;
                    if(currentCount == totalCount){
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        adapter = new RequestFriendListAdapter(getApplicationContext(),friendListItems);
                        requestFriendList.setAdapter(adapter);
                        totalCount = 0;
                        currentCount = 0;

                    }
//                }
            }
        });
        filepath.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Cannot retrieve photo in this moment",Toast.LENGTH_LONG).show();
                if(dialog.isShowing()){
                    dialog.dismiss();
                };
            }
        });
        Log.d(getPackageName(),"Hello");
    }

    private void showMessageDialog() {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have no new friend request.");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private Bitmap getImage(byte[] data){
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }

}
