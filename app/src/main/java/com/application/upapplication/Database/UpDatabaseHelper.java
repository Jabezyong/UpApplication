package com.application.upapplication.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.application.upapplication.Model.UserDetails;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/16/2016.
 */

public class UpDatabaseHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "UPDATABASE.db";
        private static final int DATABASE_VERSION = 1;

        public static final String USER_TABLE = "Users";
        public static final String USER_FRIEND_TABLE = "UserFriends";
        public static final String TARGET_SHOWN_TABLE = "TargetShown";
        public static final String COURSE_TABLE= "Courses";
        public static final String FRIENDSHIP_TABLE ="Friendship";
        public static final String INTEREST_TABLE =" Interests";
        public static final String MESSAGES_TABLE =" Messages";
        public static final String IMAGES_TABLE = "Images";
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
        public static final String ACADEMIC_YEAR_COLUMN ="academic_year";
        public static final String ABOUT_ME_COLUMN = "aboutme";
        public static final String INTEREST_1_COLUMN = "interest1";
        public static final String INTEREST_2_COLUMN = "interest2";
        public static final String INTEREST_3_COLUMN = "interest3";
        public static final String TARGET_MALE_COLUMN ="targetmale";
        public static final String TARGET_FEMALE_COLUMN = "targetfemale";
        public static final String VERIFIED_COLUMN ="verified";
        public static final String LONGTITUDE_COLUMN = "longtitude";
        public static final String LATITUDE_COLUMN = "latitude";
        public static final String LAST_LOGIN_COLUMN ="lastLogin";

        //for courses table
        public static final String COURSE_ID_COLUMN ="courseid";
        public static final String COURSE_NAME_COLUMN ="coursename";
        //for request friend table
        public static final String INDEX_COLUMN ="id";
        public static final String FRIEND_ID_COLUMN ="friendid";
        public static final String CHATROOM_ID_COLUMN = "chatroomid";
        public static final String APPROVE_COLUMN ="approved";
        //for messages table
        public static final String MESSAGEID_COLUMN="messageid";
        public static final String SENDER_COLUMN ="sender";
        public static final String RECEIVER_COLUMN="receiver";
        public static final String TEXT_COLUMN ="text";
        public static final String CONTENT_COLUMN ="content";
        public static final String CONTENT_TYPE_COLUMN ="contenttype";
        public static final String TIMESTAMP_COLUMN ="time";
        public static final String VOICE_COLUMN = "voice";
        public static final String IMAGE_COLUMN = "image";
        public static final String SEEN_COLUMN = " seen";
        //for user firned table
        public static final String FIREBASE_ID_COLUMN ="databaseid";
        //for taret shown table
        public static final String DATE_SHOW_COLUMN ="dateshow";
        //for image table
        public static final String IMAGES_ID_COLUMN ="imageid";
//        public static final String IMAGE_COLUMN = "image";
        public static final String CREATE_USERS_TABLE =
                "CREATE TABLE "+USER_TABLE +" (" +
                        ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        FIRST_NAME_COLUMN+" TEXT NOT NULL, "+
                        LAST_NAME_COLUMN+" TEXT NOT NULL, "+
                        GENDER_COLUMN+ " TEXT NOT NULL, "+
                        DOB_COLUMN+ " TEXT ,"+
                        AGE_COLUMN+ " INTEGER ," +
                        PHONE_COLUMN+" TEXT , "+
                        FACEBOOK_ID_COLUMN+" TEXT NOT NULL, "+
                        PROFILE_PHOTO_COLUMN + " TEXT , " +
                        COURSE_COLUMN+" TEXT NOT NULL, "+
                        ACADEMIC_YEAR_COLUMN+ " INTEGER NOT NULL," +
                        ABOUT_ME_COLUMN+" TEXT NOT NULL, "+
                        INTEREST_1_COLUMN+" TEXT NOT NULL, "+
                        INTEREST_2_COLUMN+" TEXT NOT NULL, "+
                        INTEREST_3_COLUMN+" TEXT NOT NULL, "+
                        VERIFIED_COLUMN+" INTEGER NOT NULL, "+
                        TARGET_MALE_COLUMN+" INTEGER NOT NULL,"+
                        TARGET_FEMALE_COLUMN + " INTEGER NOT NULL,"+
                        LONGTITUDE_COLUMN+ " REAL, "+
                        LATITUDE_COLUMN +" REAL, "+
                        LAST_LOGIN_COLUMN+" INTEGER )";
        public static final String CREATE_USER_FRIEND_TABLE =
                "CREATE TABLE "+USER_FRIEND_TABLE+" ("+
