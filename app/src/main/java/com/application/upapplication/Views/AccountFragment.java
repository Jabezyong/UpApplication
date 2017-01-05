package com.application.upapplication.Views;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.application.upapplication.Controller.VolleyApplication;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private String APPKEY = "125fe10cbed00";
    private String APPSECRET = "bc73c2185d77786c192a063f63c62241";
    private StorageReference mStorage;
    private StorageReference filepath;
    ValueEventListener eventListener;
    FirebaseAuth mAuth;
    Button logout,btnNext;
    View view;
    LayoutInflater inflater;
    ViewGroup container;
    TextView textViewShowName,textViewShowGender,textViewShowBirthday;
    EditText etAbout;
    ImageView imageViewProfilePicture;
    private ArrayAdapter<CharSequence> yearAdapter,coursesAdapter,songAdapter,sportAdapter,foodAdapter;
    private Spinner sportSpinner,foodSpinner,songSpinner,courseSpinner,yearSpinner;
    private Switch male,female;
    private Profile FACEBOOK_PROFILE ;
    String food,song,sport,course,firstName,lastName,gender,birthday,aboutMe,fbId,photo,phone;
    int year,age,targetMale,targetFemale,verified;
    boolean found = false;
    Date lastLogin;
    private Bitmap bitmap;
    Bundle bundle;
    ProgressDialog dialog;
    byte[] bytesFromFirebase;
    public AccountFragment() {

        // Required empty public constructor
    }
    private void init(){
        etAbout = (EditText) view.findViewById(R.id.editTextAboutMe);
        textViewShowName = (TextView) view.findViewById(R.id.textViewShowName);
        textViewShowGender = (TextView) view.findViewById(R.id.textViewShowGender);
        textViewShowBirthday = (TextView) view.findViewById(R.id.textViewShowBirthday);
        imageViewProfilePicture = (ImageView) view.findViewById(R.id.imageViewProfilePicture);
        logout = (Button) view.findViewById(R.id.logout);
        btnNext = (Button) view.findViewById(R.id.btnContinue);
        male = (Switch) view.findViewById(R.id.switchMale);
        female = (Switch) view.findViewById(R.id.switchFemale);
        sportSpinner = (Spinner) view.findViewById(R.id.spinnerSport);
        songSpinner = (Spinner) view.findViewById(R.id.spinnerSong);
        foodSpinner = (Spinner) view.findViewById(R.id.spinnerFood);
        yearSpinner = (Spinner) view.findViewById(R.id.spinnerYear);
        courseSpinner = (Spinner) view.findViewById(R.id.spinnerCourse);
        userInput();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                submit();
            }
        });

    }



    private void initSpinners(){
        foodAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item,getResources().getTextArray(R.array.food)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        sportAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item,getResources().getTextArray(R.array.sports)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        songAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item,getResources().getTextArray(R.array.songs)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        coursesAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item,getResources().getTextArray(R.array.courses)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        yearAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item,getResources().getTextArray(R.array.year)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };

        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        songAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        yearSpinner.setAdapter(yearAdapter);
        foodSpinner.setAdapter(foodAdapter);
        sportSpinner.setAdapter(sportAdapter);
        songSpinner.setAdapter(songAdapter);
        courseSpinner.setAdapter(coursesAdapter);

        yearSpinner.setOnItemSelectedListener(this);
        foodSpinner.setOnItemSelectedListener(this);
        sportSpinner.setOnItemSelectedListener(this);
        songSpinner.setOnItemSelectedListener(this);
        courseSpinner.setOnItemSelectedListener(this);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialog  = new ProgressDialog(getContext());
        dialog.setMessage("Loading");
        dialog.setCancelable(false);
        dialog.show();
        bundle = getActivity().getIntent().getBundleExtra(LoginFragment.bundleTAG);
        this.view = inflater.inflate(R.layout.fragment_account, container, false);
        this.inflater  = inflater;
        this.container = container;
        if(bundle != null){
            fbId = bundle.getString("id");
            SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.UPPREFERENCE,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.ownerid),fbId);
            editor.commit();
            retrieveDataFromDatabase(fbId);
        }else{
            logOut();
        }

        return this.view;
    }
    private void userInput() {

        firstName = bundle.getString("first_name");
        lastName = bundle.getString("last_name");
        textViewShowName.setText(firstName + " " + lastName);
        gender = bundle.getString("gender");
        textViewShowGender.setText(gender);
        birthday = bundle.getString("birthday");
        textViewShowBirthday.setText(birthday);

        initSpinners();
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position != 0) {
            switch (parent.getId()) {
                case R.id.spinnerFood:
                    food = (String) foodSpinner.getSelectedItem();
                    break;
                case R.id.spinnerSong:
                    song = (String) songSpinner.getSelectedItem();
                    break;
                case R.id.spinnerSport:
                    sport = (String) sportSpinner.getSelectedItem();
                    break;
                case R.id.spinnerCourse:
                    course = (String) courseSpinner.getSelectedItem();
                    break;
                case R.id.spinnerYear:
                    year = Integer.valueOf((String)yearSpinner.getSelectedItem());
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void logOut(){
        LoginManager.getInstance().logOut();
        getActivity().finish();
        Intent intent = new Intent(this.getContext(),MainActivity.class);
        startActivity(intent);
    }

    public void submit() {
        boolean check = true;
        String msg = "";
        if (foodSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Food\n";
        }
        if (yearSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Intake Year\n";
        }
        if (songSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Song Type\n";
        }
        if (foodSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Food Type\n";
        }
        if (sportSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Sport\n";
        }
        if (!male.isPressed() && !female.isPressed()) {
            msg += "Please Select Gender of target\n";
        }
        if(birthday == null){

        }


        if (!check) {
            if(dialog.isShowing())
                dialog.dismiss();
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());
            builder.setTitle(Html.fromHtml("<font color='FFFF4081'>Error Message</font>"));
            builder.setMessage(msg);
            builder.setCancelable(true);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            if(!found) {
                aboutMe = etAbout.getText().toString();
                if(male.isChecked()){
                    targetMale = 1;
                }else{
                    targetMale = 0;
                }

                if(female.isChecked()){
                    targetFemale = 1;
                }else{
                    targetFemale = 0;
                }
            }
            verifySms();
        }
    }
    private void saving() {

            if (birthday != null) {
                String[] separated = birthday.split("/");
                age = 2017- Integer.valueOf(separated[2]);
            } else {
                age = 20;
            }
            lastLogin = new Date();

            Toast.makeText(getContext(), lastLogin.toString(), Toast.LENGTH_LONG).show();
            saveImageToDatabase();
            executeQuery();
    }
    private void executeQuery(){
        ContentValues values = new ContentValues();
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        values.put(UpDatabaseHelper.ID_COLUMN,fbId);
        values.put(UpDatabaseHelper.FIRST_NAME_COLUMN,firstName);
        values.put(UpDatabaseHelper.LAST_NAME_COLUMN,lastName);
        values.put(UpDatabaseHelper.GENDER_COLUMN,gender);
        values.put(UpDatabaseHelper.FACEBOOK_ID_COLUMN,fbId);
        values.put(UpDatabaseHelper.PROFILE_PHOTO_COLUMN,photo);
        values.put(UpDatabaseHelper.COURSE_COLUMN,course);
        values.put(UpDatabaseHelper.ACADEMIC_YEAR_COLUMN,year);
        values.put(UpDatabaseHelper.ABOUT_ME_COLUMN,aboutMe);
        values.put(UpDatabaseHelper.DOB_COLUMN,birthday);
        values.put(UpDatabaseHelper.PHONE_COLUMN,phone);
        values.put(UpDatabaseHelper.VERIFIED_COLUMN,verified);
        values.put(UpDatabaseHelper.AGE_COLUMN,age);
        values.put(UpDatabaseHelper.INTEREST_1_COLUMN,song);
        values.put(UpDatabaseHelper.INTEREST_2_COLUMN,sport);
        values.put(UpDatabaseHelper.INTEREST_3_COLUMN,food);
        values.put(UpDatabaseHelper.TARGET_MALE_COLUMN,targetMale);
        values.put(UpDatabaseHelper.TARGET_FEMALE_COLUMN,targetFemale);
        values.put(UpDatabaseHelper.LAST_LOGIN_COLUMN,lastLogin.toString());
        db.insert(UpDatabaseHelper.USER_TABLE,null,values);
        db.close();
        if(!found) {
            initFireBase();
            saveToFireBase();
            uploadProfilePhoto();
            Toast.makeText(getContext(),"Saved to firebase",Toast.LENGTH_LONG).show();
        }
        startNewActivity();

    }
    private void startNewActivity(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
//        getActivity().finish();
        Intent intent = new Intent(getContext(),MainActivity.class);
        startActivity(intent);
    }

    private void verifySms() {
        if(!found) {
            SMSSDK.initSDK(getContext(), APPKEY, APPSECRET);
//        phone = "0163582906";

            final RegisterPage registerPage = new RegisterPage();
            registerPage.setRegisterCallback(new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                        String country = (String) phoneMap.get("country");
                        phone = "0" + (String) phoneMap.get("phone");
                        Toast.makeText(getContext(), phone, Toast.LENGTH_LONG).show();
                        verified = 1;
                        saving();
                    } else if (result == SMSSDK.RESULT_ERROR) {
                        phone = "0163582906";
                    }
                }
            });
            registerPage.show(getContext());
        }
    }

    private void initFireBase(){
        mStorage = FirebaseStorage.getInstance().getReference();
        filepath = mStorage.child("UserPhotos").child(fbId+".png");

    }
    private void registerUser(String phone) {
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String id = Profile.getCurrentProfile().getId();
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.PHONE_COLUMN,phone);
        values.put(UpDatabaseHelper.VERIFIED_COLUMN,1);

        db.update(UpDatabaseHelper.USER_TABLE,values,
                UpDatabaseHelper.FACEBOOK_ID_COLUMN+" = ?",new String[]{id});
    }
    private File saveBitmap() {
        imageViewProfilePicture.setDrawingCacheEnabled(true);
        imageViewProfilePicture.buildDrawingCache();
        bitmap = imageViewProfilePicture.getDrawingCache();
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory,fbId+".png");
        if(file.exists()){
            file.delete();
            file = new File(extStorageDirectory,fbId+".png");
            Log.e("File Exists",""+file+",Bitmap= "+bitmap);
        }else{
            try {
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("File",""+file);
        }
        return file;
    }
    private void saveToFireBase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebase.getReference();
        UserDetails newUser = new UserDetails(fbId,firstName,lastName,gender,birthday,phone,course,year,aboutMe,age,song,sport,food,1,targetMale,targetFemale,lastLogin,photo);
        databaseReference.child("users").child(fbId).setValue(newUser);
    }
    private void uploadProfilePhoto() {


        if(bitmap == null){
            imageViewProfilePicture.setDrawingCacheEnabled(true);
            imageViewProfilePicture.buildDrawingCache();
            bitmap = imageViewProfilePicture .getDrawingCache();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = filepath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Not able to upload photo",Toast.LENGTH_LONG).show();
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photo  = taskSnapshot.getDownloadUrl().toString();
//                Toast.makeText(getContext(),"Photo= "+photo,Toast.LENGTH_LONG).show();
                Log.e("Photo url",photo);
                startNewActivity();
            }
        });
    }
    private void downloadProfilePhoto(){
        mStorage = FirebaseStorage.getInstance().getReference();
        filepath = mStorage.child("UserPhotos").child(fbId+".png");
        photo = filepath.getDownloadUrl().toString();
        int ONE_MEGABYTE = 1024*1024;
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
//                bytesFromFirebase =bytes;
                bitmap = getImage(bytes);
//                imageViewProfilePicture = (ImageView) view.findViewById(R.id.imageViewProfilePicture);
//                imageViewProfilePicture
//                saveImageToDatabase();
                saving();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Cant download profile photo",Toast.LENGTH_LONG).show();
            }
        });
    }



    private void retrieveDataFromDatabase(String id){
         final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        eventListener= new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Toast.makeText(getContext(),"Cant read from databaese",Toast.LENGTH_LONG).show();
                    init();
                    downloadPhotoFromFacebook();
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                    mDatabase.removeEventListener(eventListener);
                }else{
                    found = true;
                    UserDetails user = dataSnapshot.getValue(UserDetails.class);
                    birthday = user.getBirthday();
                    firstName = user.getFirstName();
                    lastName = user.getLastName();
                    gender = user.getGender();
                    song = user.getInterest1();
                    sport = user.getInterest2();
                    food = user.getInterest3();
                    targetMale = user.getTargetMale();
                    targetFemale = user.getTargetFemale();
                    aboutMe = user.getAboutMe();
                    verified = user.getIsVerified();
                    photo = user.getPhoto();
                    course = user.getCourse();
                    year = user.getAcademicYear();
                    downloadProfilePhoto();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
    }



    private void downloadPhotoFromFacebook(){
        FacebookSdk.sdkInitialize(getContext());
        FACEBOOK_PROFILE = Profile.getCurrentProfile();
        if (FACEBOOK_PROFILE != null) {
            Uri link = FACEBOOK_PROFILE.getProfilePictureUri(400, 400);
            String url = link.toString();
            ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    imageViewProfilePicture.setImageBitmap(response);
                    saveBitmap();
                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            }, 400, 400, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Unable to get Profile Picture", Toast.LENGTH_LONG).show();
                }
            });
            if (request != null) {
                VolleyApplication.getHttpQueues().add(request);
            } else {
                Toast.makeText(getContext(), "Unable to get Profile Picture", Toast.LENGTH_LONG).show();
            }

            Log.i(getActivity().getPackageName(), link + "");
        }
    }
    private void saveImageToDatabase(){
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN,fbId);
        values.put(UpDatabaseHelper.IMAGE_COLUMN, getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.insert(UpDatabaseHelper.IMAGES_TABLE,null,values);
    }

    public static byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] data){
        return BitmapFactory.decodeByteArray(data,0,data.length);
    }
    //    private void retrieveDataFromDatabase() {
