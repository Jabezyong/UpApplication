package com.application.upapplication.Views;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.application.upapplication.Database.UpDatabaseHelper;
import com.facebook.Profile;

import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import static android.Manifest.permission;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by user on 12/23/2016.
 */

public class SmsFragment extends Fragment {
    private String appKey = "125fe10cbed00";
    private String appSecret = "bc73c2185d77786c192a063f63c62241";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        int readContacts = checkSelfPermission(getContext(), permission.READ_CONTACTS);
        checkPermission();
        if(readContacts != PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(getContext(),"Cannot read contacts",Toast.LENGTH_LONG).show();
        }else {
            SMSSDK.initSDK(getContext(), appKey, appSecret);
            final RegisterPage registerPage = new RegisterPage();
            registerPage.setRegisterCallback(new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                        String country = (String) phoneMap.get("country");
                        String phone = (String) phoneMap.get("phone");
//                        Toast.makeText(getContext(), phone, Toast.LENGTH_LONG).show();
                        registerUser(phone);
                    }
                }
            });
            registerPage.show(getContext());
        }
        return super.onCreateView(inflater, container, savedInstanceState);

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

    private void checkPermission() {
        int requestCode = 0;
        ArrayList<String> permissions = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23) {
            int readPhone = checkSelfPermission(getContext(), permission.READ_PHONE_STATE);
            int receiveSms = checkSelfPermission(getContext(), permission.RECEIVE_SMS);
            int readSms = checkSelfPermission(getContext(), permission.READ_SMS);
            int readContacts = checkSelfPermission(getContext(), permission.READ_CONTACTS);
            int readSdcard = checkSelfPermission(getContext(), permission.READ_EXTERNAL_STORAGE);
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 0;
                permissions.add(permission.READ_PHONE_STATE);
            }
            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 1;
                permissions.add(permission.RECEIVE_SMS);
            }
            if (readSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 2;
                permissions.add(permission.READ_SMS);
            }
            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 3;
                permissions.add(permission.READ_CONTACTS);
            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 4;
                permissions.add(permission.READ_EXTERNAL_STORAGE);
            }
            if (requestCode > 0) {
                String[] permission = new String[permissions.size()];
                this.requestPermissions(permissions.toArray(permission), requestCode);
                return;
            }
        }
    }
}