//                        INDEX_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        INDEX_COLUMN+ " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        FRIEND_ID_COLUMN + " TEXT,"+
                        FIREBASE_ID_COLUMN+" TEXT,"+
                        "FOREIGN KEY("+INDEX_COLUMN+") REFERENCES "+
                        USER_TABLE+"("+INDEX_COLUMN+"), "+
                        "FOREIGN KEY("+FRIEND_ID_COLUMN+") REFERENCES "+
                        USER_TABLE+"("+ID_COLUMN+"));";


        public static final String CREATE_FRIENDSHIP_TABLE=
                "CREATE TABLE "+FRIENDSHIP_TABLE+"("+
//                        INDEX_COLUMN+ " INTEGER PRIMARY KEY, "+
                        FRIEND_ID_COLUMN + " TEXT, "+
//                        APPROVE_COLUMN+" INTEGER NOT NULL, "+
                        FIRST_NAME_COLUMN + " TEXT,"+
                        LAST_NAME_COLUMN + " TEXT,"+
                        CHATROOM_ID_COLUMN+" TEXT, "+
                        "FOREIGN KEY("+FRIEND_ID_COLUMN+") REFERENCES "+
                        USER_TABLE+"("+FACEBOOK_ID_COLUMN+"));";
        String CREATE_MESSAGE_TABLE =  "CREATE TABLE" + MESSAGES_TABLE +
                "("+
                MESSAGEID_COLUMN + " TEXT PRIMARY KEY, " +
                CHATROOM_ID_COLUMN + " TEXT NOT NULL, " +
                SENDER_COLUMN + "  TEXT NOT NULL, " +
                RECEIVER_COLUMN + " TEXT NOT NULL, " +
                CONTENT_COLUMN + " TEXT, "+
                CONTENT_TYPE_COLUMN + " INTEGER,"+
                TIMESTAMP_COLUMN + " TEXT NOT NULL, " +
                SEEN_COLUMN  + " INTEGER, "+
                "FOREIGN KEY ("+SENDER_COLUMN+") REFERENCES "+
                USER_TABLE+"("+FACEBOOK_ID_COLUMN+")"+
                "FOREIGN KEY ("+RECEIVER_COLUMN+") REFERENCES "+
                USER_TABLE+"("+FACEBOOK_ID_COLUMN+")"+
                "FOREIGN KEY ("+CHATROOM_ID_COLUMN+") REFERENCES "+
        FRIENDSHIP_TABLE+"("+CHATROOM_ID_COLUMN+")"+
                " ) ";
        String CREATE_IMAGES_TABLE =" CREATE TABLE "+IMAGES_TABLE +
                "( "+ IMAGES_ID_COLUMN +" TEXT PRIMARY KEY, "+
                IMAGE_COLUMN +" BLOB)";

