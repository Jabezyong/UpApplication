package com.application.upapplication.Views;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.application.upapplication.Controller.MyFirebaseInstanceIDService;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.R;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageViewAccount;
    ImageView imageViewInfo;
    Fragment fragment ;
    UpDatabaseHelper databaseHelper;
    MyFirebaseInstanceIDService firebaseService;
    private SQLiteDatabase db;
    private FirebaseDatabase firebase ;
    private DatabaseReference databaseReference ;
    private DatabaseReference coursesReference;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_fragment);
        new GetToken().execute();
        firebase= FirebaseDatabase.getInstance();
//        databaseReference = firebase.getReference().child("Interests");
//        coursesReference = firebase.getReference().child("Courses");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(isFirstRun(preferences)){
            initDatabase();
            Log.d(getPackageName(),"Is first Run");
            fragment = new AccountFragment();
        }else {
            Log.d(getPackageName(), "It is not first Run");
        }
        if(isLoggedIn())
            fragment = new AccountFragment();
        else
            fragment = new LoginFragment();
        transaction.add(R.id.fragment_of_container,fragment);
        transaction.commit();
    }
    private boolean isLoggedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return !(accessToken == null || accessToken.getPermissions().isEmpty());
    }
    private void initDatabase(){
       mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Initializing");
        mProgressDialog.show();
        databaseHelper = new UpDatabaseHelper(this);
            db = databaseHelper.getWritableDatabase();
           mProgressDialog.dismiss();
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
////                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
////                    for(DataSnapshot child: children){
//                        executeInterestsQuery(dataSnapshot);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            coursesReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    executeCoursesQuery(dataSnapshot);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

    }

//    private void executeCoursesQuery(DataSnapshot dataSnapshot) {
//        ContentValues values=  new ContentValues();
//        String course ="";
//        Iterator i = dataSnapshot.getChildren().iterator();
//        while (i.hasNext()){
//            DataSnapshot data= (DataSnapshot) i.next();
//            course = (String) data.getValue();
//            System.out.print(course);
//            values.put(UpDatabaseHelper.COURSE_NAME_COLUMN,course);
//            values.put(UpDatabaseHelper.COURSE_ID_COLUMN,id);
//            db.insert(UpDatabaseHelper.COURSE_TABLE,null,values);
//        }
//    }

//    private void executeInterestsQuery(DataSnapshot dataSnapshot) {
//
//        ContentValues values = new ContentValues();
//        String  interest ="";
//        String category;
//        Iterator i =  dataSnapshot.getChildren().iterator();
//       while(i.hasNext()){
//           DataSnapshot data = (DataSnapshot) i.next();
//           category =data.getKey();
//           for(DataSnapshot child : data.getChildren()){
//               interest = child.getValue().toString();
//               values.put(UpDatabaseHelper.INTEREST_CATEGORY_COLUMN,category);
//               values.put(UpDatabaseHelper.INTEREST_NAME_COLUMN,interest);
//               db.insert(UpDatabaseHelper.INTEREST_TABLE,null,values);
//           }
//           }
//           long total = data.getChildrenCount();
//           interest = data.child(data.getKey()).getValue(String.class);
//           System.out.print("hello");

//        }
//    }

    private boolean isFirstRun(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getBoolean("FirstRun",true)){
            editor.putBoolean("FirstRun",false).commit();
            return true;
        }else{
            return false;
        }
    }
    private class GetToken extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            firebaseService = new MyFirebaseInstanceIDService();
            firebaseService.onTokenRefresh();
            return null;
        }
    }
    public void onClick(View view){

    }

}
