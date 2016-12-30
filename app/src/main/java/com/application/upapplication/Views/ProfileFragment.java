package com.application.upapplication.Views;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.application.upapplication.Database.UpDatabaseHelper;
import com.application.upapplication.Model.UserDetails;
import com.application.upapplication.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    View view;
    ImageView imageViewProfilePicture;
    TextView textViewShowName,textViewShowGender,textViewShowBirthday,textViewShowPhone;
    private ArrayAdapter<CharSequence> yearAdapter,coursesAdapter,songAdapter,sportAdapter,foodAdapter;
    private Spinner sportSpinner,foodSpinner,songSpinner,courseSpinner,yearSpinner;
    private Switch switchMale,switchFemale;
    String food,song,sport,course,firstName,lastName,gender,birthday,aboutMe,fbId,photo,phone,id;
    int year,age,targetMale,targetFemale,verified;
    static int yes = 1;
    private Bitmap bitmap;
    UserDetails owner;
    UpDatabaseHelper helper;
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

        textViewShowName = (TextView) view.findViewById(R.id.textViewShowName);
        textViewShowGender = (TextView) view.findViewById(R.id.textViewShowGender);
        textViewShowBirthday = (TextView) view.findViewById(R.id.textViewShowBirthday);
        textViewShowPhone = (TextView) view.findViewById(R.id.textViewShowPhone);

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
        initAdapter();
    }
    private void initAdapter(){
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
}
