package com.codejustice.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codejustice.entities.FriendEntity;

import java.util.ArrayList;
import java.util.List;

public class FriendsDBHelper extends SQLiteOpenHelper {
    public static final String ID_KEY = "id";
    public static final String PROFILE_PIC_KEY = "profilePic";
    public static final String NICKNAME_KEY = "nickName";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    public static final String LAST_CHANGE_KEY = "lastProfileChangeTime";


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "user_friends.db";

    private String currentTableName;
    private SQLiteDatabase currentDatabase;

    private static FriendsDBHelper instance;


    public static FriendsDBHelper getInstance(Context context) {
        synchronized (FriendsDBHelper.class) {
            if (instance == null) {
                instance = new FriendsDBHelper(context);
            }
            return instance;
        }
    }

    public static void instantiate(Context context) {
        instance = new FriendsDBHelper(context);
    }
    private FriendsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        currentTableName = "default_table";

    }
    private FriendsDBHelper(Context context, long userID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        currentTableName = genTableName(userID);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void createDefaultTable(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS default_table (id INTEGER primary key, nickName TEXT, phoneNumber TEXT, profilePic TEXT, lastProfileChangeTime INTEGER)";
        db.execSQL(createTableQuery);
    }

    private void createNewTable(long userID) {
        SQLiteDatabase db = getWritableDatabase();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + genTableName(userID) + " (id INTEGER primary key, nickName TEXT, phoneNumber TEXT, profilePic TEXT, lastProfileChangeTime INTEGER)";
        db.execSQL(createTableQuery);
    }

    private String genTableName(long userID) {
        return "friendsOf_" + userID;
    }

    public void insertData(long id, String nickname,
                           String phoneNum, String picLocation,
                           long picLastChangeTime) {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, id);
        values.put(NICKNAME_KEY, nickname);
        values.put(PHONE_NUMBER_KEY, phoneNum);
        values.put(PROFILE_PIC_KEY, picLocation);
        values.put(LAST_CHANGE_KEY, picLastChangeTime);
        currentDatabase.insert(currentTableName, null, values);
    }

    public void insertData(FriendEntity friend) {

        ContentValues values = new ContentValues();
        values.put(ID_KEY, friend.friendID);
        values.put(NICKNAME_KEY, friend.friendName);
        values.put(PHONE_NUMBER_KEY, friend.phoneNumber);
        values.put(PROFILE_PIC_KEY, friend.profilePicLocation);
        values.put(LAST_CHANGE_KEY, friend.lastChangeProfileTime);
        currentDatabase.insert(currentTableName, null, values);

    }

    public String getFriendNicknameByID(long friendID, long userID) {
        String nickname = null;
        createNewTable(userID);
        currentDatabase = getWritableDatabase();
        Cursor cursor = currentDatabase.query(genTableName(userID), new String[]{NICKNAME_KEY},
                ID_KEY + "=?", new String[]{String.valueOf(friendID)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            nickname = cursor.getString(cursor.getColumnIndexOrThrow(NICKNAME_KEY));
            cursor.close();
        }
        return nickname;
    }

    public void switchTable(long userID) {
        if (currentDatabase != null) {
            currentDatabase.close();
        }
        currentDatabase = getWritableDatabase();
        createNewTable(userID);
        currentTableName = genTableName(userID);
    }

    public Cursor getCursor() {
        String[] columns = {ID_KEY, NICKNAME_KEY, PHONE_NUMBER_KEY, PROFILE_PIC_KEY, LAST_CHANGE_KEY};
        return currentDatabase.query(currentTableName, columns, null, null, null, null, null);
    }

    public List<FriendEntity> getFriends(){
        List<FriendEntity> result = new ArrayList<>();
        Cursor cursor = getCursor();
        if (cursor.moveToFirst()) {
            do {
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(ID_KEY));
                String nickName = cursor.getString(cursor.getColumnIndexOrThrow(NICKNAME_KEY));
                String phoneNum = cursor.getString(cursor.getColumnIndexOrThrow(PHONE_NUMBER_KEY));
                String profilePicLocation = cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_PIC_KEY));
                long lastChangeTime = cursor.getLong(cursor.getColumnIndexOrThrow(LAST_CHANGE_KEY));
                FriendEntity friend = new FriendEntity(userId, nickName, phoneNum, profilePicLocation, lastChangeTime);
                result.add(friend);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }




}
