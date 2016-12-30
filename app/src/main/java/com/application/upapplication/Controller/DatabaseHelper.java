package com.application.upapplication.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 12/5/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UPDATABASE";
    private static final int DATABASE_VERSION = 1;

    public static final String USER_TABLE = "Users";
    public static final String USER_FRIEND_TABLE = "UserFriends";
    public static final String TARGET_SHOWN_TABLE = "TargetShown";
    public static final String COURSE_TABLE= "Courses";
    public static final String REQUEST_FRIEND_TABLE ="RequestFriends";
    public static final String INTEREST_TABLE =" Interests";
    //For Users table
    public static final String ID_COLUMN = "userid";
    public static final String FIRST_NAME_COLUMN = "fname";
    public static final String LAST_NAME_COLUMN ="lname";
    public static final String AGE_COLUMN = "age";
    public static final String GENDER_COLUMN ="gender";
    public static final String DOB_COLUMN = "dob";
    public static final String PHONE_COLUMN ="phone";
    public static final String FACEBOOK_ID_COLUMN ="facebookid";
    public static final String PROFILE_PHOTO_COLUMN ="profile_photo";
    public static final String COURSE_COLUMN = "courseid";
    public static final String STUDY_YEAR_COLUMN ="study_year";
    public static final String ABOUT_ME_COLUMN = "aboutme";
    public static final String INTEREST_1_COLUMN = "interest1";
    public static final String INTEREST_2_COLUMN = "interest2";
    public static final String INTEREST_3_COLUMN = "interest3";
    public static final String VERIFIED_COLUMN ="veried";
    public static final String LONGTITUDE_COLUMN = "longtitude";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LAST_LOGIN_COLUMN ="lastLogin";
    //for courses table
    public static final String COURSE_ID_COLUMN ="courseid";
    public static final String COURSE_NAME_COLUMN ="coursename";
    //for request friend table
    public static final String INDEX_COLUMN ="item_index";
    public static final String FRIEND_ID_COLUMN ="friendid";
    public static final String APPROVE_COLUMN ="approved";
    //for user firned table
    public static final String FIREBASE_ID_COLUMN ="databaseid";
    //for taret shown table
    public static final String DATE_SHOW_COLUMN ="dateshow";
    //for interest table
    public static final String INTEREST_ID_COLUMN ="interstid";
    public static final String INTEREST_NAME_COLUMN ="interestname";
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE "+USER_TABLE +" (" +
                    ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    FIRST_NAME_COLUMN+" TEXT NOT NULL, "+
                    LAST_NAME_COLUMN+" TEXT NOT NULL, "+
                    AGE_COLUMN+ " INTEGER NOT NULL, "+
                    GENDER_COLUMN+ " INTEGER NOT NULL, "+
                    DOB_COLUMN+ " INTEGER NOT NULL,"+
                    PHONE_COLUMN+" INTEGER NOT NULL, "+
                    FACEBOOK_ID_COLUMN+" TEXT NOT NULL, "+
                    PROFILE_PHOTO_COLUMN+ "TEXT NOT NULL,"+
                    COURSE_COLUMN+" TEXT NOT NULL, "+
                    STUDY_YEAR_COLUMN+ " INTEGER NOT NULL," +
                    ABOUT_ME_COLUMN+" TEXT NOT NULL, "+
                    INTEREST_1_COLUMN+" INTEGER NOT NULL, "+
                    INTEREST_2_COLUMN+" INTEGER NOT NULL, "+
                    INTEREST_3_COLUMN+" INTEGER NOT NULL, "+
                    VERIFIED_COLUMN+" INTEGER NOT NULL, "+
                    LONGTITUDE_COLUMN+ " REAL, "+
                    LATITUDE_COLUMN +" REAL, "+
                    LAST_LOGIN_COLUMN+" INTEGER NOT NULL,"+
                    "FOREIGN KEY("+INTEREST_1_COLUMN+") REFERENCES"+
                    INTEREST_TABLE+"("+INTEREST_ID_COLUMN+"), "+
                    "FOREIGN KEY("+INTEREST_2_COLUMN+") REFERENCES"+
                    INTEREST_TABLE+"("+INTEREST_ID_COLUMN+"), "+
                    "FOREIGN KEY("+INTEREST_3_COLUMN+") REFERENCES"+
                    INTEREST_TABLE+"("+INTEREST_ID_COLUMN+"));";
    public static final String CREATE_USER_FRIEND_TABLE =
            "CREATE TABLE "+USER_FRIEND_TABLE+" ("+
                    INDEX_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    ID_COLUMN+ " INTEGER ,"+
                    FRIEND_ID_COLUMN + " INTEGER,"+
                    FIREBASE_ID_COLUMN+" TEXT,"+
                    "FOREIGN KEY("+ID_COLUMN+") REFERENCES "+
                    USER_TABLE+"("+ID_COLUMN+"), "+
                    "FOREIGN KEY("+FRIEND_ID_COLUMN+") REFERENCES "+
                    USER_TABLE+"("+ID_COLUMN+"));";

    public static final String CREATE_COURSES_TABLE=
            "CREATE TABLE "+COURSE_TABLE+"("+
                    COURSE_ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COURSE_NAME_COLUMN + " TEXT NOT NULL)";
    public static final String CREATE_REQUEST_FRIEND_TABLE=
            "CREATE TABLE "+REQUEST_FRIEND_TABLE+"("+
                    INDEX_COLUMN+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    ID_COLUMN+ " INTEGER ,"+
                    FRIEND_ID_COLUMN + " INTEGER, "+
                    APPROVE_COLUMN+" INTEGER NOT NULL, "+
                    "FOREIGN KEY("+ID_COLUMN+") REFERENCES "+
                    USER_TABLE+"("+ID_COLUMN+"), "+
                    "FOREIGN KEY("+FRIEND_ID_COLUMN+") REFERENCES "+
                    USER_TABLE+"("+ID_COLUMN+"));";

    public static final String CREATE_INTEREST_TABLE=
            "CREATE TABLE "+INTEREST_TABLE+"("+
                    INTEREST_ID_COLUMN+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    INTEREST_NAME_COLUMN+" TEXT NOT NULL)";
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COURSES_TABLE);
        db.execSQL(CREATE_INTEREST_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_REQUEST_FRIEND_TABLE);
        db.execSQL(CREATE_USER_FRIEND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CREATE_USER_FRIEND_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CREATE_REQUEST_FRIEND_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CREATE_USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CREATE_INTEREST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CREATE_COURSES_TABLE);
        onCreate(db);
    }
}
