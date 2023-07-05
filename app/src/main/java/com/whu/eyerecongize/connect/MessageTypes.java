package com.whu.eyerecongize.connect;

public class MessageTypes {
    public static final int HANDLER_NEW_MESSAGE = 1;
    public static final int HANDLER_CHANGE_CONNECTION_STATUS = 2;
    public static final int HANDLER_DONE_INITIALIZATION = 3;
    public static final int HANDLER_REQUEST_SEND_MESSAGE = 4;
    public static final int HANDLER_UPDATE_RECYCLER = 5;
    public static final int HANDLER_MESSAGE_FAIL = 6;
    public static final int GO_TO_SEND_MESSAGES = 0;
    public static final String ACTION_GO_TO_SEND_MESSAGES = "com.codeJustice.GO_TO_SEND_MESSAGES";
    public static final String ACTION_GO_TO_PICK_PATIENTS = "com.codeJustice.GO_TO_PICK_PATIENTS";
    public static final String NEW_MESSAGE_RECEIVED = "com.codeJustice.NEW_MESSAGE_RECEIVED";
}
