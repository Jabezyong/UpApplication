package com.application.upapplication.Swipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.application.upapplication.Model.SendFriendRequest;
import com.application.upapplication.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import static android.view.View.VISIBLE;

/**
 * Created by janisharali on 19/08/16.
 */
@NonReusable
@Layout(R.layout.tinder_card_view)
public class TinderCard {
    private String FRIEND ="Friend Request";
    private static int count;
    private String ownerId;
    private String friendId;
    private String name;
    private int age;
    private String course;
    private Context context;
    private SwipePlaceHolderView swipeView;
    private Button btnSearch;
    private LinearLayout wholeLayout;
    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.courseText)
    private TextView courseText;

    @Click(R.id.profileImageView)
    private void onClick(){
        Log.d("DEBUG", "profileImageView");
    }
    public TinderCard(){}
    public TinderCard(Context context, SwipePlaceHolderView swipeView, Button btnSearch,String ownerid){
        this.context = context;
        this.swipeView =swipeView;
        this.btnSearch = btnSearch;
        this.ownerId = ownerid;
    }
    public TinderCard(String id,String name,int age,String course){
        this.friendId = id;
        this.name = name;
        this.age = age;
        this.course = course;
        setnameAgeTxt(name,age);
        setCourseText(course);
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    @Resolve
    private void onResolved(){
        this.courseText.setText(course);
    }
    public void setnameAgeTxt(String name,int age){
        this.nameAgeTxt.setText(name+", "+age);
    }
    public void setProfileImageView(Bitmap bitmap){
        this.profileImageView.setImageBitmap(bitmap);
    }
    public void setCourseText(String course){
        this.courseText.setText(course);
    }


    @SwipeOut
    private void onSwipedOut(){
        int i = swipeView.getChildCount();
        if(i ==1){
            btnSearch.setVisibility(VISIBLE);
        }
        Log.d("DEBUG", "onSwipedOut");
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("DEBUG", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        sendFriendRequest();
        Toast.makeText(context,"Friend Request Sent",Toast.LENGTH_LONG).show();
        int i = swipeView.getChildCount();
        if(i ==1){
            btnSearch.setVisibility(VISIBLE);
        }
        Log.d("DEBUG", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("DEBUG", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){

        Log.d("DEBUG", "onSwipeOutState");
    }
    private void sendFriendRequest(){
        new friendTask().execute(ownerId,friendId,FRIEND);

    }

    private class friendTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            String ownerId = params[0];
            String friendId = params[1];
            String FRIEND = params[2];
            SendFriendRequest request = new SendFriendRequest(ownerId);
            DatabaseReference friendReferece = FirebaseDatabase.getInstance().getReference().child(FRIEND).child(friendId);
            friendReferece.child(ownerId).setValue(request);

            return null;
        }


    }
//    private void sendFriendRequest(){
//        DatabaseReference ownerReference = FirebaseDatabase.getInstance().getReference().child(FRIEND).child(ownerId);
//        DatabaseReference friendReferece = FirebaseDatabase.getInstance().getReference().child(FRIEND).child(friendId);
//        int isProved = 0;
//        String roomId = randomUUID().toString();
//        Friend myFriend = new Friend(ownerId,friendId,isProved,roomId);
//        Friend hisFriend = new Friend(friendId,ownerId,isProved,roomId);
//        ownerReference.setValue(myFriend);
//        ownerReference.setValue(hisFriend);
//
//    }
}
