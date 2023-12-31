package com.codejustice.enums;

public class MessageTypes {
    public static final int HANDLER_NEW_MESSAGE = 1;
    public static final int HANDLER_CHANGE_CONNECTION_STATUS = 2;
    public static final int HANDLER_DONE_INITIALIZATION = 3;
    public static final int HANDLER_REQUEST_SEND_MESSAGE = 4;
    public static final int HANDLER_UPDATE_RECYCLER = 5;
    public static final int HANDLER_MESSAGE_FAIL = 6;
    public static final int HANDLER_PICK_PATIENTS_RETURN_AND_SEND = 7;
    public static final int HANDLER_REFRESH_TABLE = 9;
    public static final int HANDLER_SEND_REPLY_MESSAGE = 10;
    public static final int GO_TO_SEND_MESSAGES = 0;
    public static final String INTENT_EXTRA_NEW_USER_ID = "new_user_id";
    public static final String INTENT_EXTRA_SENDER_ID = "sender_id";
    public static final String INTENT_EXTRA_notification_ID = "notification_id";
    public static final String ACTION_GO_TO_SEND_MESSAGES = "com.codeJustice.GO_TO_SEND_MESSAGES";
    public static final String ACTION_ALTER_CHAT_CONTENTS = "com.codeJustice.ACTION_ALTER_CHAT_CONTENTS";
    public static final String ACTION_GO_TO_PICK_PATIENTS = "com.codeJustice.GO_TO_PICK_PATIENTS";
    public static final String ACTION_REFRESH_FAMILY_PICKER_CONTENT = "com.codeJustice.REFRESH_FAMILY_PICKER_CONTENT";
    public static final String NEW_MESSAGE_RECEIVED = "com.codeJustice.NEW_MESSAGE_RECEIVED";
}
