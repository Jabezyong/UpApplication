package com.application.upapplication.Views;

import android.app.NotificationManager;
import android.content.ContentValues;
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
import com.application.upapplication.Model.ChatListItem;
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

public class ChatActivity extends AppCompatActivity implements ChildEventListener,ValueEventListener{
    public static String BUNDLE = "com.application.upapplication.BUNDLE";
    public static String CHATROOM = "CHATROOM";
    public static String CHATROOMID =  "CHATROOMID";
    public static String FRIENDID = " FRIENDID";
    public static String NAME = "NAME";
    public final static String MESSAGE = "MESSAGES";
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private String lastMsgKey;
    String ownerId ;
    String friendId;
    String roomId;
    String fullName;
    private String chat_msg,chat_sender;
    private List<Message> msgList = new ArrayList<Message>();
    private DatabaseReference messageReference ;
    UpDatabaseHelper helper;
    MyFirebaseInstanceIDService serve;
    SQLiteDatabase writaleDatabase;

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
//        myDb = new DatabaseHelper(this);
    }

    private void initView() {
        inputText = (EditText) findViewById(R.id.editMessage);
        send = (Button) findViewById(R.id.btnSend);
        adapter = new MsgAdapter(ChatActivity.this,R.layout.msg_item,msgList);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

    }
    private void append_chat_conversation(Message msg){
        if (msg.getReceiver().equals(friendId)) {
            msg.setDeliverType(Message.TYPE_SEND);
        } else {
            msg.setDeliverType(Message.TYPE_RECEIVED);
        }
        msgList.add(msg);
        adapter.notifyDataSetChanged();
        saveInDatabase(msg);
    }
    private void append_chat_conversation(DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue()!=null) {
            Message msg = dataSnapshot.getValue(Message.class);
            if (msg.getReceiver().equals(friendId)) {
                msg.setDeliverType(Message.TYPE_SEND);
            } else {
                msg.setDeliverType(Message.TYPE_RECEIVED);
            }
            for(int i=0;i<msgList.size();i++){
                if(msgList.get(i).getMessageId().equals(msg.getMessageId())){
                    return;
                }
            }
            msgList.add(msg);
            adapter.notifyDataSetChanged();
            saveInDatabase(msg);

        }
    }
//    private void append_chat_conversation(DataSnapshot dataSnapshot) {
//        if(dataSnapshot.getValue()!=null) {
//            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
//            while (iterator.hasNext()) {
//                DataSnapshot next = iterator.next();
//                Message msg = next.getValue(Message.class);
//                if (msg.getReceiver().equals(friendId)) {
//                    msg.setDeliverType(Message.TYPE_SEND);
//                } else {
//                    msg.setDeliverType(Message.TYPE_RECEIVED);
//                }
//                msgList.add(msg);
//                adapter.notifyDataSetChanged();
//                saveInDatabase(msg);
//            }
//        }
//        while(i.hasNext()){
//            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
//            chat_sender = (String) ((DataSnapshot)i.next()).getValue();
//            Message m = null;
//            if(chat_sender.equals(ownerId)){
//                m = new Message(chat_msg,Message.TYPE_SEND);
//            }else{
//                m = new Message(chat_msg,Message.TYPE_RECEIVED);
//            }
//            msgList.add(msg);
//        }

    private void sendMsg(){
        String msg = inputText.getText().toString();
        if(!TextUtils.isEmpty(msg)) {
            inputText.setEnabled(false);
            inputText.setText("");
            DatabaseReference push = messageReference.push();
            String msgId = push.getKey();
            Message newMsg = new Message(msgId,ownerId,friendId,msg,Message.TEXT);
            newMsg.setDeliverType(Message.TYPE_SEND);
            newMsg.setRoomId(roomId);

            push.setValue(newMsg);
//            msgList.add(newMsg);
//            saveInDatabase(newMsg);
//            adapter.notifyDataSetChanged();



        }
        inputText.setEnabled(true);
//        Intent intent = new Intent(LoginActivity.class);


    }

    private void saveInDatabase(Message msg) {
        ContentValues value = new ContentValues();
        value.put(UpDatabaseHelper.MESSAGEID_COLUMN,msg.getMessageId());
        value.put(UpDatabaseHelper.CHATROOM_ID_COLUMN,msg.getRoomId());
        value.put(UpDatabaseHelper.SENDER_COLUMN,msg.getSender());
        value.put(UpDatabaseHelper.RECEIVER_COLUMN,msg.getReceiver());
        value.put(UpDatabaseHelper.CONTENT_COLUMN,msg.getContent());
        value.put(UpDatabaseHelper.CONTENT_TYPE_COLUMN,msg.getContentType());
        value.put(UpDatabaseHelper.TIMESTAMP_COLUMN,msg.getDate().toString());
        if(!writaleDatabase.isOpen())
            writaleDatabase = helper.getWritableDatabase();
        writaleDatabase.insert(UpDatabaseHelper.MESSAGES_TABLE,null,value);
        ChatListFragment.updateUI(friendId,msg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        writaleDatabase.close();
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
        fullName = bundleExtra.getString(NAME);
        setTitle(fullName);
        helper = new UpDatabaseHelper(this);
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        writaleDatabase = helper.getWritableDatabase();
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
        messageReference = FirebaseDatabase.getInstance().getReference().child(MESSAGE).child(roomId);
        new readMsgTask().execute(msgCursor);




    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        append_chat_conversation(dataSnapshot);
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
    public void onDataChange(DataSnapshot dataSnapshot) {
        append_chat_conversation(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

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
                for(int i=0;i<cursor.getCount();i++){
                    String msgid = cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.MESSAGEID_COLUMN));
                    String senderId = cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.SENDER_COLUMN));
                    String receiverId = cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.RECEIVER_COLUMN));
                    String content =  cursor.getString(cursor.getColumnIndexOrThrow(UpDatabaseHelper.CONTENT_COLUMN));
                    Date timeStamp = new Date(cursor.getInt(cursor.getColumnIndexOrThrow(UpDatabaseHelper.CONTENT_COLUMN)));

                    if(!senderId.equals(ownerId)){
                        msg = new Message(msgid,content,Message.TYPE_RECEIVED);
                    }else{
                        msg = new Message(msgid,content,Message.TYPE_SEND);
                    }
                    lastMsgKey = msgid;
                    msgList.add(msg);
                    cursor.moveToNext();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            adapter.notifyDataSetChanged();
            if(lastMsgKey == null){
                messageReference.orderByKey().addChildEventListener(ChatActivity.this);
            }else {
               messageReference.orderByKey().startAt(lastMsgKey).addChildEventListener(ChatActivity.this);
            }
        }
    }
}
