package com.application.upapplication.Views;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.application.upapplication.Controller.GeofenceTransitionsIntentService;
import com.application.upapplication.Model.Constants;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.application.upapplication.Swipe.TinderCard;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.butterknifelite.ButterKnifeLite;
import com.mindorks.butterknifelite.annotations.BindView;
import com.mindorks.butterknifelite.annotations.OnClick;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Lz-Yang on 18/12/2016.
 */

public class SwipeFragment extends Fragment implements ResultCallback<Status> {
    private ArrayList<Geofence> mGeofenceList;
    private ArrayList<TinderCard> tinderCards ;
    private LinearLayout wholeLayout;
    ValueEventListener listener;
    private DatabaseReference mDataBase;
    ProgressDialog waitDialog;
    int count = 0;
    int totalPhoto = 0;
    @BindView(R.id.swipeView)
    private SwipePlaceHolderView mSwipView;
    private ImageButton btnUp,btnDown;
    public static Button btnSearch;
    public static boolean isSchool = false;
    String ownerid;
    public static SwipeFragment newInstance() {
        SwipeFragment swipeFragment = new SwipeFragment();
        Bundle extraArguments = new Bundle();
        swipeFragment.setArguments(extraArguments);
        return swipeFragment;
    }
    public SwipeFragment() {
    }
    public void search(){


        mGeofenceList = new ArrayList<>();
        populateGefenceList();
        addGeofences();
        mSwipView.getBuilder()
                .setDisplayViewCount(4)
                .setWidthSwipeDistFactor(15)
                .setHeightSwipeDistFactor(20)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.UPPREFERENCE, Context.MODE_PRIVATE);
        ownerid = preferences.getString(getString(R.string.ownerid),"");
        wholeLayout = (LinearLayout) view.findViewById(R.id.wholeLayout);
        tinderCards = new ArrayList<>();
        ButterKnifeLite.bind(this,view);
        btnUp = (ImageButton) view.findViewById(R.id.imageViewUp);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptClick();
            }
        });
        btnDown = (ImageButton) view.findViewById(R.id.imageViewDown);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "yyy", Toast.LENGTH_SHORT).show();
                onRejectClick();
            }
        });
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void initCards(){
//        new DownloadCardsTask().execute();
//            initCards();
        if(isSchool){
            waitDialog = new ProgressDialog(getContext());
            waitDialog.setMessage("Loading");
            waitDialog.setCancelable(true);
            waitDialog.show();
            executing();


        }else{
            showDialogMessage();
        }
        isSchool = false;

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    private void populateGefenceList() {
        for(Map.Entry<String,LatLng> entry: Constants.LANDMARKS.entrySet()){
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }
    public void addGeofences(){
        if(!MainActivity.MGOOGLEAPICLIENT.isConnected() || MainActivity.MGOOGLEAPICLIENT.isConnecting()){
            MainActivity.MGOOGLEAPICLIENT.connect();
//            Toast.makeText(getContext(),"Google API Client not connected!",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    MainActivity.MGOOGLEAPICLIENT,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
        }
    }
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(getContext(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
        return PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    private void executing(){
        readFromFirebase();
        btnSearch.setVisibility(View.GONE);
        wholeLayout.setVisibility(View.VISIBLE);

    }

    private void insertCardView(DataSnapshot data) {
        UserDetails user = data.getValue(UserDetails.class);
        String id = user.getId();
        if(ownerid.equals(id)){
            return;
        }
        String name = user.getFirstName()+" "+ user.getLastName();
        int age = user.getAge();
        String course = user.getCourse();
        final TinderCard card = new TinderCard(getContext(),mSwipView,btnSearch,ownerid);
        card.setFriendId(id);
        mSwipView.addView(card);
        card.setnameAgeTxt(name,age);
        card.setCourseText(course);
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filepath = mStorage.child("UserPhotos").child(id+".png");
        int ONE_MEGABYTE = 1024*1024;
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                card.setProfileImageView(getImage(bytes));
                tinderCards.add(card);

                totalPhoto++;
                if(totalPhoto==5){
                    if(waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    totalPhoto  =0;

                }
            }
        });
    }

    private void readFromFirebase() {

        mDataBase = FirebaseDatabase.getInstance().getReference().child("users");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for(DataSnapshot data:children){

                        insertCardView(data);
                        count++;
                        if(count==5){
//                            Toast.makeText(getContext(),"Read enough data",Toast.LENGTH_LONG).show();

                            mDataBase.removeEventListener(listener);
                            count = 0;
                            break;
                        }

                    }
                    if(waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
//                    if(waitDialog.isShowing())
//                        waitDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDataBase.addListenerForSingleValueEvent(listener);
    }
    private void readEveryUsers(){

    }
    @Override
    public void onResult(@NonNull Status status) {
        if(status.isSuccess()){
               initCards();

        }
    }


    private void showDialogMessage() {
//        Toast.makeText(getContext(),"make sure you are in campus!",Toast.LENGTH_LONG).show();
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setMessage("Make sure you are in school area!\n Try again later")
                .setPositiveButton("Dismiss",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        build.create().show();
    }

    private Bitmap getImage(byte[] data){
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }
    @OnClick(R.id.imageViewDown)
    private void onRejectClick(){
        mSwipView.doSwipe(false);
    }

    @OnClick(R.id.imageViewUp)
    private void onAcceptClick(){
        mSwipView.doSwipe(true);
    }
}
