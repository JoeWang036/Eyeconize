package com.codejustice.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ChatMessageAbstract;
import NetService.ConnectionUtils.TimeShowingMessage;
import com.codejustice.global.Global;

import java.util.ArrayList;
import java.util.List;

import NetService.MessageProtocol.ConfirmMessage;

public class MessagesDBHelper extends SQLiteOpenHelper {

    public static final String ID_KEY = "id";
    public static final String CONTENT_KEY = "content";
    public static final String TIME_KEY = "sendTime";
    public static final String SENT_KEY = "sentStatus";
    public static final String SERIAL_KEY = "messageSerial";



    private static final String DATABASE_NAME = "user_conversations.db";
    private SQLiteDatabase currentDatabase;

    private String currentTableName;
    private static final int DATABASE_VERSION = 1;

    private static MessagesDBHelper instance;

    public static MessagesDBHelper getInstance(Context context) {
        synchronized (MessagesDBHelper.class) {
            if (instance == null) {
                instance = new MessagesDBHelper(context);
            }
            return instance;
        }
    }

    public static void instantiate(Context context) {
        synchronized (MessagesDBHelper.class) {
            if (instance == null) {
                instance = new MessagesDBHelper(context);
            }
        }
    }
    private MessagesDBHelper(Context context) {
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

        String createTableQuery = "DROP TABLE IF EXISTS default_table";
        db.execSQL(createTableQuery);

        createTableQuery = "CREATE TABLE IF NOT EXISTS default_table (id INTEGER primary key, content text, sendTime INTEGER, sentStatus INTEGER, messageSerial INTEGER)";
        db.execSQL(createTableQuery);
    }

    private void createNewTable(long hostID, long otherID) {
        if (currentDatabase == null || !currentDatabase.isOpen()) {
            currentDatabase = getWritableDatabase();
        }

//        String createTableQuery = "DROP TABLE IF EXISTS " + genTableName(hostID, otherID);
//        currentDatabase.execSQL(createTableQuery);

        String createTableQuery = ("CREATE TABLE IF NOT EXISTS " + genTableName(hostID, otherID) + " (id INTEGER, content text, sendTime INTEGER, sentStatus INTEGER, messageSerial INTEGER)");
        currentDatabase.execSQL(createTableQuery);
    }

    private String genTableName(long hostID, long otherID) {
        return "conversation_" + hostID + "to" + otherID;
    }

//    public void insertData(long senderID, String chatContent, long sendTime) {
//        ContentValues values = new ContentValues();
//        values.put(ID_KEY, senderID);
//        values.put(CONTENT_KEY, chatContent);
//        values.put(TIME_KEY, sendTime);
//        currentDatabase.insert(currentTableName, null, values);
//    }
public void insertData(ChatMessage msg) {
    ContentValues values = new ContentValues();
    if (currentDatabase == null || !currentDatabase.isOpen())  {
        currentDatabase = getWritableDatabase();
    }
    if (msg.senderID != Global.selfID && !currentTableName.equals(genTableName(Global.selfID, msg.senderID))) {
        System.out.println(currentTableName);
        System.out.println(genTableName(Global.selfID, msg.senderID));
        System.out.println("switching...");
        switchTable(Global.selfID, msg.senderID);
    }
    System.out.println("inserting data.");
    values.put(ID_KEY, msg.senderID);
    values.put(CONTENT_KEY, msg.messageContent);
    values.put(TIME_KEY, msg.timestamp);
    values.put(SENT_KEY, msg.sentStatus);
    values.put(SERIAL_KEY, msg.messageSerial);
    System.out.println(currentDatabase.insertWithOnConflict(currentTableName, null, values, SQLiteDatabase.CONFLICT_IGNORE));
}


    public short getLastSerial(long otherID) {
        short res = 0;
        currentTableName = genTableName(Global.selfID, otherID);
        String[] columns = {SERIAL_KEY};
        String orderBy = "sendTime DESC"; // 按照sendTime从大到小排序
        Cursor c =  currentDatabase.query(currentTableName, columns, null, null, null, null, orderBy);
        if (c.moveToFirst()) {
            res = c.getShort(c.getColumnIndexOrThrow(SERIAL_KEY));
        }
        c.close();
        return res;
    }


    public void updateSentStatus(ConfirmMessage message) {
        short serial = message.messageSerial;
        long senderID = message.senderID;
        long sendTime = message.sendTime;
        if (currentDatabase == null || !currentDatabase.isOpen()) {
            currentDatabase = getWritableDatabase();
        }

        // 更新目标元组的sentStatus和sendTime字段
        ContentValues values = new ContentValues();
        values.put(SENT_KEY, ChatMessage.SENT);
        values.put(TIME_KEY, sendTime);

        // 构建子查询，获取TIME_KEY值最大的记录的sendTime
        String subQuery = "SELECT MAX(" + TIME_KEY + ") FROM " + currentTableName + " WHERE " + SERIAL_KEY + " = "+serial+" AND " + ID_KEY + " = "+senderID;

        // 构建更新条件，只更新TIME_KEY值最大的记录
        String whereClause = TIME_KEY + " = (" + subQuery + ") AND "+SERIAL_KEY +" = ? AND "+ID_KEY + " = ?";
        String[] whereArgs = {String.valueOf(serial), String.valueOf(senderID)};

        currentDatabase.update(currentTableName, values, whereClause, whereArgs);
    }

