package com.application.upapplication.Views;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.application.upapplication.Controller.FriendListAdapter;
import com.application.upapplication.Controller.NameComparator;
import com.application.upapplication.Controller.RequestFriendListAdapter;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.Model.RequestFriendItem;
import com.application.upapplication.Model.SuccessFriendRequest;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.bitmap;
import static android.R.attr.data;


/**
 * Created by user on 12/30/2016.
 */

public class FriendsFragment extends Fragment {
    RelativeLayout relativeLayout;
    ListView listView;
    FriendListAdapter friendListAdapter;
    List<FriendListItem> friendListItems;
    UpDatabaseHelper databaseHelper;
    DatabaseReference reference ;
    View view;
    String ownerId;
    String friendsKey;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friendlist,null);
        init();
        return view;
    }

    public void checkNewFriend(){
        Intent intent = new Intent(getContext(), RequestFriendActivity.class);
        startActivity(intent);
    }

    private void init(){
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.UPPREFERENCE, Context.MODE_PRIVATE);
        ownerId = preferences.getString(getString(R.string.ownerid),"");
        relativeLayout = (RelativeLayout) view.findViewById(R.id.requestfriendlist);
        listView = (ListView) view.findViewById(R.id.listViewFriends);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNewFriend();
            }
        });
        friendListItems = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getContext(),friendListItems);

        readDataFromDatabase();
    }
    private Cursor getFriendQuery(){
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        Cursor friendCursor = readableDatabase.query(
                UpDatabaseHelper.FRIENDSHIP_TABLE,
                new String[]{
                        UpDatabaseHelper.FRIEND_ID_COLUMN,
                        UpDatabaseHelper.FIRST_NAME_COLUMN,
                        UpDatabaseHelper.LAST_NAME_COLUMN,
                        UpDatabaseHelper.CHATROOM_ID_COLUMN
                },
                null,
                null,
                null,
                null,
                UpDatabaseHelper.FIRST_NAME_COLUMN +" DESC"
        );
        return friendCursor;
    }
    private void readDataFromDatabase() {
        databaseHelper = new UpDatabaseHelper(getContext());

        Cursor friendCursor = getFriendQuery();

        if(friendCursor.getCount() >0){
            new myTask().execute(friendCursor);
        }else{
            readFromFirebase();
        }
//        friendListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private void readFromFirebase() {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RequestFriendListAdapter.FRIENDLIST).child(ownerId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null ){
                    final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        final DataSnapshot next = iterator.next();
                        SuccessFriendRequest request = next.getValue(SuccessFriendRequest.class);
                        final String friendId = request.getFriendId();
                        final String roomId = request.getRoomId();
                        friendsKey = friendId;
                        FirebaseDatabase.getInstance().getReference().child("users").child(friendId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                                        executeQuery(userDetails, roomId);
                                        downloadBitmap(userDetails);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void downloadBitmap(final UserDetails userDetails) {
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("UserPhotos").child(userDetails.getId() + ".png");
        String photo = filepath.getDownloadUrl().toString();
        int ONE_MEGABYTE = 1024*1024;
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = AccountFragment.getImage(bytes);
                saveImageToDatabase(userDetails,bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(),"Cant download profile photo",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void executeQuery(UserDetails user,String roomId) {
        ContentValues values = new ContentValues();
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
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
    private void saveImageToDatabase(UserDetails user,Bitmap bitmap){
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN,user.getId());
        values.put(UpDatabaseHelper.IMAGE_COLUMN, AccountFragment.getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.insert(UpDatabaseHelper.IMAGES_TABLE,null,values);
        String fullName = user.getFirstName()+" "+user.getLastName();
        FriendListItem item = new FriendListItem(fullName,user.getId(),bitmap);
        friendListItems.add(item);
        friendListAdapter.notifyDataSetChanged();
        db.close();
    }


    private class myTask extends AsyncTask<Cursor,Void,Void>{

        @Override
        protected Void doInBackground(Cursor... params) {
            Cursor friendCursor = params[0];
            friendCursor.moveToFirst();
            for(int i=0;i<friendCursor.getCount();i++){

                String id = friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.FRIEND_ID_COLUMN));
                String firstName = friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.FIRST_NAME_COLUMN));
                String lastName =  friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.LAST_NAME_COLUMN));
                String fullName = firstName +" "+lastName;
                String roomId = friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.CHATROOM_ID_COLUMN));
                Bitmap bitmap = getImage(databaseHelper.getProfilePic(id));
                FriendListItem item = new FriendListItem(fullName,id,bitmap);
                item.setRoomId(roomId);
                friendsKey = id;
                friendListItems.add(item);
                friendCursor.moveToNext();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            friendListAdapter.notifyDataSetChanged();
            Collections.sort(friendListItems,new NameComparator());
            listView.setAdapter(friendListAdapter);
            listenToNewFriends();
        }
    }
    private void listenToNewFriends(){
        reference= FirebaseDatabase.getInstance().getReference().child(RequestFriendListAdapter.FRIENDLIST).child(ownerId);
        reference.startAt(friendsKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                friendListItems = new ArrayList<>();
                friendListAdapter = new FriendListAdapter(getContext(),friendListItems);
                listView.setAdapter(friendListAdapter);
                new myTask().execute(getFriendQuery());
//                final String friendId = request.getFriendId();
//                final String roomId = request.getRoomId();
//                FirebaseDatabase.getInstance().getReference().child("users").child(friendId)
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
//                                executeQuery(userDetails, roomId);
//                                downloadBitmap(userDetails);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public Bitmap getImage(byte[] data){
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }
}
