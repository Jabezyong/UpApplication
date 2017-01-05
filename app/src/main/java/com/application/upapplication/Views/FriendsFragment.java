package com.application.upapplication.Views;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.application.upapplication.Controller.FriendListAdapter;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by user on 12/30/2016.
 */

public class FriendsFragment extends Fragment {
    RelativeLayout relativeLayout;
    ListView listView;
    FriendListAdapter friendListAdapter;
    List<FriendListItem> friendListItems;
    UpDatabaseHelper databaseHelper;
    View view;
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

    private void readDataFromDatabase() {
        databaseHelper = new UpDatabaseHelper(getContext());
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

        if(friendCursor.getCount() >0){
            new myTask().execute(friendCursor);
        }
//        friendListAdapter.notifyDataSetChanged();
    }

    private class myTask extends AsyncTask<Cursor,Void,Void>{

        @Override
        protected Void doInBackground(Cursor... params) {
            Cursor friendCursor = params[0];
            friendCursor.moveToFirst();
            while(!friendCursor.isLast()){
                String id = friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.FRIEND_ID_COLUMN));
                String firstName = friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.FIRST_NAME_COLUMN));
                String lastName =  friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.LAST_NAME_COLUMN));
                String fullName = firstName +" "+lastName;
                String roomId = friendCursor.getString(friendCursor.getColumnIndexOrThrow(UpDatabaseHelper.CHATROOM_ID_COLUMN));
                Bitmap bitmap = getImage(databaseHelper.getProfilePic(id));
                FriendListItem item = new FriendListItem(fullName,roomId,bitmap);
                friendListItems.add(item);
                friendCursor.moveToNext();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            friendListAdapter.notifyDataSetChanged();
            listView.setAdapter(friendListAdapter);
        }
    }
    public Bitmap getImage(byte[] data){
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }
}
