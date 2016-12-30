package com.application.upapplication.Views;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.application.upapplication.Controller.ChatListAdapter;
import com.application.upapplication.Model.ChatListItem;
import com.application.upapplication.R;

import java.util.ArrayList;
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

    List<ChatListItem> chatListItems;
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
        chatList= (ListView) view.findViewById(R.id.chatlist);

        chatListItems = new ArrayList<ChatListItem>();

        friend_names = getResources().getStringArray(R.array.friend_names);
        lastMsg = getResources().getStringArray(R.array.lastmessage);
        time = getResources().getStringArray(R.array.contact);
        profile_pics = getResources().obtainTypedArray(R.array.profile_pic);
        for(int i=0;i<friend_names.length;i++){
            ChatListItem item = new ChatListItem(friend_names[i],
                    profile_pics.getResourceId(i,-1),
                    lastMsg[i],
                    time[i]);
            chatListItems.add(item);
        }
        ChatListAdapter adapter = new ChatListAdapter(getContext(), chatListItems);

        chatList.setAdapter(adapter);
        profile_pics.recycle();;
        chatList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(),ChatActivity.class);
        startActivity(intent);
//        Toast.makeText(getContext(),position+"",Toast.LENGTH_LONG).show();
    }
}

