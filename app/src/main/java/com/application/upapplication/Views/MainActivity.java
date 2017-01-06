package com.application.upapplication.Views;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import com.application.upapplication.Controller.CustomViewPager;
import com.application.upapplication.R;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status>{
    public static final String TAG =" UPDATING";
    public static final String UPPREFERENCE= "UPPREFERENCE";
    public static GoogleApiClient MGOOGLEAPICLIENT;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private CustomViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    Profile profile;
    ImageButton imageViewChatList;
    ImageButton imageViewAccount;
    ImageButton imageFriends;
    ImageButton imageViewSeacrh;
    public static final int REQUEST_ID_MULTIPLE_PERMISSION = 1;
    private boolean checkPermission() {
        int requestCode = 0;
        ArrayList<String> permissionList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhone = checkSelfPermission( Manifest.permission.READ_PHONE_STATE);
//            int receiveSms = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int readSms = checkSelfPermission(Manifest.permission.READ_SMS);
            int readContacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int readSdcard = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeSdcard = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int fineLocation = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int coarseLocation = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
//            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
//                permissionList.add(Manifest.permission.RECEIVE_SMS);
//            }
            if (readSms != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_SMS);
            }
            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_CONTACTS);
            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(writeSdcard !=PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(fineLocation !=PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(coarseLocation !=PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(!permissionList.isEmpty()) {
                String[] permission = new String[permissionList.size()];
                ActivityCompat.requestPermissions(this,permissionList.toArray(permission), REQUEST_ID_MULTIPLE_PERMISSION);
                return false;
            }else{
                return true;
            }
        }   else{
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkPermission()){
            init();
        }
    }

    private void init() {
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        if(!isLoggedIn()){
            finish();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        initFragments();
        imageViewChatList = (ImageButton) findViewById(R.id.imageViewChatList);
        imageViewChatList.setOnClickListener(this);
        imageViewAccount = (ImageButton)findViewById(R.id.imageViewAccount);
        imageViewAccount.setOnClickListener(this);
        imageViewSeacrh = (ImageButton)findViewById(R.id.imageViewSeacrh);
        imageViewSeacrh.setOnClickListener(this);
        imageFriends = (ImageButton)findViewById(R.id.imageViewFriendList);
        imageFriends.setOnClickListener(this);


    }

    private void initFragments() {
//        ProgressDialog dialog = new ProgressDialog(getApplicationContext());
//        dialog.setMessage("Loading");
//        dialog.show();
        ChatListFragment chatRoomListFragment = new ChatListFragment();
        SwipeFragment swipeFragment = new SwipeFragment();
        FriendsFragment profileFragment = new FriendsFragment();
        ProfileFragment profileFragment1 = new ProfileFragment();
        fragments.add(chatRoomListFragment);
        fragments.add(swipeFragment);
        fragments.add(profileFragment);
        fragments.add(profileFragment1);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        };
        viewPager = (CustomViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(fragmentPagerAdapter);
//        dialog.dismiss();
    }

    public void onClick(View view){

        Fragment fragment = null;
        if(view == imageViewChatList){
            setTitle("Message");
            viewPager.setCurrentItem(0);
        }
        if(view == imageViewSeacrh){
            setTitle("Search Friends");
            viewPager.setCurrentItem(1);
        }
        if(view == imageFriends){
            setTitle("Friends");
            viewPager.setCurrentItem(2);
        }
        if(view == imageViewAccount){
            setTitle("Profile");
            viewPager.setCurrentItem(3);
        }

    }

    private boolean isLoggedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return !(accessToken == null || accessToken.getPermissions().isEmpty());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(getPackageName(),"Suspended connection to GoogleApi");
        MGOOGLEAPICLIENT.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
        if(!MGOOGLEAPICLIENT.isConnecting() || !MGOOGLEAPICLIENT.isConnected()){
            MGOOGLEAPICLIENT.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(MGOOGLEAPICLIENT.isConnecting() || MGOOGLEAPICLIENT.isConnected()){
            MGOOGLEAPICLIENT.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        MGOOGLEAPICLIENT = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void fragmentTransactionReplace(Fragment fragmentInstance) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_of_container, fragmentInstance)
                .commit();
    }
    public void openFragment(final int fragment){
        viewPager.setCurrentItem(fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"Permission Called back");
        switch (requestCode){
            case REQUEST_ID_MULTIPLE_PERMISSION:{
                Map<String,Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.READ_PHONE_STATE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECEIVE_SMS,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION,PackageManager.PERMISSION_GRANTED);
                if(grantResults.length>0){
                    for(int i=0;i<permissions.length;i++){
                        perms.put(permissions[i],grantResults[i]);
                    }
                    if(perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        &&
                            perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        &&
                            perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        &&
                            perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        &&
                            perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        &&
                            perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        &&
                            perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            ){
                        Log.d(TAG,"All permission granted");
                        init();
                    }else{
                        Log.d(TAG,"Some permission not granted and ask again");
                        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_PHONE_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_SMS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION))
                            showDialogOk("Permissions are required to run this app.",
                                    new Dialog.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch(which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkPermission();
                                                    break;
                                                default:
                                                    quitApp();
                                            }
                                        }
                                    });
                        else{
                            quitApp();
                        }

                    }
                }
            }
        }

    }

    private void quitApp() {
        this.finish();
    }

    @Override
    public void onBackPressed() {

    }

    private void showDialogOk(String msg, DialogInterface .OnClickListener listener){
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",listener)
                .create()
                .show();
    }
}