//        public static final String CREATE_INTEREST_TABLE=
//                "CREATE TABLE "+INTEREST_TABLE+"("+
//                        INTEREST_ID_COLUMN+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
//                        INTEREST_NAME_COLUMN+" TEXT NOT NULL,"+
//                        INTEREST_CATEGORY_COLUMN+" TEXT NOT NULL)";

        public UpDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            SQLiteDatabase db = this.getWritableDatabase();
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USERS_TABLE);
            db.execSQL(CREATE_FRIENDSHIP_TABLE);
            db.execSQL(CREATE_USER_FRIEND_TABLE);
            db.execSQL(CREATE_MESSAGE_TABLE);
            db.execSQL(CREATE_IMAGES_TABLE);
        }
        private void insertQuery(){

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+MESSAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+USER_FRIEND_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+FRIENDSHIP_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+USER_TABLE);
                db.execSQL("DROP TABLE IF EXISTS "+ IMAGES_TABLE);

            onCreate(db);
        }
        public static boolean x(Context context, String dbName) {
                File dbFile = context.getDatabasePath(dbName);
                return dbFile.exists();
        }
        public void deleteDatabase(Context context){
                context.deleteDatabase(DATABASE_NAME);
        }

        public UserDetails readUser(String id){
                SQLiteDatabase db = getReadableDatabase();
                String[] userColumns ={
                        FIRST_NAME_COLUMN,
                        LAST_NAME_COLUMN,
                        GENDER_COLUMN,
                        DOB_COLUMN,
                        PHONE_COLUMN,
                        FACEBOOK_ID_COLUMN,
                        COURSE_COLUMN,
                        ACADEMIC_YEAR_COLUMN,
                        ABOUT_ME_COLUMN,
                        PHONE_COLUMN,
                        INTEREST_1_COLUMN,
                        INTEREST_2_COLUMN,
                        INTEREST_3_COLUMN,
                        TARGET_MALE_COLUMN,
                        TARGET_FEMALE_COLUMN
                };
                Cursor cursor =db.query(USER_TABLE,
                        userColumns,
                        "facebookid = ?",
                        new String[]{id},
                        null,
                        null,
                        null
                );
                if(cursor!=null){
                        cursor.moveToFirst();
                }else{
                    return null;
                }
                if(cursor.getCount()>0) {
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow(FIRST_NAME_COLUMN));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow(LAST_NAME_COLUMN));
                    String gender = cursor.getString(cursor.getColumnIndexOrThrow(GENDER_COLUMN));
                    String dob = cursor.getString(cursor.getColumnIndexOrThrow(DOB_COLUMN));
                    String course = cursor.getString(cursor.getColumnIndexOrThrow(COURSE_COLUMN));
                    int year = cursor.getInt(cursor.getColumnIndexOrThrow(ACADEMIC_YEAR_COLUMN));
                    String aboutMe = cursor.getString(cursor.getColumnIndexOrThrow(ABOUT_ME_COLUMN));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(PHONE_COLUMN));
                    String song = cursor.getString(cursor.getColumnIndexOrThrow(INTEREST_1_COLUMN));
                    String sport = cursor.getString(cursor.getColumnIndexOrThrow(INTEREST_2_COLUMN));
                    String food = cursor.getString(cursor.getColumnIndexOrThrow(INTEREST_3_COLUMN));
                    int targetMale = cursor.getInt(cursor.getColumnIndexOrThrow(TARGET_MALE_COLUMN));
                    int targetFemale = cursor.getInt(cursor.getColumnIndexOrThrow(TARGET_FEMALE_COLUMN));
                    UserDetails user = new UserDetails(firstName, lastName, gender, dob, phone, course, year, aboutMe, song, sport, food, targetMale, targetFemale);
                    db.close();
                    return user;
                }else{
                    return null;
                }
        }

        public byte[] getProfilePic(String id){
            SQLiteDatabase db = getReadableDatabase();
            String[] columns = {
                IMAGES_ID_COLUMN,
                IMAGE_COLUMN
            };
            Cursor cursor = db.query(
                    IMAGES_TABLE,
                    columns,
                    IMAGES_ID_COLUMN+" = ?",
                    new String[]{id},
                    null,
                    null,
                    null
            );

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
            }else{
                return null;
            }
            byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow(IMAGE_COLUMN));
            db.close();
            return data;
        }

    public List<String> getFriendList(){
        String[] columns = {
                FRIEND_ID_COLUMN
        };
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FRIENDSHIP_TABLE,
                columns,
                null,
                null,
                null,
                null,
                null);
        if(cursor.getCount()>0){
            List<String> lists = new ArrayList<>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                String friendId = cursor.getString(cursor.getColumnIndex(FRIEND_ID_COLUMN));
                lists.add(friendId);
                cursor.moveToNext();
            }
            return lists;
        }else{
            return null;
        }
    }

    public int getTargetMale(String id){
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                TARGET_MALE_COLUMN
        };
        Cursor cursor = db.query(USER_TABLE,
                columns,
                null,
                null,
                null,
                null,
                null);
        if(cursor.getCount()>0){
            List<String> lists = new ArrayList<>();
            cursor.moveToFirst();
            int targetMale = cursor.getInt(cursor.getColumnIndex(TARGET_MALE_COLUMN));
            return targetMale;
        }else{
            return 0;
        }
    }
    public int getTargetFemale(String id){
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                TARGET_FEMALE_COLUMN
        };
        Cursor cursor = db.query(USER_TABLE,
                columns,
                null,
                null,
                null,
                null,
                null);
        if(cursor.getCount()>0){
            List<String> lists = new ArrayList<>();
            cursor.moveToFirst();
            int targetFemale = cursor.getInt(cursor.getColumnIndex(TARGET_FEMALE_COLUMN));
            return targetFemale;
        }else{
            return 0;
        }
    }
}
