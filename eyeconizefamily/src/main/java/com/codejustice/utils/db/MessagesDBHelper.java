package com.codejustice.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.codejustice.entities.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class MessagesDBHelper extends SQLiteOpenHelper {

    public static final String ID_KEY = "id";
    public static final String CONTENT_KEY = "content";
    public static final String TIME_KEY = "sendTime";



    private static final String DATABASE_NAME = "user_conversations.db";
    private SQLiteDatabase currentDatabase;

    private String currentTableName;
    private static final int DATABASE_VERSION = 1;

    public MessagesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        currentTableName = "default_table";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDefaultTable(db);
        currentDatabase = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createDefaultTable(SQLiteDatabase db){
        String createTableQuery = "CREATE TABLE IF NOT EXISTS default_table (id INTEGER primary key, content text, sendTime INTEGER)";
        db.execSQL(createTableQuery);
    }

    private void createNewTable(long hostID, long otherID) {
        SQLiteDatabase db = getWritableDatabase();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + genTableName(hostID, otherID) + " (id INTEGER, content text, sendTime INTEGER)";
        db.execSQL(createTableQuery);
    }

    private String genTableName(long hostID, long otherID) {
        return "conversation_" + hostID + "to" + otherID;
    }

    public void insertData(long senderID, String chatContent, long sendTime) {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, senderID);
        values.put(CONTENT_KEY, chatContent);
        values.put(TIME_KEY, sendTime);
        currentDatabase.insert(currentTableName, null, values);
    }
    public void insertData(ChatMessage msg) {
        ContentValues values = new ContentValues();
        values.put(ID_KEY, msg.senderID);
        values.put(CONTENT_KEY, msg.messageContent);
        values.put(TIME_KEY, msg.timestamp);
        System.out.println(currentDatabase.insert(currentTableName, null, values));
    }

    public void switchTable(long hostID, long otherID) {
        if (currentDatabase != null) {
            currentDatabase.close();
        }
        currentDatabase = getWritableDatabase();
        createNewTable(hostID, otherID);
        currentTableName = genTableName(hostID, otherID);
    }
    public Cursor getCursor(){

        String[] columns = {ID_KEY, CONTENT_KEY, TIME_KEY};
        String orderBy = "sendTime DESC"; // 按照sendTime从大到小排序
        return currentDatabase.query(currentTableName, columns, null, null, null, null, orderBy);
    }
    public List<ChatMessage> getChatMessages(){
        List<ChatMessage> result = new ArrayList<>();
        Cursor cursor = getCursor();
        if (cursor.moveToFirst()) {
            System.out.println("content detected.");
            do {
                long senderID = cursor.getLong(cursor.getColumnIndexOrThrow(ID_KEY));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_KEY));
                long sendTime = cursor.getLong(cursor.getColumnIndexOrThrow(TIME_KEY));
                ChatMessage cm = new ChatMessage(message, senderID, sendTime);
                result.add(cm);
            } while (cursor.moveToNext());
        }else{
            System.out.println("Table is empty:"+currentTableName);
        }
        cursor.close();
        return result;
    }




}