//        databaseHelper = new UpDatabaseHelper(getContext());
//        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
//        String[] projection ={
//                UpDatabaseHelper.INTEREST_ID_COLUMN,
//                UpDatabaseHelper.INTEREST_NAME_COLUMN,
//                UpDatabaseHelper.INTEREST_CATEGORY_COLUMN
//        };
//        String sortOrder = UpDatabaseHelper.INTEREST_NAME_COLUMN +" ASC";
//        Cursor query = readableDatabase.query(
//                UpDatabaseHelper.INTEREST_TABLE,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                sortOrder
//        );
//        if(query.getCount()>0){
//            query.moveToFirst();
//            while (!query.isLast()){
//                int id = query.getInt(
//                        query.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_ID_COLUMN)
//                );
//                String name = query.getString(
//                        query.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_NAME_COLUMN)
//                );
//                String category = query.getString(
//                        query.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_CATEGORY_COLUMN)
//                );
//                Interest interest = new Interest(id,name,category);
//                if(category.equals("Sports")){
//                    sportRes.add(interest);
//                }else if(category.equals("Food")){
//                    foodRes.add(interest);
//                }else{
//                    songRes.add(interest);
//                }
//            }
//        }
//        String filter = UpDatabaseHelper.INTEREST_CATEGORY_COLUMN +"= ?";
//        String[] sportFilterArgs = {"Sports"};
//        String[] foodFilterArgs = {"Food"};
//        String[] songFilterAges = {"Song"};
//        String sortOrder = UpDatabaseHelper.INTEREST_NAME_COLUMN+"  ASC";
//        Cursor sportQuery = readableDatabase.query(
//                UpDatabaseHelper.INTEREST_TABLE,
//                projection,
//                filter,
//                sportFilterArgs,
//                null,
//                null,
//                sortOrder
//        );
//        Cursor foodQuery = readableDatabase.query(
//                UpDatabaseHelper.INTEREST_TABLE,
//                projection,
//                filter,
//                foodFilterArgs,
//                null,
//                null,
//                sortOrder
//        );
//        Cursor songQuery = readableDatabase.query(
//                UpDatabaseHelper.INTEREST_TABLE,
//                projection,
//                filter,
//                songFilterAges,
//                null,
//                null,
//                sortOrder
//        );
//        if(sportQuery.getCount() > 0) {
//            sportQuery.moveToFirst();
//            while (!sportQuery.isLast()) {
//                int id = sportQuery.getInt(
//                        sportQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_ID_COLUMN)
//                );
//                String name = sportQuery.getString(
//                        sportQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_NAME_COLUMN)
//                );
//                String category = sportQuery.getString(
//                        sportQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_CATEGORY_COLUMN)
//                );
//                Interest interest = new Interest(id, name, category);
//                sportRes.add(interest);
//                sportQuery.moveToNext();
//            }
//        }
//        if(foodQuery.getCount() > 0) {
//            foodQuery.moveToFirst();
//            while (!foodQuery.isLast()) {
//                int id = foodQuery.getInt(
//                        foodQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_ID_COLUMN)
//                );
//                String name = foodQuery.getString(
//                        foodQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_NAME_COLUMN)
//                );
//                String category = foodQuery.getString(
//                        foodQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_CATEGORY_COLUMN)
//                );
//                Interest interest = new Interest(id, name, category);
//                foodRes.add(interest);
//                foodQuery.moveToNext();
//            }
//        }
//        if(songQuery.getCount() >0) {
//            songQuery.moveToFirst();
//            while (!songQuery.isLast()) {
//                int id = songQuery.getInt(
//                        songQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_ID_COLUMN)
//                );
//                String name = songQuery.getString(
//                        songQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_NAME_COLUMN)
//                );
//                String category = songQuery.getString(
//                        songQuery.getColumnIndexOrThrow(UpDatabaseHelper.INTEREST_CATEGORY_COLUMN)
//                );
//                Interest interest = new Interest(id, name, category);
//                songRes.add(interest);
//                songQuery.moveToNext();
//            }
//        }
//        sportAdapter = new SpinInterestAdapter(getContext(),
//                android.R.layout.simple_spinner_item,
//                sportRes);
//        songAdapter = new SpinInterestAdapter(getContext(),
//                android.R.layout.simple_spinner_item,
//                songRes);
//        foodAdapter = new SpinInterestAdapter(getContext(),
//                android.R.layout.simple_spinner_item,
//                foodRes);
//        adapter = new ArrayAdapter<Interest>(getContext(),android.R.layout.simple_spinner_item,songRes);
//
//        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        songAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sportSpinner.setAdapter(adapter);
//        songSpinner.setAdapter(adapter);
//        foodSpinner.setAdapter(adapter);
//    }
}