    public void updateSentStatusFail(long receiverID, long sendTime, short messageSerial) {
//        if (!currentTableName.equals(genTableName(Global.selfID, receiverID))) {
//            switchTable(Global.selfID, receiverID);
//        }
        if (currentDatabase == null || !currentDatabase.isOpen()) {
            currentDatabase = getWritableDatabase();
        }
        ContentValues val = new ContentValues();
        val.put(SENT_KEY, ChatMessage.FAILED);
//        String selection = SERIAL_KEY + " = ? AND " + ID_KEY  + " = ? AND " + TIME_KEY + " = ?";
//        String[] selectionArgs = {String.valueOf(messageSerial), String.valueOf(receiverID), String.valueOf(sendTime)};



        String selection = SERIAL_KEY + " = ? AND " + TIME_KEY + " = ?";
        String[] selectionArgs = {String.valueOf(messageSerial), String.valueOf(sendTime)};
        int i = currentDatabase.update(currentTableName, val, selection, selectionArgs);
    }
    public ChatMessage findUpdatedMessage(ConfirmMessage message) {
        short serial = message.messageSerial;
        long receiverID = message.receiverID;
        long sendTime = message.sendTime;
        if (currentDatabase == null || !currentDatabase.isOpen()) {
            currentDatabase = getWritableDatabase();
        }


        String[] columns = {ID_KEY, CONTENT_KEY, TIME_KEY, SENT_KEY};
        String selection = SERIAL_KEY + " = ? AND " + ID_KEY  + " = ? AND " + TIME_KEY + " = ?";
        String[] selectionArgs = {String.valueOf(serial), String.valueOf(receiverID), String.valueOf(sendTime)};

        String orderBy = TIME_KEY + " DESC"; // 按照sendTime从大到小排序

        Cursor cursor = currentDatabase.query(currentTableName, columns, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            long senderID = cursor.getLong(cursor.getColumnIndexOrThrow(ID_KEY));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_KEY));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(TIME_KEY));
            byte sentStatus = (byte) cursor.getShort(cursor.getColumnIndexOrThrow(SENT_KEY));
            ChatMessage updatedMessage = new ChatMessage(content, senderID, time, serial, sentStatus);
            cursor.close();
            return updatedMessage;
        } else {
            cursor.close();
            return null;
        }
    }
    public ChatMessage findNewestMessage(long otherID) {

        SQLiteDatabase db = getWritableDatabase();
        String tableName = genTableName(Global.selfID, otherID);
        createNewTable(Global.selfID, otherID);


        String[] columns = {ID_KEY, CONTENT_KEY, TIME_KEY, SENT_KEY, SERIAL_KEY};

        String orderBy = TIME_KEY + " DESC"; // 按照sendTime从大到小排序

        Cursor cursor = db.query(tableName, columns, null, null, null, null, orderBy);

        if (cursor.moveToFirst()) {
            long senderID = cursor.getLong(cursor.getColumnIndexOrThrow(ID_KEY));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_KEY));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(TIME_KEY));
            byte sentStatus = (byte) cursor.getShort(cursor.getColumnIndexOrThrow(SENT_KEY));
            short serial = cursor.getShort(cursor.getColumnIndexOrThrow(SERIAL_KEY));
            ChatMessage updatedMessage = new ChatMessage(content, senderID, time, serial, sentStatus);
            cursor.close();
            return updatedMessage;
        } else {
            cursor.close();
            return null;
        }
    }

    public void switchTable(long hostID, long otherID) {
        if (currentDatabase == null || !currentDatabase.isOpen())  {
            currentDatabase = getWritableDatabase();
        }
        createNewTable(hostID, otherID);
        currentTableName = genTableName(hostID, otherID);
    }
    public Cursor getCursor(){

        String[] columns = {ID_KEY, CONTENT_KEY, TIME_KEY, SERIAL_KEY, SENT_KEY};
        String orderBy = "sendTime ASC"; // 按照sendTime从大到小排序
        return currentDatabase.query(currentTableName, columns, null, null, null, null, orderBy);
    }
    public List<ChatMessageAbstract> getChatMessages(){
        switchTable(Global.selfID, Global.receiverID);
        List<ChatMessageAbstract> result = new ArrayList<>();
        Cursor cursor = getCursor();
        if (cursor.moveToFirst()) {
            int i = 1;
            long lastSendTime = 0;
            do {
                long senderID = cursor.getLong(cursor.getColumnIndexOrThrow(ID_KEY));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_KEY));
                long sendTime = cursor.getLong(cursor.getColumnIndexOrThrow(TIME_KEY));
                short messageSerial = cursor.getShort(cursor.getColumnIndexOrThrow(SERIAL_KEY));
                byte sent = (byte)cursor.getShort(cursor.getColumnIndexOrThrow(SENT_KEY));

                ChatMessage cm = new ChatMessage(message, senderID, sendTime, messageSerial, sent);
                if (sendTime - lastSendTime > 30000) {
                    System.out.println("adding...");
                    result.add(new TimeShowingMessage(sendTime));
                }
                lastSendTime = sendTime;
                result.add(cm);
            } while (cursor.moveToNext());
        }else{
            System.out.println("Table is empty:"+currentTableName);
        }
        cursor.close();
        return result;
    }




}
