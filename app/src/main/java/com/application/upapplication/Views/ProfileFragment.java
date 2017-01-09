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
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.application.upapplication.Controller.VolleyApplication;
import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;


import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    View view;
    ImageView imageViewProfilePicture;
    EditText etAbout;
    TextView textViewShowName,textViewShowGender,textViewShowBirthday,textViewShowPhone;
    private ArrayAdapter<CharSequence> yearAdapter,coursesAdapter,songAdapter,sportAdapter,foodAdapter;
    private Spinner sportSpinner,foodSpinner,songSpinner,courseSpinner,yearSpinner;
    private Switch switchMale,switchFemale;
    private Button btnLogOut,btnUpdate;
    String food,song,sport,course,firstName,lastName,gender,birthday,aboutMe,fbId,photo,phone,id;
    int year,age,targetMale,targetFemale,verified;
    Date lastLogin;
    ProgressDialog dialog;
    static int yes = 1;
    private Bitmap bitmap;
    UserDetails owner;
    UpDatabaseHelper helper;
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private FaceDetector faceDetector;

    public static ProfileFragment newInstance() {
        ProfileFragment profileFragment = new ProfileFragment();
        return profileFragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        readFromDatabase();

        return view;
    }
    private void readFromDatabase(){
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.UPPREFERENCE,Context.MODE_PRIVATE);
        id = preferences.getString(getString(R.string.ownerid),"");
        helper = new UpDatabaseHelper(getContext());
        owner = helper.readUser(id);
        if(owner !=null){
            initView();
        }
    }

    private void initView() {
        imageViewProfilePicture = (ImageView) view.findViewById(R.id.imageViewProfilePicture);
        Bitmap bitmap = getImage(helper.getProfilePic(id));
        imageViewProfilePicture.setImageBitmap(bitmap);

        etAbout = (EditText) view.findViewById(R.id.editTextAboutMe);

        textViewShowName = (TextView) view.findViewById(R.id.textViewShowName);
        textViewShowGender = (TextView) view.findViewById(R.id.textViewShowGender);
        textViewShowBirthday = (TextView) view.findViewById(R.id.textViewShowBirthday);
        textViewShowPhone = (TextView) view.findViewById(R.id.textViewShowPhone);

        etAbout.setText(owner.getAboutMe());
        textViewShowBirthday.setText(owner.getBirthday());
        textViewShowGender.setText(owner.getGender());
        textViewShowName.setText(owner.getFirstName()+" "+owner.getLastName());
        textViewShowPhone.setText(owner.getPhoneNumber());

        switchMale = (Switch) view.findViewById(R.id.switchMale);
        switchFemale = (Switch) view.findViewById(R.id.switchFemale);
        if(owner.getTargetMale() == yes){
            switchMale.setChecked(true);
        }
        if(owner.getTargetFemale() == yes){
            switchFemale.setChecked(true);
        }
        courseSpinner = (Spinner) view.findViewById(R.id.spinnerCourse);
        yearSpinner = (Spinner) view.findViewById(R.id.spinnerYear);
        songSpinner = (Spinner) view.findViewById(R.id.spinnerSong);
        sportSpinner = (Spinner) view.findViewById(R.id.spinnerSport);
        foodSpinner = (Spinner) view.findViewById(R.id.spinnerFood);
        imageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceDetector = new FaceDetector.Builder(getActivity().getApplicationContext()).build();
                if (!faceDetector.isOperational()) {
                    Toast.makeText(getActivity(), "Face detector dependencies are not yet available.", Toast.LENGTH_LONG).show();
                }
                changeProfilePicture();
            }
        });
        btnLogOut = (Button) view.findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        initAdapter();
    }
    private void initAdapter(){
        foodAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.food)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        sportAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.sports)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        songAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.songs)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        coursesAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.courses)){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
        };
        yearAdapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.year)){
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
        selectSpinnerItemByValue(songSpinner,owner.getInterest1());
        selectSpinnerItemByValue(sportSpinner,owner.getInterest2());
        selectSpinnerItemByValue(foodSpinner,owner.getInterest3());
        selectSpinnerItemByValue(courseSpinner,owner.getCourse());
        selectSpinnerItemByValue(yearSpinner,String.valueOf(owner.getAcademicYear()));
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
    private Bitmap getImage(byte[] data){
        byte[] hello = data;
        Bitmap b = BitmapFactory.decodeByteArray(data,0,data.length);
        return b;
    }

    public void logOut(){
        LoginManager.getInstance().logOut();
        getActivity().finish();
        Intent intent = new Intent(this.getContext(),MainActivity.class);
        startActivity(intent);
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

//                 declare a stream to read the image data from the SD Card.
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
    public void update(){
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        dialog.show();
        firstName = owner.getFirstName();
        birthday = owner.getBirthday();
        lastName = owner.getLastName();
        fbId = id;
        phone = owner.getPhoneNumber();
        age = owner.getAge();
        gender = owner.getGender();
        food = (String) foodSpinner.getSelectedItem();
        song = (String) songSpinner.getSelectedItem();
        sport = (String) sportSpinner.getSelectedItem();
        course = (String) courseSpinner.getSelectedItem();
        year = Integer.valueOf((String) yearSpinner.getSelectedItem());
        aboutMe = etAbout.getText().toString();
        if (switchMale.isChecked()) {
            targetMale = 1;
        } else {
            targetMale = 0;
        }

        if (switchFemale.isChecked()) {
            targetFemale = 1;
        } else {
            targetFemale = 0;
        }
        lastLogin = new Date();
        uploadProfilePhoto();
    }
    private void executeQuery() {
        ContentValues values = new ContentValues();
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        values.put(UpDatabaseHelper.ID_COLUMN, id);
        values.put(UpDatabaseHelper.FIRST_NAME_COLUMN, firstName);
        values.put(UpDatabaseHelper.LAST_NAME_COLUMN, lastName);
        values.put(UpDatabaseHelper.GENDER_COLUMN, gender);
        values.put(UpDatabaseHelper.FACEBOOK_ID_COLUMN, id);
        values.put(UpDatabaseHelper.PROFILE_PHOTO_COLUMN, photo);
        values.put(UpDatabaseHelper.COURSE_COLUMN, course);
        values.put(UpDatabaseHelper.ACADEMIC_YEAR_COLUMN, year);
        values.put(UpDatabaseHelper.ABOUT_ME_COLUMN, aboutMe);
        values.put(UpDatabaseHelper.DOB_COLUMN, birthday);
        values.put(UpDatabaseHelper.PHONE_COLUMN, phone);
        values.put(UpDatabaseHelper.VERIFIED_COLUMN, 1);
        values.put(UpDatabaseHelper.AGE_COLUMN, age);
        values.put(UpDatabaseHelper.INTEREST_1_COLUMN, song);
        values.put(UpDatabaseHelper.INTEREST_2_COLUMN, sport);
        values.put(UpDatabaseHelper.INTEREST_3_COLUMN, food);
        values.put(UpDatabaseHelper.TARGET_MALE_COLUMN, targetMale);
        values.put(UpDatabaseHelper.TARGET_FEMALE_COLUMN, targetFemale);
        values.put(UpDatabaseHelper.LAST_LOGIN_COLUMN, lastLogin.toString());
//        db.insert(UpDatabaseHelper.USER_TABLE, null, values);
        String[] selectionArgs = { id };
        db.update(UpDatabaseHelper.USER_TABLE,
                values,
                "facebookid = ?",
                selectionArgs);
        db.close();
        saveImageToDatabase();
    }
    private void saveImageToDatabase() {
        imageViewProfilePicture.setDrawingCacheEnabled(true);
        imageViewProfilePicture.buildDrawingCache();
        bitmap = imageViewProfilePicture.getDrawingCache();
        ContentValues values = new ContentValues();
        values.put(UpDatabaseHelper.IMAGES_ID_COLUMN, id);
        values.put(UpDatabaseHelper.IMAGE_COLUMN, AccountFragment.getBytes(bitmap));
        UpDatabaseHelper databaseHelper = new UpDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.update(UpDatabaseHelper.IMAGES_TABLE,
                values,
                UpDatabaseHelper.IMAGES_ID_COLUMN+" = ?",
                new String[]{id});
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        Toast.makeText(getContext(),"Updated",Toast.LENGTH_LONG).show();
    }
    private void saveToFireBase() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebase.getReference();
        UserDetails newUser = new UserDetails(fbId, firstName, lastName, gender, birthday, phone, course, year, aboutMe, age, song, sport, food, 1, targetMale, targetFemale, lastLogin, photo);
        databaseReference.child("users").child(fbId).setValue(newUser);
        executeQuery();
    }
    private void uploadProfilePhoto() {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filepath = mStorage.child("UserPhotos").child(fbId + ".png");
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
                saveToFireBase();
                Log.e("Photo url", photo);

            }
        });
    }
}
