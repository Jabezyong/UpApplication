package com.application.upapplication.Views;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
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
import com.application.upapplication.Controller.RequestFriendListAdapter;
import com.application.upapplication.Controller.VolleyApplication;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.FriendListItem;
import com.application.upapplication.Model.SuccessFriendRequest;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import static android.app.Activity.RESULT_OK;



/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private String APPKEY = "125fe10cbed00";
    private String APPSECRET = "bc73c2185d77786c192a063f63c62241";
    private StorageReference mStorage;
    private StorageReference filepath;
    ValueEventListener eventListener;
    FirebaseAuth mAuth;
    UserDetails user;
    Button logout, btnNext;
    View view;
    Button btnEdit;
    LayoutInflater inflater;
    ViewGroup container;
    TextView textViewShowName, textViewShowGender, textViewShowBirthday;
    EditText etAbout;
    ImageView imageViewProfilePicture;
    private ArrayAdapter<CharSequence> acaYearAdapter, coursesAdapter, songAdapter, sportAdapter, foodAdapter,dayAdapter,monthAdapter,yearAdapter;
    private Spinner sportSpinner, foodSpinner, songSpinner, courseSpinner, acaYearSpinner,daySpinner,monthSpinner,yearSpinner;
    private Switch male, female;
    private Profile FACEBOOK_PROFILE;
    String food, song, sport, course, firstName, lastName, gender, birthday, aboutMe, fbId, photo, phone,strYear,strMonth,strDay;
    int academicYear,month,day,year, age, targetMale, targetFemale, verified;
    boolean found = false;
    Date lastLogin;
    private Bitmap bitmap;
    Bundle bundle;
    ProgressDialog dialog;
    byte[] bytesFromFirebase;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private FaceDetector faceDetector;
    private int friendCount ;
    private int friendDataDownloadedCount ;
    public AccountFragment() {

        // Required empty public constructor
    }

    private void init() {
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
        acaYearSpinner = (Spinner) view.findViewById(R.id.spinnerYear);
        courseSpinner = (Spinner) view.findViewById(R.id.spinnerCourse);
        daySpinner = (Spinner) view.findViewById(R.id.spinnerBirthdayDay);
        monthSpinner = (Spinner) view.findViewById(R.id.spinnerBirthdayMonth);
        yearSpinner = (Spinner) view.findViewById(R.id.spinnerBirthdayYear);
        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceDetector = new FaceDetector.Builder(getActivity().getApplicationContext()).build();
                if (!faceDetector.isOperational()) {
                    Toast.makeText(getActivity(), "Face detector dependencies are not yet available.", Toast.LENGTH_LONG).show();
                }
                changeProfilePicture();
            }
        });
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


    private void initSpinners() {
        foodAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.food)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        sportAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.sports)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        songAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.songs)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        coursesAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.courses)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        acaYearAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.year)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        String[] years = new String[]{"Year","2000","1999","1998","1997","1996","1995","1994","1993","1992"};
        yearAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, years) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        monthAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getTextArray(R.array.month)) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        String[] days = new String[]{"Day","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
        dayAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_dropdown_item, days) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        };
        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        songAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acaYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        acaYearSpinner.setAdapter(acaYearAdapter);
        foodSpinner.setAdapter(foodAdapter);
        sportSpinner.setAdapter(sportAdapter);
        songSpinner.setAdapter(songAdapter);
        courseSpinner.setAdapter(coursesAdapter);
        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);
        daySpinner.setAdapter(dayAdapter);

        acaYearSpinner.setOnItemSelectedListener(this);
        foodSpinner.setOnItemSelectedListener(this);
        sportSpinner.setOnItemSelectedListener(this);
        songSpinner.setOnItemSelectedListener(this);
        courseSpinner.setOnItemSelectedListener(this);
        yearSpinner.setOnItemSelectedListener(this);
        monthSpinner.setOnItemSelectedListener(this);
        daySpinner.setOnItemSelectedListener(this);
        if(birthday!=null){
            String[] separated = birthday.split("/");
            String month = separated[0];
            String day = separated[1];
            String year = separated[2];
            selectSpinnerItemByValue(yearSpinner,year);
            selectSpinnerItemByValue(monthSpinner,month);
            selectSpinnerItemByValue(daySpinner,day);
        }
    }
    private void selectSpinnerItemByValue(Spinner spinner,String value){
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for(int position=0;position<adapter.getCount();position++){
            String v = adapter.getItem(position).toString();
            if(v.equals(value)){
                spinner.setSelection(position);
                return;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading");
        dialog.setCancelable(false);

        bundle = getActivity().getIntent().getBundleExtra(LoginFragment.bundleTAG);
        this.view = inflater.inflate(R.layout.fragment_account, container, false);
        this.inflater = inflater;
        this.container = container;
        if (bundle != null) {
            dialog.show();
            fbId = bundle.getString("id");
            SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.UPPREFERENCE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.ownerid), fbId);
            editor.commit();
            mStorage = FirebaseStorage.getInstance().getReference();
            retrieveDataFromFirebase(fbId);
        } else {
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


        initSpinners();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0) {
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
                    academicYear = Integer.valueOf((String) acaYearSpinner.getSelectedItem());
                    break;
                case R.id.spinnerBirthdayYear:
                    year = Integer.valueOf((String) yearSpinner.getSelectedItem());
                    break;
                case R.id.spinnerBirthdayMonth:
                    month = Integer.valueOf((String) monthSpinner.getSelectedItem());
                    break;
                case R.id.spinnerBirthdayDay:
                    day = Integer.valueOf((String) daySpinner.getSelectedItem());
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void logOut() {
        LoginManager.getInstance().logOut();
        getActivity().finish();
        Intent intent = new Intent(this.getContext(), MainActivity.class);
        startActivity(intent);
    }

    public void submit() {
        boolean check = true;
        String msg = "";
        if (foodSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Food\n";
        }
        if (acaYearSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Academic Year\n";
        }
        if (songSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Song Type\n";
        }
        if (foodSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Food Type\n";
        }
        if (yearSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Year of Birth\n";
        }
        if (monthSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Month of Birth\n";
        }
        if (daySpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Day of Birth\n";
        }
        if (sportSpinner.getSelectedItemPosition() == 0) {
            check = false;
            msg += "Please Select Favourite Sport\n";
        }
        if (!male.isPressed() && !female.isPressed()) {
            msg += "Please Select Gender of target\n";
        }
        birthday = monthSpinner.getSelectedItem() +"/"+ daySpinner.getSelectedItem()+"/"+yearSpinner.getSelectedItem();


        if (!check) {
            if (dialog.isShowing())
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
            if (!found) {
                aboutMe = etAbout.getText().toString();
                if (male.isChecked()) {
                    targetMale = 1;
                } else {
                    targetMale = 0;
                }

                if (female.isChecked()) {
                    targetFemale = 1;
                } else {
                    targetFemale = 0;
                }
            }
            verifySms();
        }
    }

    private void saving() {

        if (birthday != null) {

            age = 2017 - year;
        } else {
            age = 20;
        }
        lastLogin = new Date();

        if (!found) {
            saveImageToDatabase();
        }
        executeQuery();
    }



    private void startNewActivity() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void verifySms() {
        if (!found) {
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



    private File saveBitmap() {
        imageViewProfilePicture.setDrawingCacheEnabled(true);
        imageViewProfilePicture.buildDrawingCache();
        bitmap = imageViewProfilePicture.getDrawingCache();
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, fbId + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, fbId + ".png");
            Log.e("File Exists", "" + file + ",Bitmap= " + bitmap);
        } else {
            try {
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("File", "" + file);
        }
        return file;
    }

    private void saveToFireBase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebase.getReference();
        UserDetails newUser = new UserDetails(fbId, firstName, lastName, gender, birthday, phone, course, year, aboutMe, age, song, sport, food, 1, targetMale, targetFemale, lastLogin, photo);
        databaseReference.child("users").child(fbId).setValue(newUser);
        uploadProfilePhoto();
    }

    private void uploadProfilePhoto() {


        filepath = mStorage.child("UserPhotos").child(fbId + ".png");
        if (bitmap == null) {
            imageViewProfilePicture.setDrawingCacheEnabled(true);
            imageViewProfilePicture.buildDrawingCache();
            bitmap = imageViewProfilePicture.getDrawingCache();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = filepath.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Not able to upload photo", Toast.LENGTH_LONG).show();
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photo = taskSnapshot.getDownloadUrl().toString();
                Log.e("Photo url", photo);
                startNewActivity();
            }
        });
    }




    private void retrieveDataFromFirebase(String id) {
        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
//                    Toast.makeText(getContext(),"Cant read from databaese",Toast.LENGTH_LONG).show();
                    init();
                    downloadPhotoFromFacebook();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    mDatabase.removeEventListener(eventListener);
                } else {
                    found = true;
                    user = dataSnapshot.getValue(UserDetails.class);
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
                    Toast.makeText(getContext(), "You have registered before. Retrieving Saved History", Toast.LENGTH_LONG).show();

                    downloadProfilePhoto();

                    readFriendsFromFirebase(user.getId());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
    }

    private void readFriendsFromFirebase(String id) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(RequestFriendListAdapter.FRIENDLIST).child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        final DataSnapshot next = iterator.next();
                        SuccessFriendRequest request = next.getValue(SuccessFriendRequest.class);
                        final String friendId = request.getFriendId();
                        final String roomId = request.getRoomId();
                        FirebaseDatabase.getInstance().getReference().child("users").child(friendId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        friendCount++;
                                        UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                                        executeQuery(userDetails, roomId);
                                        downloadFriendsPhoto(userDetails);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void downloadPhotoFromFacebook() {
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
                    if (dialog.isShowing())
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
    private void downloadProfilePhoto() {
        mStorage = FirebaseStorage.getInstance().getReference();
        filepath = mStorage.child("UserPhotos").child(fbId + ".png");
        photo = filepath.getDownloadUrl().toString();
        int ONE_MEGABYTE = 1024 * 1024;
        filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = getImage(bytes);
                saveImageToDatabase(user, bitmap);
                saving();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    private void downloadFriendsPhoto(final UserDetails userDetails) {
        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("UserPhotos").child(userDetails.getId() + ".png");
            int ONE_MEGABYTE = 1024 * 1024;
            filepath.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = AccountFragment.getImage(bytes);
                    saveImageToDatabase(userDetails, bitmap);
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void executeQuery() {
        ContentValues values = new ContentValues();
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        values.put(UpDatabaseHelper.ID_COLUMN, fbId);
        values.put(UpDatabaseHelper.FIRST_NAME_COLUMN, firstName);
        values.put(UpDatabaseHelper.LAST_NAME_COLUMN, lastName);
        values.put(UpDatabaseHelper.GENDER_COLUMN, gender);
        values.put(UpDatabaseHelper.FACEBOOK_ID_COLUMN, fbId);
        values.put(UpDatabaseHelper.PROFILE_PHOTO_COLUMN, photo);
        values.put(UpDatabaseHelper.COURSE_COLUMN, course);
        values.put(UpDatabaseHelper.ACADEMIC_YEAR_COLUMN, academicYear);
        values.put(UpDatabaseHelper.ABOUT_ME_COLUMN, aboutMe);
        values.put(UpDatabaseHelper.DOB_COLUMN, birthday);
        values.put(UpDatabaseHelper.PHONE_COLUMN, phone);
        values.put(UpDatabaseHelper.VERIFIED_COLUMN, verified);
        values.put(UpDatabaseHelper.AGE_COLUMN, age);
        values.put(UpDatabaseHelper.INTEREST_1_COLUMN, song);
        values.put(UpDatabaseHelper.INTEREST_2_COLUMN, sport);
        values.put(UpDatabaseHelper.INTEREST_3_COLUMN, food);
        values.put(UpDatabaseHelper.TARGET_MALE_COLUMN, targetMale);
        values.put(UpDatabaseHelper.TARGET_FEMALE_COLUMN, targetFemale);
        values.put(UpDatabaseHelper.LAST_LOGIN_COLUMN, lastLogin.toString());
        db.insert(UpDatabaseHelper.USER_TABLE, null, values);
        db.close();
        saveToFireBase();
    }
    private void saveImageToDatabase() {
        imageViewProfilePicture.setDrawingCacheEnabled(true);
        imageViewProfilePicture.buildDrawingCache();
        bitmap = imageViewProfilePicture.getDrawingCache();
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN, fbId);
        values.put(UpDatabaseHelper.IMAGE_COLUMN, getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.insert(UpDatabaseHelper.IMAGES_TABLE, null, values);
    }
    //from firebase save into database
    private void executeQuery(UserDetails user, String roomId) {
        ContentValues values = new ContentValues();
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        values.put(UpDatabaseHelper.ID_COLUMN, user.getId());
        values.put(UpDatabaseHelper.FIRST_NAME_COLUMN, user.getFirstName());
        values.put(UpDatabaseHelper.LAST_NAME_COLUMN, user.getLastName());
        values.put(UpDatabaseHelper.GENDER_COLUMN, user.getGender());
        values.put(UpDatabaseHelper.FACEBOOK_ID_COLUMN, user.getId());
        values.put(UpDatabaseHelper.PROFILE_PHOTO_COLUMN, user.getPhoto());
        values.put(UpDatabaseHelper.COURSE_COLUMN, user.getCourse());
        values.put(UpDatabaseHelper.ACADEMIC_YEAR_COLUMN, user.getAcademicYear());
        values.put(UpDatabaseHelper.ABOUT_ME_COLUMN, user.getAboutMe());
        values.put(UpDatabaseHelper.DOB_COLUMN, user.getBirthday());
        values.put(UpDatabaseHelper.PHONE_COLUMN, user.getPhoneNumber());
        values.put(UpDatabaseHelper.VERIFIED_COLUMN, user.getIsVerified());
        values.put(UpDatabaseHelper.AGE_COLUMN, user.getAge());
        values.put(UpDatabaseHelper.INTEREST_1_COLUMN, user.getInterest1());
        values.put(UpDatabaseHelper.INTEREST_2_COLUMN, user.getInterest2());
        values.put(UpDatabaseHelper.INTEREST_3_COLUMN, user.getInterest3());
        values.put(UpDatabaseHelper.TARGET_MALE_COLUMN, user.getTargetMale());
        values.put(UpDatabaseHelper.TARGET_FEMALE_COLUMN, user.getTargetFemale());
        values.put(UpDatabaseHelper.LAST_LOGIN_COLUMN, user.getLastLogin().toString());
        db.insert(UpDatabaseHelper.USER_TABLE, null, values);

        ContentValues newValues = new ContentValues();
        newValues.put(UpDatabaseHelper.FRIEND_ID_COLUMN, user.getId());
        newValues.put(UpDatabaseHelper.FIRST_NAME_COLUMN, user.getFirstName());
        newValues.put(UpDatabaseHelper.LAST_NAME_COLUMN, user.getLastName());
        newValues.put(UpDatabaseHelper.CHATROOM_ID_COLUMN, roomId);
        db.insert(UpDatabaseHelper.FRIENDSHIP_TABLE, null, newValues);
        db.close();
    }


    private void saveImageToDatabase(UserDetails user, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN, user.getId());
        values.put(UpDatabaseHelper.IMAGE_COLUMN, getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.insert(UpDatabaseHelper.IMAGES_TABLE, null, values);
        String fullName = user.getFirstName() + " " + user.getLastName();
        db.close();
        ++friendDataDownloadedCount;
        if(friendCount == friendDataDownloadedCount){
            startNewActivity();
        }
    }


    //for face recogniztion
    public void changeProfilePicture(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        photoPickerIntent.setDataAndType(data,"image/*");

        startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.
                // the address of the image on the SD Card.
                Uri imageUri = data.getData();

                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getActivity().getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

//                    checkFace(image);
                    if(checkFace(image)){
                        //uploadProfilePhoto();
                        // show the image to the user
                        imageViewProfilePicture.setImageBitmap(image);
                    }else{
                        Toast.makeText(getActivity(),"Unable to use this Picture! Profile picture must have FACE!", Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(getActivity(),"Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean checkFace(Bitmap image) {
        Frame frame = new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        int numberOfFaceDetected = faces.size();
        Toast.makeText(getActivity(),"No.Face : "+ numberOfFaceDetected, Toast.LENGTH_SHORT).show();
        if(numberOfFaceDetected != 0){
            return true;
        }else {
            return false;
        }
    }
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

}

