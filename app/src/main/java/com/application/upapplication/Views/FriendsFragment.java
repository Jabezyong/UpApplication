package com.application.upapplication.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.application.upapplication.R;


/**
 * Created by user on 12/30/2016.
 */

public class FriendsFragment extends Fragment {
    RelativeLayout relativeLayout;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friendlist,null);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.requestfriendlist);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNewFriend();
            }
        });
        init();
        return view;
    }

    public void checkNewFriend(){
        Intent intent = new Intent(getContext(), RequestFriendActivity.class);
        startActivity(intent);
    }

    private void init(){

    }
}
