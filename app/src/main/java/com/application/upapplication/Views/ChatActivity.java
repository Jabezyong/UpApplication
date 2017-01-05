package com.application.upapplication.Views;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.application.upapplication.Controller.DatabaseHelper;
import com.application.upapplication.Controller.MsgAdapter;
import com.application.upapplication.Controller.MyFirebaseInstanceIDService;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.Message;
import com.application.upapplication.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    public static String BUNDLE = "com.application.upapplication.BUNDLE";
    public static String CHATROOM = "CHATROOM";
    public static String CHATROOMID =  "CHATROOMID";
    public static String FRIENDID = " FRIENDID";
    private final String MESSAGE = " MESSAGES";
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private String lastMsgKey;
    String ownerId ;
    String friendId;
    String roomId;
    private String chat_msg,chat_sender;
    private List<Message> msgList = new ArrayList<Message>();
    private DatabaseReference messageReference ;
    MyFirebaseInstanceIDService serve;

    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        SharedPreferences preferences = getSharedPreferences(MainActivity.UPPREFERENCE, Context.MODE_PRIVATE);
        ownerId = preferences.getString(getString(R.string.ownerid),"");
        initView();
        initMsg();
        FirebaseMessaging.getInstance().subscribeToTopic("user_"+ownerId);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
        new GetToken().execute();
        myDb = new DatabaseHelper(this);
    }

    private void initView() {
        inputText = (EditText) findViewById(R.id.editMessage);
        send = (Button) findViewById(R.id.btnSend);
        adapter = new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

    }

    private void append_chat_conversation(DataSnapshot dataSnapshot) {
//        temp_key = myRef.push().getKey();
//        myRef.updateChildren(userData);
//        DatabaseReference msg_root = myRef.child(temp_key);
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_sender = (String) ((DataSnapshot)i.next()).getValue();
            Message m = null;
            if(chat_sender.equals(ownerId)){
                m = new Message(chat_msg,Message.TYPE_SEND);
            }else{
                m = new Message(chat_msg,Message.TYPE_RECEIVED);
            }
            msgList.add(m);
        }
        adapter.notifyDataSetChanged();
    }

    private void sendMsg(){
        String msg = inputText.getText().toString();
        if(!TextUtils.isEmpty(msg)) {
            inputText.setEnabled(false);
            inputText.setText("");
            DatabaseReference push = messageReference.push();
            String msgId = push.getKey();
            Message newMsg = new Message(msgId,ownerId,friendId,msg,Message.TEXT);
            push.setValue(newMsg);
            msgList.add(newMsg);
            adapter.notifyDataSetChanged();



        }
        inputText.setEnabled(true);
//        Intent intent = new Intent(LoginActivity.class);


    }

    private void recNotification(){
       NotificationCompat.Builder noti = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("From Up")
                .setContentText("Messenger")
                .setTicker("Alert mou");

        noti.setDefaults(NotificationCompat.DEFAULT_SOUND);
        noti.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1,noti.build());
    }
    private void initMsg(){
        Bundle bundleExtra = getIntent().getBundleExtra(BUNDLE);
        roomId = bundleExtra.getString(CHATROOMID);
        friendId = bundleExtra.getString(FRIENDID);
        UpDatabaseHelper helper = new UpDatabaseHelper(this);
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        String selection = UpDatabaseHelper.CHATROOM_ID_COLUMN + " = ?";
        String[] selectionArgs = { roomId };
        Cursor msgCursor = readableDatabase.query(UpDatabaseHelper.MESSAGES_TABLE,
                new String[]{
                        UpDatabaseHelper.MESSAGEID_COLUMN,
                        UpDatabaseHelper.CHATROOM_ID_COLUMN,
                        UpDatabaseHelper.SENDER_COLUMN,
                        UpDatabaseHelper.RECEIVER_COLUMN,
                        UpDatabaseHelper.CONTENT_COLUMN,
                        UpDatabaseHelper.TIMESTAMP_COLUMN
                },
                selection,
                selectionArgs,
                null,
                null,
                null
                );
        new readMsgTask().execute(msgCursor);
        messageReference = FirebaseDatabase.getInstance().getReference().child(MESSAGE).child(roomId);


    }
    private class GetToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            serve = new MyFirebaseInstanceIDService();
            serve.onTokenRefresh();
            return null;
        }
    }
    private class readMsgTask extends AsyncTask<Cursor,Void,Void>{

        @Override
        protected Void doInBackground(Cursor... params) {
            Cursor cursor = params[0];
            if(cursor.getCount()>0){
                Message msg;
                cursor.moveToFirst();
                while (!cursor.isLast()){
                    String msgid = cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.MESSAGEID_COLUMN));
                    String senderId = cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.SENDER_COLUMN));
                    String receiverId = cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.RECEIVER_COLUMN));
                    String content =  cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.CONTENT_COLUMN));
                    Date timeStamp = new Date(cursor.getInt(cursor.getColumnIndexOrThrow(UpDatabaseHelper.CONTENT_COLUMN)));

                    if(!senderId.equals(ownerId)){
                        msg = new Message(content,Message.TYPE_RECEIVED);
                    }else{
                        msg = new Message(content,Message.TYPE_RECEIVED);
                    }
                    lastMsgKey = msgid;
                    msgList.add(msg);
                    cursor.moveToNext();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            messageReference.startAt(lastMsgKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    recNotification();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
