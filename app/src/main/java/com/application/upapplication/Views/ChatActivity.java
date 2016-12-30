package com.application.upapplication.Views;

import android.app.NotificationManager;
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
import com.application.upapplication.Model.Message;
import com.application.upapplication.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String user = "Jabez";
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;

    private List<Message> msgList = new ArrayList<Message>();
    private FirebaseDatabase  database = FirebaseDatabase.getInstance();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    DatabaseReference myRef = database.getReference().child("yes");
    MyFirebaseInstanceIDService serve;

    DatabaseHelper myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        initView();
        initMsg();
        String username = "puf";
        FirebaseMessaging.getInstance().subscribeToTopic("user_"+username);
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
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
                recNotification();
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
    private String chat_msg,chat_sender;
    private void append_chat_conversation(DataSnapshot dataSnapshot) {
//        temp_key = myRef.push().getKey();
//        myRef.updateChildren(userData);
//        DatabaseReference msg_root = myRef.child(temp_key);
        Iterator i = dataSnapshot.getChildren().iterator();
        while(i.hasNext()){
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_sender = (String) ((DataSnapshot)i.next()).getValue();
            Message m = null;
            if(chat_sender.equals(user)){
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
            Map<String, Object> userData = new HashMap<String, Object>();
            String temp_key = myRef.push().getKey();
            myRef.updateChildren(userData);
            DatabaseReference msg_root = myRef.child(temp_key);
            Map<String, Object> msgContent = new HashMap<String, Object>();
            msgContent.put("name", user);
            msgContent.put("msg", msg);
            msg_root.updateChildren(msgContent);
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
        Message msg1 = new Message("Hello, how are you?", Message.TYPE_RECEIVED);
        msgList.add(msg1);
        Message msg2 = new Message("Fine, thank you, and you?", Message.TYPE_SEND);
        msgList.add(msg2);
        Message msg3 = new Message("I am fine, too!", Message.TYPE_RECEIVED);
        msgList.add(msg3);

    }
    private class GetToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            serve = new MyFirebaseInstanceIDService();
            serve.onTokenRefresh();
            return null;
        }
    }
}
